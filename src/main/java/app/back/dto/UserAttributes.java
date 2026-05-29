package app.back.dto;

import app.back.entityname.Contrainte;
import app.back.entityname.EntityTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = EntityTable.USER_ATTRIBUTES)
public class UserAttributes extends AbstractEntity {

    @Column(name = Contrainte.USER_ATTRIBUTES_DISCORD_ID, nullable = false, unique = true)
    private Long discordId;
    @Column(name = Contrainte.USER_ATTRIBUTE_KEYCLOAK_USER_ID, nullable = false, unique = true)
    private UUID keycloakUserId;

    public Long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(Long discordId) {
        this.discordId = discordId;
    }

    public UUID getKeycloakUserId() {
        return keycloakUserId;
    }

    public void setKeycloakUserId(UUID keycloakUserId) {
        this.keycloakUserId = keycloakUserId;
    }
}
