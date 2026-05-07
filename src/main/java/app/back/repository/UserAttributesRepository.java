package app.back.repository;

import app.back.dto.UserAttributes;
import app.back.entityname.EntityTable;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAttributesRepository extends AbstractEntityRepository<UserAttributes> {

    Optional<UserAttributes> findByDiscordId(Long discordId);

    @NativeQuery("select * from " + EntityTable.USER_ATTRIBUTES + " where id in (?1)")
    List<UserAttributes> findByDiscordId(List<Long> discordId);

    Optional<UserAttributes> findByKeycloakUserId(UUID keycloakUserId);

    @NativeQuery("select * from " +  EntityTable.USER_ATTRIBUTES + " where keycloakUserId in ?1")
    List<UserAttributes> findByKeycloakUserIds(List<UUID> keycloakUserIds);

}
