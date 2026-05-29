package app.back.api;

import app.back.dto.KeycloakUser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KeycloakUserServiceApi {

    List<KeycloakUser> getUsers();

    Optional<KeycloakUser> getUserById(UUID userId);

    LinkedHashMap<String, String> impersonate(String keycloakUserId);
}
