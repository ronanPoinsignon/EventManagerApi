package app.web.transform;

import app.back.dto.UserAttributes;
import app.web.pojo.PojoUserAttributes;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public class TransformMember extends AbstractTransform<UserAttributes, PojoUserAttributes> {

    @Override
    protected UserAttributes from(@Nonnull PojoUserAttributes pojo) {
        var user = super.from(pojo);
        user.setDiscordId(pojo.getDiscordId());
        user.setKeycloakUserId(pojo.getKeycloakUserId());

        return user;
    }

    @Override
    protected PojoUserAttributes from(@Nonnull UserAttributes dto) {
        var user = super.from(dto);
        user.setDiscordId(dto.getDiscordId());
        user.setKeycloakUserId(dto.getKeycloakUserId());

        return user;
    }

    @Override
    protected UserAttributes createDto() {
        return new UserAttributes();
    }

    @Override
    protected PojoUserAttributes createPojo() {
        return new PojoUserAttributes();
    }

}
