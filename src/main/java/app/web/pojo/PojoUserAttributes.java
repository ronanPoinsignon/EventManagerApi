package app.web.pojo;

import java.util.UUID;

public class PojoUserAttributes extends PojoEntity {

    private long discordId;
    private UUID keycloakUserId;

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public UUID getKeycloakUserId() {
        return keycloakUserId;
    }

    public void setKeycloakUserId(UUID keycloakUserId) {
        this.keycloakUserId = keycloakUserId;
    }
}
