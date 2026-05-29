package app.serviceprimary;

import app.back.api.KeycloakUserServiceApi;
import app.back.dto.KeycloakUser;
import app.back.security.User;
import app.back.security.UserServiceApi;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Primary
public class KeycloakUserServiceTest implements KeycloakUserServiceApi {


    private final List<User> userList = new ArrayList<>();

    public KeycloakUserServiceTest(UserServiceApi userServiceApi) {
        userList.add(userServiceApi.getUser());
    }

    @Override
    public List<KeycloakUser> getUsers() {
        return userList.stream().map(user -> {
            var keycloakUser = new KeycloakUser();
            keycloakUser.setId(user.getUserId());
            keycloakUser.setFirstName(user.getPrenom());
            keycloakUser.setLastName(user.getNom());

            return keycloakUser;
        }).toList();
    }

    @Override
    public Optional<KeycloakUser> getUserById(UUID userId) {
        return getUsers().stream().filter(user -> user.getId().equals(userId)).findFirst();
    }

    @Override
    public LinkedHashMap<String, String> impersonate(String keycloakUserId) {
        return null;
    }

    public void addNewUser(UUID userId, String firstname, String lastname) {
        var newUser = new User();
        newUser.setUserId(userId);
        newUser.setPrenom(firstname);
        newUser.setNom(lastname);
        userList.add(newUser);
    }
}
