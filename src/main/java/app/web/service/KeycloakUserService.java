package app.web.service;

import app.back.api.KeycloakUserServiceApi;
import app.web.exception.NotFoundException;
import app.web.pojo.PojoKeycloakUserAttributes;
import app.web.transform.TransformKeycloakUser;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class KeycloakUserService {

    private final TransformKeycloakUser transformKeycloakUser;
    private final KeycloakUserServiceApi keycloakUserServiceApi;

    public KeycloakUserService(TransformKeycloakUser transformKeycloakUser, KeycloakUserServiceApi keycloakUserServiceApi) {
        this.transformKeycloakUser = transformKeycloakUser;
        this.keycloakUserServiceApi = keycloakUserServiceApi;
    }

    public PojoKeycloakUserAttributes findById(UUID id) {
        var user = keycloakUserServiceApi.getUserById(id).orElseThrow(() -> new NotFoundException("Aucun utilisateur trouvé."));
        return transformKeycloakUser.toPojoWithAttributes(user);
    }

    public List<PojoKeycloakUserAttributes> findByIds(Collection<UUID> idCollection) {
        return keycloakUserServiceApi.getUsersById(idCollection).stream().map(transformKeycloakUser::toPojoWithAttributes).toList();
    }

    public List<PojoKeycloakUserAttributes> getUsers() {
        return keycloakUserServiceApi.getUsers().stream().map(transformKeycloakUser::toPojoWithAttributes).toList();
    }
}
