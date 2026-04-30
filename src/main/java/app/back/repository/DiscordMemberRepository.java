package app.back.repository;

import app.back.dto.DiscordMember;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscordMemberRepository extends AbstractEntityRepository<DiscordMember> {

    Optional<DiscordMember> findByNickname(String nickname);
    Optional<DiscordMember> findByDiscordId(Long discordId);

    @NativeQuery("select * from discord_members where id in (?1)")
    List<DiscordMember> findByDiscordId(List<Long> discordId);

}
