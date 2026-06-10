package app.back.dto;

import app.back.entityname.Contrainte;
import app.back.entityname.EntityTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = EntityTable.DISCORD_GUILD)
public class DiscordGuild extends AbstractEntity {

    @Column(name = Contrainte.DISCORD_GUILD_ID, unique = true)
    private String discordGuildId;
    @Column(name = "channel_message_id")
    private String guildCommunicationChannel;

    public String getDiscordGuildId() {
        return discordGuildId;
    }

    public void setDiscordGuildId(String discordGuildId) {
        this.discordGuildId = discordGuildId;
    }

    public String getGuildCommunicationChannel() {
        return guildCommunicationChannel;
    }

    public void setGuildCommunicationChannel(String guildCommunicationChannel) {
        this.guildCommunicationChannel = guildCommunicationChannel;
    }
}
