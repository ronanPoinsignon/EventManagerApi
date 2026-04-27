package app.web.service;

import app.back.dto.Event;
import app.back.exception.BackBadRequestException;
import app.web.exception.BadRequestException;
import app.web.exception.NotFoundException;
import app.web.pojo.PojoEvent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Transactional
public class EventServiceTest extends BasicTestService<Event, PojoEvent, EventService> {

    private final PojoEvent BASIC_EVENT = new PojoEvent();
    private final AtomicInteger counter = new AtomicInteger();

    public EventServiceTest(@Autowired EventService eventService) {
        super(eventService);

        BASIC_EVENT.setEventName("eventName");
        BASIC_EVENT.setLocation("location");
        BASIC_EVENT.setTricountUrl("tricount");
    }

    @Override
    protected PojoEvent createBasicPojo() {
        var event = new PojoEvent();
        event.setEventName(BASIC_EVENT.getEventName() + "_" + counter.getAndIncrement());
        event.setLocation(BASIC_EVENT.getLocation());
        event.setTricountUrl(BASIC_EVENT.getTricountUrl());

        return event;
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
    @Order(7)
    void testRemoveSubEventOnEmpty() {
        Assertions.assertThrows(BadRequestException.class, () -> service.removeSubEvent(0, ""));
    }

    @Test
    @Order(7)
    void testRemoveSubEventOnSubEventNotFound() {
        var parent = createBasicPojo();
        parent = service.save(parent);
        var parentId = parent.getId();
        Assertions.assertThrows(NotFoundException.class, () -> service.removeSubEvent(parentId, "test"));
    }
}
