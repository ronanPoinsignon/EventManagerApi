package app.web.service;

import app.back.dto.Event;
import app.back.exception.BackBadRequestException;
import app.utils.EventUtils;
import app.web.exception.BadRequestException;
import app.web.exception.NotFoundException;
import app.web.pojo.PojoEvent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class EventServiceTest extends BasicTestService<Event, PojoEvent, EventService> {

    private final EventUtils eventUtils;

    public EventServiceTest(@Autowired EventService eventService, @Autowired EventUtils eventUtils) {
        super(eventService);
        this.eventUtils = eventUtils;
    }

    @Override
    protected PojoEvent createBasicPojo() {
        return eventUtils.createBasicPojo();
    }

    @Test
    @Order(1)
    void testAddSubEvent() {
        var parent = createBasicPojo();
        parent = service.save(parent);
        Assertions.assertTrue(parent.getSubEvents().isEmpty());

        var resultAdd = service.addSubEvent(parent.getId(), createBasicPojo());
        Assertions.assertEquals(1, resultAdd.getSubEvents().size());

        parent = service.findOne(parent.getId());
        Assertions.assertEquals(1, resultAdd.getSubEvents().size());

        var enfant = parent.getSubEvents().getFirst();
        enfant = service.findOne(enfant.getId());
        Assertions.assertEquals(parent.getId(), enfant.getParentEvent().getId());
    }

    @Test
    @Order(2)
    void testAddSubEventWithoutName() {
        var parent = createBasicPojo();
        parent = service.save(parent);
        var parentId = parent.getId();

        var enfant = createBasicPojo();
        enfant.setEventName(null);
        Assertions.assertThrows(BackBadRequestException.class, () -> service.addSubEvent(parentId, enfant));
    }

    @Test
    @Order(3)
    void testAddSubEventOnNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.addSubEvent(0, createBasicPojo()));
    }

    @Test
    @Order(4)
    void testAddSubEventNull() {
        Assertions.assertThrows(BadRequestException.class, () -> service.addSubEvent(0, null));
    }

    @Test
    @Order(5)
    void testAddEventNameConflict() {
        var event1 = createBasicPojo();
        var event2 = createBasicPojo();
        event2.setEventName(event1.getEventName());

        service.save(event1);
        Assertions.assertThrows(BackBadRequestException.class, () -> service.save(event2));
    }

    @Test
    @Order(5)
    void testUpdateEventNameConflict() {
        var event1 = createBasicPojo();
        var event2 = createBasicPojo();

        event1 = service.save(event1);
        event2 = service.save(event2);

        event2.setEventName(event1.getEventName());
        PojoEvent finalEvent = event2;
        Assertions.assertThrows(BackBadRequestException.class, () -> service.save(finalEvent));
    }

    @Test
    @Order(5)
    void testRemoveSubEvent() {
        var parent = createBasicPojo();
        parent = service.save(parent);
        var enfant = createBasicPojo();
        parent = service.addSubEvent(parent.getId(), enfant);
        enfant =  parent.getSubEvents().getFirst();

        parent = service.removeSubEvent(parent.getId(), enfant.getEventName());
        Assertions.assertTrue(parent.getSubEvents().isEmpty());
    }

    @Test
    @Order(6)
    void testRemoveSubEventOnNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.removeSubEvent(0, createBasicPojo().getEventName()));
    }

    @Test
    @Order(7)
    void testRemoveSubEventOnNull() {
        Assertions.assertThrows(BadRequestException.class, () -> service.removeSubEvent(0, null));
    }

    @Test
    @Order(8)
    void testRemoveSubEventOnEmpty() {
        Assertions.assertThrows(BadRequestException.class, () -> service.removeSubEvent(0, ""));
    }

    @Test
    @Order(9)
    void testRemoveSubEventOnSubEventNotFound() {
        var parent = createBasicPojo();
        parent = service.save(parent);
        var parentId = parent.getId();
        Assertions.assertThrows(NotFoundException.class, () -> service.removeSubEvent(parentId, "test"));
    }

}
