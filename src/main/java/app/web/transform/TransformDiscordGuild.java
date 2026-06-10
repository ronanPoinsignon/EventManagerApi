package app.web.transform;

import app.back.dto.DiscordGuild;
import app.web.pojo.PojoDiscordGuild;
import org.springframework.stereotype.Service;

@Service
public class TransformDiscordGuild implements Transform<DiscordGuild, PojoDiscordGuild> {

    @Override
    public DiscordGuild toDto(PojoDiscordGuild pojo) {
        var discordGuild = new DiscordGuild();
        discordGuild.setId(pojo.getId());
        discordGuild.setDiscordGuildId(pojo.getDiscordGuildId());
        discordGuild.setGuildCommunicationChannel(pojo.getDiscordCommunicationChannel());

        return discordGuild;
    }

    @Override
    public PojoDiscordGuild toPojo(DiscordGuild dto) {
        var pojo = new PojoDiscordGuild();
        pojo.setId(dto.getId());
        pojo.setDiscordGuildId(dto.getDiscordGuildId());
        pojo.setDiscordCommunicationChannel(dto.getGuildCommunicationChannel());

        return pojo;
    }

}
