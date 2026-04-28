package app.web.transform;

import app.back.dto.DiscordMember;
import app.utils.DiscordMemberUtils;
import app.web.pojo.PojoDiscordMember;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class TransformDiscordMemberTest {

    private static final AtomicLong counter = new AtomicLong();

    private final TransformMember transformMember;

    public TransformDiscordMemberTest(@Autowired TransformMember transformMember) {
        this.transformMember = transformMember;
    }

    @Test
    @Order(1)
    void testTransformEntityToPojo() {
        var dm = DiscordMemberUtils.createBasicEntity();
        var result = transformMember.toPojo(dm);
        DiscordMemberUtils.compare(dm, result);
    }

    @Test
    @Order(2)
    void testTransformEntityToPojoNull() {
        DiscordMember dm = null;
        var result = transformMember.toPojo(dm);
        Assertions.assertNull(result);
    }

    @Test
    @Order(3)
    void testTransformEntityToPojoList() {
        var dm1 = DiscordMemberUtils.createBasicEntity();
        var dm2 = DiscordMemberUtils.createBasicEntity();
        var dmList = List.of(dm1, dm2);
        var result = transformMember.toPojo(dmList);
        DiscordMemberUtils.compare(dm1, result.getFirst());
        DiscordMemberUtils.compare(dm2, result.get(1));
    }

    @Test
    @Order(4)
    void testTransformEntityToPojoListEmpty() {
        var dmList = List.<DiscordMember>of();
        var result = transformMember.toPojo(dmList);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(5)
    void testTransformEntityToPojoListNull() {
        List<DiscordMember> dmList = null;
        var result = transformMember.toPojo(dmList);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(6)
    void testTransformPojoToEntity() {
        var dm = DiscordMemberUtils.createBasicPojo();

        var result = transformMember.toDto(dm);
        Assertions.assertEquals(dm.getId(), result.getId());
        Assertions.assertEquals(dm.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(dm.getFirstname(), result.getFirstname());
        Assertions.assertEquals(dm.getNickname(), result.getNickname());
    }

    @Test
    @Order(7)
    void testTransformPojoToEntityNull() {
        PojoDiscordMember dm = null;
        var result = transformMember.toDto(dm);
        Assertions.assertNull(result);
    }

    @Test
    @Order(8)
    void testTransformPojoToEntityList() {
        var dm1 = DiscordMemberUtils.createBasicPojo();
        var dm2 = DiscordMemberUtils.createBasicPojo();
        var dmList = List.of(dm1, dm2);
        var result = transformMember.toDto(dmList);
        DiscordMemberUtils.compare(dm1, result.getFirst());
        DiscordMemberUtils.compare(dm2, result.get(1));
    }

    @Test
    @Order(9)
    void testTransformPojoToEntityListEmpty() {
        var dmList = List.<PojoDiscordMember>of();
        var result = transformMember.toDto(dmList);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(10)
    void testTransformPojoToEntityListNull() {
        List<PojoDiscordMember> dmList = null;
        var result = transformMember.toDto(dmList);
        Assertions.assertTrue(result.isEmpty());
    }
}
