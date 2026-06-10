package app.web.pojo;

public class PojoDiscordGuild extends PojoEntity {

    private String discordGuildId;
    private String discordCommunicationChannel;

    public String getDiscordGuildId() {
        return discordGuildId;
    }

    public void setDiscordGuildId(String discordGuildId) {
        this.discordGuildId = discordGuildId;
    }

    public String getDiscordCommunicationChannel() {
        return discordCommunicationChannel;
    }

    public void setDiscordCommunicationChannel(String discordCommunicationChannel) {
        this.discordCommunicationChannel = discordCommunicationChannel;
    }

}
