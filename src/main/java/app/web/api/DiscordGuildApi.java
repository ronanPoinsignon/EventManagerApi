package app.web.api;

import app.web.pojo.PojoDiscordGuild;

public interface DiscordGuildApi extends AbstractServiceApi<PojoDiscordGuild> {

    PojoDiscordGuild setGuildCommunicationChannel(String guildId, String channelId);

    String getGuildCommunicationChannel(String guildId);
}
