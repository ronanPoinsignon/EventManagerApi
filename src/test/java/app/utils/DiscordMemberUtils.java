package app.utils;

import app.back.dto.DiscordMember;
import app.web.pojo.PojoDiscordMember;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.atomic.AtomicLong;

public class DiscordMemberUtils {

    private static final AtomicLong counter = new AtomicLong();

    public static DiscordMember createBasicEntity() {
        var dm = new DiscordMember();
        dm.setDiscordId(counter.getAndIncrement());
        dm.setFirstname("firstname_test_" + counter.getAndIncrement());
        dm.setNickname("nickname_test_" + counter.getAndIncrement());

        return dm;
    }

    public static PojoDiscordMember createBasicPojo() {
        var dm = new PojoDiscordMember();
        dm.setDiscordId(counter.getAndIncrement());
        dm.setFirstname("firstname_test_" + counter.getAndIncrement());
        dm.setNickname("nickname_test_" + counter.getAndIncrement());

        return dm;
    }

    public static void compare(DiscordMember dm, PojoDiscordMember pojo) {
        Assertions.assertEquals(dm.getId(), pojo.getId());
        Assertions.assertEquals(dm.getDiscordId(), pojo.getDiscordId());
        Assertions.assertEquals(dm.getFirstname(), pojo.getFirstname());
        Assertions.assertEquals(dm.getNickname(), pojo.getNickname());
    }

    public static void compare(PojoDiscordMember pojo, DiscordMember result) {
        Assertions.assertEquals(pojo.getId(), result.getId());
        Assertions.assertEquals(pojo.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(pojo.getFirstname(), result.getFirstname());
        Assertions.assertEquals(pojo.getNickname(), result.getNickname());
    }

}
