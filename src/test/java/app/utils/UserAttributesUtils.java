package app.utils;

import app.back.dto.UserAttributes;
import app.web.pojo.PojoUserAttributes;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Service
public class UserAttributesUtils {

    private final UuidUtils uuidUtils;

    private static final AtomicLong counter = new AtomicLong();

    private Supplier<Long> counterStrategy;

    public UserAttributesUtils(@Autowired UuidUtils uuidUtils) {
        this.uuidUtils = uuidUtils;
        playCounter();
    }

    public void stopAll() {
        stopCounter();
    }

    public void playAll() {
        playCounter();
    }

    public void stopCounter() {
        counterStrategy = counter::get;
    }

    public void playCounter() {
        counterStrategy = counter::incrementAndGet;
    }

    public UserAttributes createBasicEntity() {
        var userAttributes = new UserAttributes();
        userAttributes.setDiscordId(counterStrategy.get());
        userAttributes.setKeycloakUserId(uuidUtils.generate());

        return userAttributes;
    }

    public PojoUserAttributes createBasicPojo() {
        var userAttributes = new PojoUserAttributes();
        userAttributes.setDiscordId(counterStrategy.get());
        userAttributes.setKeycloakUserId(uuidUtils.generate());

        return userAttributes;
    }

    public static void compare(UserAttributes base, UserAttributes result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(base.getKeycloakUserId(), result.getKeycloakUserId());
    }

    public static void compare(UserAttributes base, PojoUserAttributes result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(base.getKeycloakUserId(), result.getKeycloakUserId());
    }

    public static void compare(PojoUserAttributes base, UserAttributes result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(base.getKeycloakUserId(), result.getKeycloakUserId());
    }

    public static void compare(PojoUserAttributes base, PojoUserAttributes result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(base.getKeycloakUserId(), result.getKeycloakUserId());
    }

}
