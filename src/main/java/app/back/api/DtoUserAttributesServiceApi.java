package app.back.api;

import app.back.dto.UserAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DtoUserAttributesServiceApi extends AbstractDtoServiceApi<UserAttributes> {

    Optional<UserAttributes> findByDiscordId(Long id);

    List<UserAttributes> findByDiscordId(List<Long> idList);

    Optional<UserAttributes> findByKeycloakId(UUID userId);

    List<UserAttributes> findByKeycloakIds(List<UUID> userIds);

}
