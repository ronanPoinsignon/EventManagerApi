package app.web.service;

import app.back.dto.DiscordGuild;
import app.back.service.DtoDiscordGuildService;
import app.web.api.DiscordGuildApi;
import app.web.pojo.PojoDiscordGuild;
import app.web.transform.TransformDiscordGuild;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscordGuildService extends AbstractService<DiscordGuild, PojoDiscordGuild, DtoDiscordGuildService> implements DiscordGuildApi {

    public DiscordGuildService(DtoDiscordGuildService service, TransformDiscordGuild transform) {
        super(service, transform);
    }

    @Transactional
    @Override
    public PojoDiscordGuild setGuildCommunicationChannel(String guildId, String channelId) {
        var guild =  getService().setGuildCommunicationChannel(guildId, channelId);
        return getTransform().toPojo(guild);
    }

    @Transactional
    @Override
    public String getGuildCommunicationChannel(String guildId) {
        return getService().getGuildCommunicationChannel(guildId);
    }
}
