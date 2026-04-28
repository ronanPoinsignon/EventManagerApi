package app.back.service;

import app.back.dto.Event;
import app.back.exception.BackBadRequestException;
import app.utils.EventUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Transactional
public class DtoEventServiceTest extends BasicDtoTestService<Event, DtoEventService> {

    private final EventUtils eventUtils;

    public DtoEventServiceTest(@Autowired DtoEventService dtoEventService, @Autowired EventUtils eventUtils) {
        super(dtoEventService);
        this.eventUtils = eventUtils;
    }

    @Override
    protected Event createBasicObject() {
        return eventUtils.createBasicEntity();
    }

    @Test
    @Order(1)
    void testCreate() {
        eventUtils.stopAll();
        var base = eventUtils.createFullEntity();
        var event = eventUtils.createFullEntity();
        eventUtils.playALl();

        event.setCreationDate(base.getCreationDate());
        event = dtoService.save(event);

        base.setId(event.getId());
        base.getSubEvents().getFirst().setId(event.getSubEvents().getFirst().getId());
        EventUtils.compare(base, event);
    }

    @Test
    @Order(2)
    void testFindByEventNameOk() {
        var event = createBasicObject();
        dtoService.save(event);
        var result = dtoService.findByEventName(event.getEventName()).orElseThrow((() -> new RuntimeException("Aucun event trouvé.")));
        Assertions.assertEquals(event.getId(), result.getId());
    }

    @Test
    @Order(3)
    void testFindByEventNameNok() {
        var event = createBasicObject();
        dtoService.save(event);
        var result = dtoService.findByEventName("test");
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(4)
    void testFindByEventNameNull() {
        var event = createBasicObject();
        dtoService.save(event);
        var result = dtoService.findByEventName(null);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(5)
    void testFindByEventNameSubEvent() {
        var event = createBasicObject();
        event = dtoService.save(event);
        var enfant = createBasicObject();
        enfant.setParentEvent(event);
        dtoService.save(event);
        var result = dtoService.findByEventName(enfant.getEventName());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(6)
    void testSaveEventNameNull() {
        var event = createBasicObject();
        event.setEventName(null);
        Assertions.assertThrows(BackBadRequestException.class, () -> dtoService.save(event));
    }

    @Test
    @Order(7)
    void testSaveSameEventInfo() {
        var event1 = createBasicObject();
        var event2 = createBasicObject();
        event1.setEventName("test");
        event2.setEventName("test");
        dtoService.save(event1);
        Assertions.assertThrows(BackBadRequestException.class, () -> dtoService.save(event2));
    }

    @Test
    @Order(8)
    void testSaveSameEventInfoOnChildren() {
        var event1 = createBasicObject();
        event1 = dtoService.save(event1);
        var event2 = createBasicObject();
        event2.setParentEvent(event1);
        dtoService.save(event2);
        var event3 = createBasicObject();
        event3.setEventName(event2.getEventName());
        event3.setParentEvent(event1);
        Assertions.assertThrows(BackBadRequestException.class, () -> dtoService.save(event3));
    }

    @Test
    @Order(9)
    void testFindBeforeEndWithStart() {
        var start = LocalDateTime.now();

        var event1 = createBasicObject();
        event1.setStartDate(start.minusDays(1));
        event1.setEndDate(null);
        dtoService.save(event1);

        var event2 = createBasicObject();
        event2.setStartDate(start.minusDays(2));
        event2.setEndDate(null);
        dtoService.save(event2);

        var result = dtoService.findAllBeforeEnd(start.minusDays(1));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(event1.getId(), result.getFirst().getId());
    }

    @Test
    @Order(10)
    void testFindBeforeEndWithEnd() {
        var start = LocalDateTime.now();

        var event1 = createBasicObject();
        event1.setStartDate(start.minusDays(1));
        event1.setEndDate(start.plusDays(1));
        dtoService.save(event1);

        var event2 = createBasicObject();
        event2.setStartDate(start.minusDays(2));
        event2.setEndDate(start.plusDays(2));
        dtoService.save(event2);

        var event3 = createBasicObject();
        event3.setStartDate(start.minusDays(3));
        event3.setEndDate(start.plusDays(3));
        dtoService.save(event3);

        var result = dtoService.findAllBeforeEnd(start.plusDays(2));
        Assertions.assertEquals(2, result.size());
        var match = result.stream().map(Event::getId).allMatch(List.of(event2.getId(), event3.getId())::contains);
        Assertions.assertTrue(match);
    }

    @Test
    @Order(11)
    void testFindBeforeEndWithStartAndEnd() {
        var start = LocalDateTime.now();

        var event1 = createBasicObject();
        event1.setStartDate(start.plusDays(3));
        dtoService.save(event1);

        var event2 = createBasicObject();
        event2.setStartDate(start.minusDays(2));
        event2.setEndDate(start.plusDays(2));
        dtoService.save(event2);

        var event3 = createBasicObject();
        event3.setStartDate(start.minusDays(3));
        event3.setEndDate(start.plusDays(4));
        dtoService.save(event3);

        var event4 = createBasicObject();
        event4.setStartDate(start.minusDays(3));
        event4.setEndDate(start.plusDays(1));
        dtoService.save(event4);

        var result = dtoService.findAllBeforeEnd(start.plusDays(2));
        Assertions.assertEquals(3, result.size());
        var match = result.stream().map(Event::getId).allMatch(List.of(event1.getId(), event2.getId(), event3.getId())::contains);
        Assertions.assertTrue(match);
    }

    @Test
    @Order(12)
    void testLastEventCreated() {
        var event1 = createBasicObject();
        // obligé de set en dur la différence pour ne pas que les deux événements ne se créent à la même date
        event1.setCreationDate(event1.getCreationDate().plusDays(1));
        dtoService.save(event1);

        var event2 = createBasicObject();
        event2.setCreationDate(event2.getCreationDate().plusDays(5));
        dtoService.save(event2);

        var event3 = createBasicObject();
        dtoService.save(event3);

        var result = dtoService.getLast().orElseThrow(() -> new RuntimeException("Aucun event trouvé."));
        Assertions.assertEquals(event2.getId(), result.getId());
    }

    @Test
    @Order(13)
    void testLastEventCreatedEmpty() {
        var result = dtoService.getLast();
        Assertions.assertTrue(result.isEmpty());
    }

}
