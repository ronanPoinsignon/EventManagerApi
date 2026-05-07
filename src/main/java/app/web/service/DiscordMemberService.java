package app.web.service;

import app.back.api.DtoUserAttributesServiceApi;
import app.back.dto.UserAttributes;
import app.web.api.DiscordMemberServiceApi;
import app.web.pojo.PojoUserAttributes;
import app.web.transform.TransformMember;
import org.springframework.stereotype.Service;

@Service
public class DiscordMemberService extends AbstractService<UserAttributes, PojoUserAttributes, DtoUserAttributesServiceApi> implements DiscordMemberServiceApi {

    public DiscordMemberService(DtoUserAttributesServiceApi service, TransformMember transformMember) {
        super(service, transformMember);
    }

    @Override
    public PojoUserAttributes findByDiscordId(long discordId) {
        return getService().findByDiscordId(discordId)
                .map(getTransform()::toPojo)
                .orElse(null);
    }
}
