package app.back.repository;

import app.back.dto.DiscordMember;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscordMemberRepository extends AbstractEntityRepository<DiscordMember> {

    Optional<DiscordMember> findByNickname(String nickname);
    Optional<DiscordMember> findByDiscordId(long discorId);

}
