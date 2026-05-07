package app.back.service.event;

import app.back.exception.BackBadRequestException;
import app.utils.EventUtils;
import app.utils.UuidUtils;
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
    private final UuidUtils uuidUtils;

    public DtoEventServiceParticipantTest(@Autowired EventUtils eventUtils, @Autowired UuidUtils uuidUtils) {
        this.eventUtils = eventUtils;
        this.uuidUtils = uuidUtils;
    }

    @Test
    @Order(1)
    void testRemoveParticipant() {
        var event = eventUtils.createBasicEntity();
        var user1 = eventUtils.addUserId(event);
        var user2 = eventUtils.addUserId(event);
        Assertions.assertEquals(2, event.getParticipants().size());

        event.removeParticipant(user1);
        Assertions.assertEquals(1, event.getParticipants().size());
    }

    @Test
    @Order(2)
    void testAddParticipants() {
        var event = eventUtils.createBasicEntity();
        var user1 = uuidUtils.generate();
        var user2 = uuidUtils.generate();

        event.addParticipants(List.of(user1, user2));
        Assertions.assertEquals(2, event.getParticipants().size());
    }

    @Test
    @Order(3)
    void testSetParticipants() {
        var event = eventUtils.createBasicEntity();
        var user1 = uuidUtils.generate();
        var user2 = uuidUtils.generate();;
        var user3 = uuidUtils.generate();;

        event.addParticipant(user1);
        var userList = List.of(user2, user3);
        event.setParticipants(userList);
        Assertions.assertEquals(2, event.getParticipants().size());
        var result = userList.containsAll(event.getParticipants());
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
        var userId = uuidUtils.generate();;

        event.addParticipant(userId);
        event.setParticipants(null);
        Assertions.assertEquals(0, event.getParticipants().size());
    }

}
