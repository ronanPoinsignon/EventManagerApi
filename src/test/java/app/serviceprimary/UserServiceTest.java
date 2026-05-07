package app.serviceprimary;

import app.back.security.User;
import app.back.security.UserServiceApi;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Primary
public class UserServiceTest implements UserServiceApi {

    private final User testUser;

    UserServiceTest() {
        testUser = new User();
        testUser.setNom("test nom");
        testUser.setPrenom("test prenom");
        testUser.setUserId(UUID.randomUUID());
    }

    @Override
    public User getUser() {
        return testUser;
    }

}
