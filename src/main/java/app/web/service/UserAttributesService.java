package app.web.service;

import app.back.api.DtoUserAttributesServiceApi;
import app.back.dto.UserAttributes;
import app.back.security.UserService;
import app.web.api.UserAttributesServiceApi;
import app.web.pojo.PojoUserAttributes;
import app.web.transform.TransformMember;
import org.springframework.stereotype.Service;

@Service
public class UserAttributesService extends AbstractService<UserAttributes, PojoUserAttributes, DtoUserAttributesServiceApi> implements UserAttributesServiceApi {

    private final UserService userService;

    public UserAttributesService(DtoUserAttributesServiceApi service, TransformMember transformMember, UserService userService) {
        super(service, transformMember);
        this.userService = userService;
    }

    @Override
    public PojoUserAttributes findByDiscordId(long discordId) {
        return getService().findByDiscordId(discordId)
                .map(getTransform()::toPojo)
                .orElse(null);
    }

    @Override
    public PojoUserAttributes save(PojoUserAttributes pojo) {
        if(pojo == null) {
            return null;
        }

        var result = getTransform().toDto(pojo);
        return getTransform().toPojo(result);
    }

    @Override
    public PojoUserAttributes linkDiscordId(long id) {
        var userId = userService.getUser().getUserId();
        var attributes = getService().findByKeycloakId(userId).orElseGet(() -> {
            var userAttributes = new UserAttributes();
            userAttributes.setKeycloakUserId(userId);

            return userAttributes;
        });
        attributes.setDiscordId(id);

        return getTransform().toPojo(getService().save(attributes));
    }
}
