package app.web.service;

import app.back.dto.DiscordMember;
import app.back.repository.DiscordMemberRepository;
import app.back.service.DtoDiscordMemberService;
import app.web.api.DiscordMemberServiceApi;
import app.web.pojo.PojoDiscordMember;
import app.web.transform.TransformMember;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class DiscordMemberService extends AbstractService<DiscordMember, PojoDiscordMember, @NonNull DiscordMemberRepository, DtoDiscordMemberService> implements DiscordMemberServiceApi {

    public DiscordMemberService(DtoDiscordMemberService service, TransformMember transformMember) {
        super(service, transformMember);
    }

    public PojoDiscordMember findByNickname(String nickname) {
        return getService().findByNickname(nickname)
                .map(getTransform()::toPojo)
                .orElse(null);
    }
}
