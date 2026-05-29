package app.web.controller.user;

import app.back.api.KeycloakUserServiceApi;
import app.web.pojo.PojoKeycloakUserAttributes;
import app.web.transform.TransformKeycloakUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final KeycloakUserServiceApi keycloakUserService;
    private final TransformKeycloakUser transformKeycloakUser;

    public UserController(KeycloakUserServiceApi keycloakUserService, TransformKeycloakUser transformKeycloakUser) {
        this.keycloakUserService = keycloakUserService;
        this.transformKeycloakUser = transformKeycloakUser;
    }

    @GetMapping("/")
    public List<PojoKeycloakUserAttributes> findUsers() {
        return keycloakUserService.getUsers().stream().map(transformKeycloakUser::toPojoWithAttributes).toList();
    }

}
