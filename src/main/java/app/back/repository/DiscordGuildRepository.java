package app.back.repository;

import app.back.dto.DiscordGuild;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscordGuildRepository extends AbstractEntityRepository<DiscordGuild> {

    Optional<DiscordGuild> findByDiscordGuildId(String discordGuildId);

}
