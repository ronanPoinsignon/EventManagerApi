package app.web.filter;

import app.back.api.KeycloakUserServiceApi;
import app.back.repository.UserAttributesRepository;
import app.web.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class DiscordBridgeFilter extends OncePerRequestFilter {

    private static final String DISCORD_CLIENT_ID = "pladonf_discord_bot";

    private static final String DISCORD_HEADER = "X-Discord-User-Id";

    private final KeycloakUserServiceApi keycloakUserService;
    private final JwtDecoder jwtDecoder;
    private final UserAttributesRepository userRepository;
    private final HandlerExceptionResolver resolver;

    public DiscordBridgeFilter(KeycloakUserServiceApi keycloakUserService, JwtDecoder jwtDecoder, UserAttributesRepository userRepository, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.keycloakUserService = keycloakUserService;
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwt = jwtAuth.getToken();
        var clientId = jwt.getClaimAsString("azp");
        if (!DISCORD_CLIENT_ID.equals(clientId)) {
            // si le client n'est pas le bot, on garde l'authentification classique
            filterChain.doFilter(request, response);
            return;
        }

        var discordUserId = request.getHeader(DISCORD_HEADER);
        if (discordUserId == null || discordUserId.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Missing X-Discord-User-Id header"
            );
            return;
        }

        var userAttributes = userRepository
                .findByDiscordId(Long.parseLong(discordUserId))
                .orElse(null);

        if (userAttributes == null) {
            resolver.resolveException(
                    request,
                    response,
                    null,
                    new UnauthorizedException("Utilisateur discord non reconnu.")
            );
            return;
        }

        var keycloakUserId = userAttributes.getKeycloakUserId();
        var userTokenInformations = keycloakUserService.impersonate(keycloakUserId);
        var userJwt = jwtDecoder.decode(userTokenInformations.get("access_token"));
        UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken(userJwt, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);

        filterChain.doFilter(request, response);
    }
}