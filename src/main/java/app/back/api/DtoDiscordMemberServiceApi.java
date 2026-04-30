package app.back.api;

import app.back.dto.DiscordMember;

import java.util.List;
import java.util.Optional;

public interface DtoDiscordMemberServiceApi extends AbstractDtoServiceApi<DiscordMember> {

    Optional<DiscordMember> findByDiscordId(Long id);

    List<DiscordMember> findByDiscordId(List<Long> idList);

    Optional<DiscordMember> findByNickname(String nickname);

}
