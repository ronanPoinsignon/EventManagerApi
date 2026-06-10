package app.back.service;

import app.back.dto.DiscordGuild;
import app.back.entityname.EntityTable;
import app.back.exception.BackNotFoundException;
import app.back.repository.DiscordGuildRepository;
import org.springframework.stereotype.Service;

@Service
public class DtoDiscordGuildService extends DtoAbstractEntityService<DiscordGuild, DiscordGuildRepository> {

    protected DtoDiscordGuildService(DiscordGuildRepository repository) {
        super(repository);
    }

    @Override
    protected void update(DiscordGuild entityToSave, DiscordGuild dbEntity) {
        dbEntity.setDiscordGuildId(entityToSave.getDiscordGuildId());
        dbEntity.setGuildCommunicationChannel(entityToSave.getGuildCommunicationChannel());
    }

    @Override
    public String getTableName() {
        return EntityTable.DISCORD_GUILD;
    }

    public DiscordGuild setGuildCommunicationChannel(String guildId, String channelId) {
        var guild = this.repository.findByDiscordGuildId(guildId).orElseGet(() -> {
            var newGuild = new DiscordGuild();
            newGuild.setDiscordGuildId(guildId);

            return newGuild;
        });
        guild.setGuildCommunicationChannel(channelId);

        return this.save(guild);
    }

    public String getGuildCommunicationChannel(String guildId) {
        var guild = repository.findByDiscordGuildId(guildId).orElseThrow(() -> new BackNotFoundException("Aucun serveur trouvé pour cet id."));
        return guild.getGuildCommunicationChannel();
    }
}
