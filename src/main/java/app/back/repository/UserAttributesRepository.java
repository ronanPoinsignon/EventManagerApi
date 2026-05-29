package app.back.repository;

import app.back.dto.UserAttributes;
import app.back.entityname.EntityTable;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAttributesRepository extends AbstractEntityRepository<UserAttributes> {

    Optional<UserAttributes> findByDiscordId(Long discordId);

    @NativeQuery("select * from " + EntityTable.USER_ATTRIBUTES + " where id in (?1)")
    List<UserAttributes> findByDiscordId(List<Long> discordId);

    @NativeQuery("select * from " +  EntityTable.USER_ATTRIBUTES + " where keycloak_user_id = ?1")
    Optional<UserAttributes> findByKeycloakUserId(String keycloakUserId);

    @NativeQuery("select * from " +  EntityTable.USER_ATTRIBUTES + " where keycloak_user_id in ?1")
    List<UserAttributes> findByKeycloakUserIds(List<String> keycloakUserIds);

}
