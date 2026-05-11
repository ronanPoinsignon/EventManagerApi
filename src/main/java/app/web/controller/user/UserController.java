package app.web.controller.user;

import app.back.service.KeycloakUserService;
import app.web.pojo.PojoUser;
import app.web.transform.TransformKeycloakUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final KeycloakUserService keycloakUserService;
    private final TransformKeycloakUser transformKeycloakUser;

    public UserController(KeycloakUserService keycloakUserService, TransformKeycloakUser transformKeycloakUser) {
        this.keycloakUserService = keycloakUserService;
        this.transformKeycloakUser = transformKeycloakUser;
    }

    @GetMapping("/")
    public List<PojoUser> findUsers() {
        return keycloakUserService.getUsers().stream().map(transformKeycloakUser::toPojo).toList();
    }

}
