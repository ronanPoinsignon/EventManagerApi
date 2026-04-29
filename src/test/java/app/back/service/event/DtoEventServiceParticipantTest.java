package app.back.service.event;

import app.back.exception.BackBadRequestException;
import app.back.service.DtoEventService;
import app.utils.DiscordMemberUtils;
import app.utils.EventUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class DtoEventServiceParticipantTest {

    private final EventUtils eventUtils;
    private final DtoEventService dtoService;
    private final DiscordMemberUtils discordMemberUtils;

    public DtoEventServiceParticipantTest(@Autowired DtoEventService dtoService, @Autowired EventUtils eventUtils, @Autowired DiscordMemberUtils discordMemberUtils) {
        this.dtoService = dtoService;
        this.eventUtils = eventUtils;
        this.discordMemberUtils = discordMemberUtils;
    }

    @Test
    @Order(1)
    void testRemoveParticipant() {
        var event = eventUtils.createBasicEntity();
        var member1 = eventUtils.addDiscordMember(event);
        var member2 = eventUtils.addDiscordMember(event);
        Assertions.assertEquals(2, event.getParticipants().size());

        event.removeParticipant(member1.getId());
        Assertions.assertEquals(1, event.getParticipants().size());
    }

    @Test
    @Order(2)
    void testAddParticipants() {
        var event = eventUtils.createBasicEntity();
        var member1 = discordMemberUtils.createBasicEntity();
        var member2 = discordMemberUtils.createBasicEntity();

        event.addParticipants(List.of(member1, member2));
        Assertions.assertEquals(2, event.getParticipants().size());
    }

    @Test
    @Order(3)
    void testSetParticipants() {
        var event = eventUtils.createBasicEntity();
        var member1 = discordMemberUtils.createBasicEntity();
        var member2 = discordMemberUtils.createBasicEntity();
        var member3 = discordMemberUtils.createBasicEntity();

        event.addParticipant(member1);
        var memberList = List.of(member2, member3);
        event.setParticipants(memberList);
        Assertions.assertEquals(2, event.getParticipants().size());
        var result = memberList.containsAll(event.getParticipants());
        Assertions.assertTrue(result);
    }

    @Test
    @Order(4)
    void testAddParticipantNull() {
        var event = eventUtils.createBasicEntity();

        Assertions.assertThrows(BackBadRequestException.class, () -> event.addParticipant(null));
        Assertions.assertEquals(0, event.getParticipants().size());
    }

    @Test
    @Order(5)
    void testAddParticipantsNull() {
        var event = eventUtils.createBasicEntity();

        var result = event.addParticipants(null);
        Assertions.assertFalse(result);
        Assertions.assertEquals(0, event.getParticipants().size());
    }

    @Test
    @Order(6)
    void testSetParticipantsNull() {
        var event = eventUtils.createBasicEntity();
        var member = discordMemberUtils.createBasicEntity();

        event.addParticipant(member);
        event.setParticipants(null);
        Assertions.assertEquals(0, event.getParticipants().size());
    }

}
