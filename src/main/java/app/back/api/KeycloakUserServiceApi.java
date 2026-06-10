package app.back.api;

import app.back.dto.KeycloakUser;

import java.util.*;

public interface KeycloakUserServiceApi {

    List<KeycloakUser> getUsers();

    Optional<KeycloakUser> getUserById(UUID userId);

    default List<KeycloakUser> getUsersById(Collection<UUID> userIds) {
        if(userIds == null) {
            return List.of();
        }

        return userIds.parallelStream()
                .map(this::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    LinkedHashMap<String, String> impersonate(String keycloakUserId);
}
