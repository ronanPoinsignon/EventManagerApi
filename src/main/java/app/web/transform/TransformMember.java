package app.web.transform;

import app.back.dto.DiscordMember;
import app.web.pojo.PojoDiscordMember;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public class TransformMember extends AbstractTransform<DiscordMember, PojoDiscordMember> {

    @Override
    protected DiscordMember from(@Nonnull PojoDiscordMember pojo) {
        var member = super.from(pojo);
        member.setDiscordId(pojo.getDiscordId());
        member.setNickname(pojo.getNickname());
        member.setFirstname(pojo.getFirstname());

        return member;
    }

    @Override
    protected PojoDiscordMember from(@Nonnull DiscordMember dto) {
        var member = super.from(dto);
        member.setNickname(dto.getNickname());
        member.setFirstname(dto.getFirstname());
        member.setDiscordId(dto.getDiscordId());

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
