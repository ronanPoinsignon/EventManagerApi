package app.configuration;

import app.web.filter.DiscordBridgeFilter;
import app.web.handler.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class KeycloakConfiguration {

    @Value("${keycloak.internal-server-url}")
    private String keycloakInternalUrl;
    @Value("${KEYCLOAK_REALM}")
    private String keycloakRealmValue;

    @Autowired
    @Lazy
    private DiscordBridgeFilter discordBridgeFilter;

    @Autowired
    private GlobalExceptionHandler exceptionHandler;

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {

            var detail = exceptionHandler.handleException(authException, request);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            new ObjectMapper().writeValue(response.getOutputStream(), detail);
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.cors(_ -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()).authenticationEntryPoint(authenticationEntryPoint()))
                .addFilterAfter(discordBridgeFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(keycloakInternalUrl + "/realms/" + keycloakRealmValue + "/protocol/openid-connect/certs").build();
    }

}
