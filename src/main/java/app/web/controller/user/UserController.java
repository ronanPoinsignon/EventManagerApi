package app.web.controller.user;

import app.web.pojo.PojoKeycloakUserAttributes;
import app.web.service.KeycloakUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final KeycloakUserService keycloakUserService;

    public UserController(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    @GetMapping("/")
    public List<PojoKeycloakUserAttributes> findUsers() {
        return keycloakUserService.getUsers();
    }

    @GetMapping("/findById")
    public PojoKeycloakUserAttributes findUserById(@RequestParam("userId") UUID userId) {
        return keycloakUserService.findById(userId);
    }

    @GetMapping("/findByIds")
    public List<PojoKeycloakUserAttributes> findUserById(@RequestParam("userIds") Collection<UUID> userIds) {
        return keycloakUserService.findByIds(userIds);
    }

}
