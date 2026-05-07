package app.web.service.event;

import app.back.dto.Event;
import app.utils.EventUtils;
import app.utils.UuidUtils;
import app.web.api.EventServiceApi;
import app.web.exception.NotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class EventServiceParticipantTest {

    private final EventServiceApi service;
    private final EventUtils eventUtils;
    private final UuidUtils uuidUtils;

    protected EventServiceParticipantTest(@Autowired EventServiceApi service, @Autowired EventUtils eventUtils, @Autowired UuidUtils uuidUtils) {
        this.service = service;
        this.eventUtils = eventUtils;
        this.uuidUtils = uuidUtils;
    }

    @Test
    @Order(1)
    void testAddParticipant() {
        var event = eventUtils.createBasicPojo();
        event = service.save(event);
        var user = uuidUtils.generate();

        event = service.addTo(event.getId(), List.of(user));
        Assertions.assertEquals(1, event.getParticipants().size());
        Assertions.assertEquals(user, event.getParticipants().getFirst());
    }

    @Test
    @Order(2)
    void testAddParticipantParticipantNotFound() {
        var event = eventUtils.createBasicPojo();
        event = service.save(event);

        app.web.pojo.PojoEvent finalEvent = event;
        event = service.addTo(finalEvent.getId(), List.of(uuidUtils.generate()));
        Assertions.assertEquals(1, event.getParticipants().size());
    }

    @Test
    @Order(3)
    void testAddParticipantEventNotFound() {
        var user = uuidUtils.generate();
        Assertions.assertThrows(NotFoundException.class, () -> service.addTo(1L, List.of(user)));
    }

    @Test
    @Order(4)
    void testAddParticipantNull() {
        var event = eventUtils.createBasicPojo();
        eventUtils.addUserId(event);
        event = service.save(event);
        Assertions.assertEquals(1, event.getParticipants().size());

        event = service.addTo(event.getId(), null);
        Assertions.assertEquals(1, event.getParticipants().size());
    }

    @Test
    @Order(5)
    void testRemoveParticipant() {
        var event = eventUtils.createBasicPojo();
        var user = eventUtils.addUserId(event);
        event = service.save(event);
        Assertions.assertEquals(1, event.getParticipants().size());

        event = service.removeTo(event.getId(), List.of(user));
        Assertions.assertTrue(event.getParticipants().isEmpty());
    }

    @Test
    @Order(6)
    void testRemoveParticipantParticipantNotFound() {
        var event = eventUtils.createBasicPojo();
        var user = eventUtils.addUserId(event);
        event = service.save(event);
        Assertions.assertEquals(1, event.getParticipants().size());

        app.web.pojo.PojoEvent finalEvent = event;
        service.removeTo(finalEvent.getId(), List.of(uuidUtils.generate()));

        event = service.findOne(event.getId());
        Assertions.assertEquals(1, event.getParticipants().size());
    }

    @Test
    @Order(7)
    void testRemoveParticipantEventNotFound() {
        var user = eventUtils.addUserId(new Event());
        Assertions.assertThrows(NotFoundException.class, () -> service.removeTo(1L, List.of(user)));
    }

    @Test
    @Order(4)
    void testRemoveParticipantNull() {
        var event = eventUtils.createBasicPojo();
        var user = eventUtils.addUserId(event);
        event = service.save(event);
        Assertions.assertEquals(1, event.getParticipants().size());

        event = service.removeTo(event.getId(), null);
        Assertions.assertEquals(1, event.getParticipants().size());
        Assertions.assertEquals(user, event.getParticipants().getFirst());
    }

}
