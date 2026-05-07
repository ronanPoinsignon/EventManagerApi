package app.back.api;

import app.back.dto.KeycloakUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KeycloakServiceApi {

    List<KeycloakUser> getUsers();

    Optional<KeycloakUser> getUserById(UUID userId);

}
