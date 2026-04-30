package app.web.service;

import app.back.api.DtoDiscordMemberServiceApi;
import app.back.dto.DiscordMember;
import app.web.api.DiscordMemberServiceApi;
import app.web.pojo.PojoDiscordMember;
import app.web.transform.TransformMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscordMemberService extends AbstractService<DiscordMember, PojoDiscordMember, DtoDiscordMemberServiceApi> implements DiscordMemberServiceApi {

    public DiscordMemberService(DtoDiscordMemberServiceApi service, TransformMember transformMember) {
        super(service, transformMember);
    }

    @Transactional
    public PojoDiscordMember findByNickname(String nickname) {
        return getService().findByNickname(nickname)
                .map(getTransform()::toPojo)
                .orElse(null);
    }

    @Override
    public PojoDiscordMember findByDiscordId(long discordId) {
        return getService().findByDiscordId(discordId)
                .map(getTransform()::toPojo)
                .orElse(null);
    }
}
