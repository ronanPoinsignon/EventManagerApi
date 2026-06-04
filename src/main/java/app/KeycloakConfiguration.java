package app;

import app.web.filter.DiscordBridgeFilter;
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
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class KeycloakConfiguration {

    @Value("${keycloak.internal-server-url}")
    private String keycloakInternalUrl;
    @Value("${KEYCLOAK_REALM}")
    private String keycloakRealmValue;

    @Autowired
    @Lazy
    private DiscordBridgeFilter discordBridgeFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.cors(_ -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .addFilterAfter(discordBridgeFilter, BearerTokenAuthenticationFilter.class);;

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(keycloakInternalUrl + "/realms/" + keycloakRealmValue + "/protocol/openid-connect/certs").build();
    }

}
