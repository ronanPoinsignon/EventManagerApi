package app.utils;

import app.back.dto.DiscordMember;
import app.web.pojo.PojoDiscordMember;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Service
public class DiscordMemberUtils {

    private static final AtomicLong counter = new AtomicLong();

    private Supplier<Long> counterStrategy;

    public DiscordMemberUtils() {
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

    public DiscordMember createBasicEntity() {
        var dm = new DiscordMember();
        dm.setDiscordId(counterStrategy.get());
        dm.setFirstname("firstname_test_" + counterStrategy.get());
        dm.setNickname("nickname_test_" + counterStrategy.get());

        return dm;
    }

    public PojoDiscordMember createBasicPojo() {
        var dm = new PojoDiscordMember();
        dm.setDiscordId(counterStrategy.get());
        dm.setFirstname("firstname_test_" + counterStrategy.get());
        dm.setNickname("nickname_test_" + counterStrategy.get());

        return dm;
    }

    public static void compare(DiscordMember dm, PojoDiscordMember result) {
        Assertions.assertEquals(dm.getId(), result.getId());
        Assertions.assertEquals(dm.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(dm.getFirstname(), result.getFirstname());
        Assertions.assertEquals(dm.getNickname(), result.getNickname());
    }

    public static void compare(PojoDiscordMember pojo, DiscordMember result) {
        Assertions.assertEquals(pojo.getId(), result.getId());
        Assertions.assertEquals(pojo.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(pojo.getFirstname(), result.getFirstname());
        Assertions.assertEquals(pojo.getNickname(), result.getNickname());
    }

    public static void compare(DiscordMember dm, DiscordMember result) {
        Assertions.assertEquals(dm.getId(), result.getId());
        Assertions.assertEquals(dm.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(dm.getFirstname(), result.getFirstname());
        Assertions.assertEquals(dm.getNickname(), result.getNickname());
    }

    public static void compare(PojoDiscordMember pojo, PojoDiscordMember result) {
        Assertions.assertEquals(pojo.getId(), result.getId());
        Assertions.assertEquals(pojo.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(pojo.getFirstname(), result.getFirstname());
        Assertions.assertEquals(pojo.getNickname(), result.getNickname());
    }

}
