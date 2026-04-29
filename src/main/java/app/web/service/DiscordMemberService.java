package app.web.service;

import app.back.dto.DiscordMember;
import app.back.service.DtoDiscordMemberService;
import app.web.api.DiscordMemberServiceApi;
import app.web.pojo.PojoDiscordMember;
import app.web.transform.TransformMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscordMemberService extends AbstractService<DiscordMember, PojoDiscordMember, DtoDiscordMemberService> implements DiscordMemberServiceApi {

    public DiscordMemberService(DtoDiscordMemberService service, TransformMember transformMember) {
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
