package app.web.transform;

import app.back.api.DtoUserAttributesServiceApi;
import app.back.dto.KeycloakUser;
import app.web.pojo.PojoUser;
import app.web.pojo.PojoKeycloakUserAttributes;
import org.springframework.stereotype.Service;

@Service
public class TransformKeycloakUser implements Transform<KeycloakUser, PojoUser> {

    private final DtoUserAttributesServiceApi dtoUserAttributesServiceApi;

    public TransformKeycloakUser(DtoUserAttributesServiceApi dtoUserAttributesServiceApi) {
        this.dtoUserAttributesServiceApi = dtoUserAttributesServiceApi;
    }

    @Override
    public KeycloakUser toDto(PojoUser pojo) {
        if(pojo == null) {
            return null;
        }

        return null;
    }

    @Override
    public PojoUser toPojo(KeycloakUser dto) {
        if(dto == null) {
            return null;
        }

        return toPojo(dto, new PojoUser());
    }

    private <T extends PojoUser> T toPojo(KeycloakUser kcUser, T pojoUser) {
        pojoUser.setId(kcUser.getId());
        pojoUser.setFirstName(kcUser.getFirstName());
        pojoUser.setUsername(kcUser.getUsername());
        pojoUser.setLastName(kcUser.getLastName());

        return pojoUser;
    }

    public PojoKeycloakUserAttributes toPojoWithAttributes(KeycloakUser dto) {
        var user = toPojo(dto, new PojoKeycloakUserAttributes());
        var attributesOptional = dtoUserAttributesServiceApi.findByKeycloakId(dto.getId());
        attributesOptional.ifPresent(attributes -> user.setDiscordId(attributes.getDiscordId()));

        return user;
    }
}
