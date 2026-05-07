package app.back.service.userattributes;

import app.back.dto.UserAttributes;
import app.back.exception.BackBadRequestException;
import app.back.service.BasicDtoTestService;
import app.back.service.DtoUserAttributesService;
import app.utils.UserAttributesUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class DtoUserAttributesServiceTest extends BasicDtoTestService<UserAttributes, DtoUserAttributesService> {

    private final UserAttributesUtils userAttributesUtils;

    public DtoUserAttributesServiceTest(@Autowired DtoUserAttributesService dtoUserAttributesService, @Autowired UserAttributesUtils userAttributesUtils) {
        super(dtoUserAttributesService);
        this.userAttributesUtils = userAttributesUtils;
    }

    @Override
    protected UserAttributes createBasicObject() {
        return userAttributesUtils.createBasicEntity();
    }

    @Test
    @Order(1)
    void testCreate() {
        var userAttributes = createBasicObject();

        Assertions.assertNull(userAttributes.getId());
        var result = dtoService.save(userAttributes);

        Assertions.assertNotNull(userAttributes.getId());
        Assertions.assertEquals(userAttributes.getId(), result.getId());
        Assertions.assertEquals(userAttributes.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(userAttributes.getKeycloakUserId(), result.getKeycloakUserId());
    }

    @Test
    @Order(2)
    void testFindByDiscordId() {
        var userAttributes = createBasicObject();
        dtoService.save(userAttributes);
        var result = dtoService.findByDiscordId(userAttributes.getDiscordId()).orElseThrow(() -> new RuntimeException("Aucun objet trouvé"));
        Assertions.assertEquals(userAttributes.getId(), result.getId());
    }

    @Test
    @Order(3)
    void testFindByUserIdList() {
        var userAttributes1 = createBasicObject();
        var userAttributes2 = createBasicObject();
        var userAttributes3 = createBasicObject();

        dtoService.save(userAttributes1);
        dtoService.save(userAttributes2);
        dtoService.save(userAttributes3);

        var userIdListToFind = List.of(userAttributes1.getId(), userAttributes2.getId());
        var result = dtoService.findByDiscordId(userIdListToFind);
        Assertions.assertEquals(2, result.size());
        var match = result.stream().map(UserAttributes::getId).allMatch(userIdListToFind::contains);
        Assertions.assertTrue(match);
    }

    @Test
    @Order(4)
    void testFindByUserIdListEmpty() {
        var userAttributes1 = createBasicObject();
        var userAttributes2 = createBasicObject();
        var userAttributes3 = createBasicObject();

        dtoService.save(userAttributes1);
        dtoService.save(userAttributes2);
        dtoService.save(userAttributes3);

        var result = dtoService.findByDiscordId(new ArrayList<>());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(5)
    void testFindByUserIdListNull() {
        var userAttributes1 = createBasicObject();
        var userAttributes2 = createBasicObject();
        var userAttributes3 = createBasicObject();

        dtoService.save(userAttributes1);
        dtoService.save(userAttributes2);
        dtoService.save(userAttributes3);

        var result = dtoService.findByDiscordId((List<Long>) null);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(7)
    void testSaveWithoutUserId() {
        var userAttributes = createBasicObject();
        userAttributes.setDiscordId(null);
        Assertions.assertThrows(BackBadRequestException.class, () -> dtoService.save(userAttributes));
    }

    @Test
    @Order(8)
    void updateWithNewEntity(@Autowired EntityManager entityManager) {
        userAttributesUtils.stopAll();
        var base = createBasicObject();
        var userAttributes = createBasicObject();
        userAttributesUtils.playAll();

        userAttributes = dtoService.save(userAttributes);
        entityManager.detach(userAttributes);

        userAttributes = dtoService.save(userAttributes);
        Assertions.assertEquals(base.getDiscordId(), userAttributes.getDiscordId());

        entityManager.detach(userAttributes);
        userAttributes = dtoService.findById(userAttributes.getId()).orElseThrow(() -> new RuntimeException("Aucun objet trouvé."));
        Assertions.assertEquals(base.getDiscordId(), userAttributes.getDiscordId());
    }

}
