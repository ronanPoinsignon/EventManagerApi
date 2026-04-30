package app.back.service.discordmember;

import app.back.dto.DiscordMember;
import app.back.exception.BackBadRequestException;
import app.back.service.BasicDtoTestService;
import app.back.service.DtoDiscordMemberService;
import app.utils.DiscordMemberUtils;
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
public class DtoDiscordMemberServiceTest extends BasicDtoTestService<DiscordMember, DtoDiscordMemberService> {

    private final DiscordMemberUtils discordMemberUtils;

    public DtoDiscordMemberServiceTest(@Autowired DtoDiscordMemberService discordMemberService, @Autowired DiscordMemberUtils discordMemberUtils) {
        super(discordMemberService);
        this.discordMemberUtils = discordMemberUtils;
    }

    @Override
    protected DiscordMember createBasicObject() {
        return discordMemberUtils.createBasicEntity();
    }

    @Test
    @Order(1)
    void testCreate() {
        var discordMember = createBasicObject();

        Assertions.assertNull(discordMember.getId());
        var result = dtoService.save(discordMember);

        Assertions.assertNotNull(discordMember.getId());
        Assertions.assertEquals(discordMember.getId(), result.getId());
        Assertions.assertEquals(discordMember.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(discordMember.getNickname(), result.getNickname());
        Assertions.assertEquals(discordMember.getFirstname(), result.getFirstname());
    }

    @Test
    @Order(2)
    void testFindByDiscordId() {
        var discordMember = createBasicObject();
        dtoService.save(discordMember);
        var result = dtoService.findByDiscordId(discordMember.getDiscordId()).orElseThrow(() -> new RuntimeException("Aucun objet trouvé"));
        Assertions.assertEquals(discordMember.getId(), result.getId());
    }

    @Test
    @Order(3)
    void testFindByDiscordIdList() {
        var discordMember1 = createBasicObject();
        var discordMember2 = createBasicObject();
        var discordMember3 = createBasicObject();

        dtoService.save(discordMember1);
        dtoService.save(discordMember2);
        dtoService.save(discordMember3);

        var discordIdListToFind = List.of(discordMember1.getId(), discordMember2.getId());
        var result = dtoService.findByDiscordId(discordIdListToFind);
        Assertions.assertEquals(2, result.size());
        var match = result.stream().map(DiscordMember::getId).allMatch(discordIdListToFind::contains);
        Assertions.assertTrue(match);
    }

    @Test
    @Order(4)
    void testFindByDiscordIdListEmpty() {
        var discordMember1 = createBasicObject();
        var discordMember2 = createBasicObject();
        var discordMember3 = createBasicObject();

        dtoService.save(discordMember1);
        dtoService.save(discordMember2);
        dtoService.save(discordMember3);

        var result = dtoService.findByDiscordId(new ArrayList<>());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(5)
    void testFindByDiscordIdListNull() {
        var discordMember1 = createBasicObject();
        var discordMember2 = createBasicObject();
        var discordMember3 = createBasicObject();

        dtoService.save(discordMember1);
        dtoService.save(discordMember2);
        dtoService.save(discordMember3);

        var result = dtoService.findByDiscordId((List<Long>) null);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(6)
    void testFindByNickname() {
        var discordMember = createBasicObject();
        dtoService.save(discordMember);
        var result = dtoService.findByNickname(discordMember.getNickname()).orElseThrow(() -> new RuntimeException("Aucun objet trouvé"));
        Assertions.assertEquals(discordMember.getId(), result.getId());
    }

    @Test
    @Order(7)
    void testSaveWithoutDiscordId() {
        var discordMember = createBasicObject();
        discordMember.setDiscordId(null);
        Assertions.assertThrows(BackBadRequestException.class, () -> dtoService.save(discordMember));
    }

    @Test
    @Order(8)
    void updateWithNewEntity(@Autowired EntityManager entityManager) {
        discordMemberUtils.stopAll();
        var base = createBasicObject();
        var discordMember = createBasicObject();
        discordMemberUtils.playAll();

        discordMember = dtoService.save(discordMember);
        entityManager.detach(discordMember);
        discordMember.setFirstname("update");

        discordMember = dtoService.save(discordMember);
        Assertions.assertEquals(base.getNickname(), discordMember.getNickname());
        Assertions.assertEquals(base.getDiscordId(), discordMember.getDiscordId());
        Assertions.assertEquals("update", discordMember.getFirstname());

        entityManager.detach(discordMember);
        discordMember = dtoService.findById(discordMember.getId()).orElseThrow(() -> new RuntimeException("Aucun objet trouvé."));
        Assertions.assertEquals(base.getNickname(), discordMember.getNickname());
        Assertions.assertEquals(base.getDiscordId(), discordMember.getDiscordId());
        Assertions.assertEquals("update", discordMember.getFirstname());
    }

}
