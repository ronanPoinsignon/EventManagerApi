package app.web.transform;

import app.back.dto.DiscordMember;
import jakarta.annotation.Nonnull;
import app.web.pojo.PojoDiscordMember;
import org.springframework.stereotype.Service;

@Service
public class TransformMember extends AbstractTransform<DiscordMember, PojoDiscordMember> {

    @Override
    protected DiscordMember from(@Nonnull PojoDiscordMember pojo) {
        var member = new DiscordMember();
        member.setDiscordId(pojo.getId());
        member.setNickname(pojo.getNickname());
        member.setFirstname(pojo.getFirstname());

        return member;
    }

    @Override
    protected PojoDiscordMember from(@Nonnull DiscordMember dto) {
        var member = new PojoDiscordMember();
        member.setNickname(dto.getNickname());
        member.setFirstname(dto.getFirstname());
        member.setId(dto.getDiscordId());

        return member;
    }

    @Override
    protected DiscordMember createDto() {
        return new DiscordMember();
    }

    @Override
    protected PojoDiscordMember createPojo() {
        return new PojoDiscordMember();
    }

}
