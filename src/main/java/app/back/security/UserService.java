package app.back.security;

import app.back.exception.BackForbiddenException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserServiceApi {

    @Override
    public User getUser() {
        var jwt = (Jwt) Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getPrincipal();

        if(jwt == null || jwt.getId() == null || jwt.getId().isBlank()) {
            throw new BackForbiddenException("Utilisateur non connecté");
        }

        var user = new User();
        user.setUserId(UUID.fromString(jwt.getSubject()));
        var claims = jwt.getClaims();
        user.setPrenom((String) claims.get("given_name"));
        user.setNom((String) claims.get("family_name"));
        if(claims.containsKey("realm_access")) {
            user.setRoles(((Map<String, List<String>>) claims.get("realm_access")).getOrDefault("roles", new ArrayList<>()));
        }
        return user;
    }
}
