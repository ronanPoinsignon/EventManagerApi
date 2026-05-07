package app.serviceprimary;

import app.back.api.KeycloakServiceApi;
import app.back.dto.KeycloakUser;
import app.back.security.UserServiceApi;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
public class KeycloakServiceTest implements KeycloakServiceApi {

    private final UserServiceApi userServiceApi;

    public KeycloakServiceTest(UserServiceApi userServiceApi) {
        this.userServiceApi = userServiceApi;
    }

    @Override
    public List<KeycloakUser> getUsers() {
        var user = userServiceApi.getUser();
        var keycloakUser = new KeycloakUser();
        keycloakUser.setId(user.getUserId());
        keycloakUser.setFirstName(user.getPrenom());
        keycloakUser.setLastName(user.getNom());

        return List.of(keycloakUser);
    }

    @Override
    public Optional<KeycloakUser> getUserById(UUID userId) {
        return getUsers().stream().filter(user -> user.getId().equals(userId)).findFirst();
    }
}
