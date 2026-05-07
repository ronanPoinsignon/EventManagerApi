package app.web.transform;

import app.back.dto.UserAttributes;
import app.utils.UserAttributesUtils;
import app.web.pojo.PojoUserAttributes;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class TransformUserAttributesTest {

    private final TransformMember transformMember;
    private final UserAttributesUtils userAttributesUtils;

    public TransformUserAttributesTest(@Autowired TransformMember transformMember, @Autowired UserAttributesUtils userAttributesUtils) {
        this.transformMember = transformMember;
        this.userAttributesUtils = userAttributesUtils;
    }

    @Test
    @Order(1)
    void testTransformEntityToPojo() {
        var dm = userAttributesUtils.createBasicEntity();
        var result = transformMember.toPojo(dm);
        UserAttributesUtils.compare(dm, result);
    }

    @Test
    @Order(2)
    void testTransformEntityToPojoNull() {
        UserAttributes dm = null;
        var result = transformMember.toPojo(dm);
        Assertions.assertNull(result);
    }

    @Test
    @Order(3)
    void testTransformEntityToPojoList() {
        var dm1 = userAttributesUtils.createBasicEntity();
        var dm2 = userAttributesUtils.createBasicEntity();
        var dmList = List.of(dm1, dm2);
        var result = transformMember.toPojo(dmList);
        UserAttributesUtils.compare(dm1, result.getFirst());
        UserAttributesUtils.compare(dm2, result.get(1));
    }

    @Test
    @Order(4)
    void testTransformEntityToPojoListEmpty() {
        var dmList = List.<UserAttributes>of();
        var result = transformMember.toPojo(dmList);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(5)
    void testTransformEntityToPojoListNull() {
        List<UserAttributes> dmList = null;
        var result = transformMember.toPojo(dmList);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(6)
    void testTransformPojoToEntity() {
        var dm = userAttributesUtils.createBasicPojo();

        var result = transformMember.toDto(dm);
        Assertions.assertEquals(dm.getId(), result.getId());
        Assertions.assertEquals(dm.getDiscordId(), result.getDiscordId());
    }

    @Test
    @Order(7)
    void testTransformPojoToEntityNull() {
        PojoUserAttributes dm = null;
        var result = transformMember.toDto(dm);
        Assertions.assertNull(result);
    }

    @Test
    @Order(8)
    void testTransformPojoToEntityList() {
        var dm1 = userAttributesUtils.createBasicPojo();
        var dm2 = userAttributesUtils.createBasicPojo();
        var dmList = List.of(dm1, dm2);
        var result = transformMember.toDto(dmList);
        UserAttributesUtils.compare(dm1, result.getFirst());
        UserAttributesUtils.compare(dm2, result.get(1));
    }

    @Test
    @Order(9)
    void testTransformPojoToEntityListEmpty() {
        var dmList = List.<PojoUserAttributes>of();
        var result = transformMember.toDto(dmList);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(10)
    void testTransformPojoToEntityListNull() {
        List<PojoUserAttributes> dmList = null;
        var result = transformMember.toDto(dmList);
        Assertions.assertTrue(result.isEmpty());
    }
}
