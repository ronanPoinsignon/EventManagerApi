package app.back.service.event;

import app.back.dto.Event;
import app.back.exception.BackBadRequestException;
import app.back.service.BasicDtoTestService;
import app.back.service.DtoEventService;
import app.utils.EventUtils;
import app.utils.UserAttributesUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class DtoEventServiceTest extends BasicDtoTestService<Event, DtoEventService> {

    private final EventUtils eventUtils;
    private final UserAttributesUtils discordMemberUtils;

    public DtoEventServiceTest(@Autowired DtoEventService dtoEventService, @Autowired EventUtils eventUtils, @Autowired UserAttributesUtils discordMemberUtils) {
        super(dtoEventService);
        this.eventUtils = eventUtils;
        this.discordMemberUtils = discordMemberUtils;
    }

    @Override
    protected Event createBasicObject() {
        return eventUtils.createBasicEntity();
    }

    @Test
    @Order(1)
    void testCreate() {
        eventUtils.stopAll();
        discordMemberUtils.stopAll();
        var base = eventUtils.createFullEntity();
        var event = eventUtils.createFullEntity();
        discordMemberUtils.playAll();
        eventUtils.playALl();

        event.setCreationDate(base.getCreationDate());
        event = dtoService.save(event);

        base.setId(event.getId());

        var baseSubEvent = base.getSubEvents().getFirst();
        var eventSubEvent = event.getSubEvents().getFirst();
        baseSubEvent.setId(eventSubEvent.getId());

        base.removeParticipants(base.getParticipants());
        base.addParticipants(event.getParticipants());

        var baseTodo = base.getTodoList().getFirst();
        var eventTodo = event.getTodoList().getFirst();
        baseTodo.setId(eventTodo.getId());

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
        eventUtils.addSubEvent(event);
        event = dtoService.save(event);
        var result = dtoService.findByEventName(event.getSubEvents().getFirst().getEventName());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(6)
    void testSaveEventNameNull() {
        var event = createBasicObject();
        event.setEventName(null);
        Assertions.assertThrows(BackBadRequestException.class, () -> dtoService.save(event));
        event.setEventName("");
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
        event1.addSubEvent(event2);
        dtoService.save(event2);
        var event3 = createBasicObject();
        event3.setEventName(event2.getEventName());
        event1.addSubEvent(event3);
        Assertions.assertThrows(BackBadRequestException.class, () -> dtoService.save(event3));
    }

    @Test
    @Order(9)
    void testFindBeforeEndWithStart() {
        var start = Instant.now();

        var event1 = createBasicObject();
        event1.setStartDate(start.minus(1, ChronoUnit.DAYS));
        event1.setEndDate(null);
        dtoService.save(event1);

        var event2 = createBasicObject();
        event2.setStartDate(start.minus(2, ChronoUnit.DAYS));
        event2.setEndDate(null);
        dtoService.save(event2);

        var result = dtoService.findAllBeforeEnd(start.minus(2, ChronoUnit.DAYS));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(event1.getId(), result.getFirst().getId());
    }

    @Test
    @Order(10)
    void testFindBeforeEndWithEnd() {
        var start = Instant.now();

        var event1 = createBasicObject();
        event1.setStartDate(start.minus(1, ChronoUnit.DAYS));
        event1.setEndDate(start.plus(1, ChronoUnit.DAYS));
        dtoService.save(event1);

        var event2 = createBasicObject();
        event2.setStartDate(start.minus(2, ChronoUnit.DAYS));
        event2.setEndDate(start.plus(2, ChronoUnit.DAYS));
        dtoService.save(event2);

        var event3 = createBasicObject();
        event3.setStartDate(start.minus(3, ChronoUnit.DAYS));
        event3.setEndDate(start.plus(3, ChronoUnit.DAYS));
        dtoService.save(event3);

        var result = dtoService.findAllBeforeEnd(start.plus(2, ChronoUnit.DAYS));
        Assertions.assertEquals(2, result.size());
        var match = result.stream().map(Event::getId).allMatch(List.of(event2.getId(), event3.getId())::contains);
        Assertions.assertTrue(match);
    }

    @Test
    @Order(11)
    void testFindBeforeEndWithStartAndEnd() {
        var start = Instant.now();

        var event1 = createBasicObject();
        event1.setStartDate(start.plus(3, ChronoUnit.DAYS));
        event1.setEndDate(null);
        dtoService.save(event1);

        var event2 = createBasicObject();
        event2.setStartDate(start.minus(2, ChronoUnit.DAYS));
        event2.setEndDate(start.plus(2, ChronoUnit.DAYS));
        dtoService.save(event2);

        var event3 = createBasicObject();
        event3.setStartDate(start.minus(3, ChronoUnit.DAYS));
        event3.setEndDate(start.plus(4, ChronoUnit.DAYS));
        dtoService.save(event3);

        var event4 = createBasicObject();
        event4.setStartDate(start.minus(3, ChronoUnit.DAYS));
        event4.setEndDate(start.plus(1, ChronoUnit.DAYS));
        dtoService.save(event4);

        var result = dtoService.findAllBeforeEnd(start.plus(2, ChronoUnit.DAYS));
        Assertions.assertEquals(3, result.size());
        var match = result.stream().map(Event::getId).allMatch(List.of(event1.getId(), event2.getId(), event3.getId())::contains);
        Assertions.assertTrue(match);
    }

    @Test
    @Order(12)
    void testFindBeforeEndNullDate() {
        var start = Instant.now();

        var event1 = createBasicObject();
        event1.setStartDate(start.plus(3, ChronoUnit.DAYS));
        event1.setEndDate(null);
        dtoService.save(event1);

        var event2 = createBasicObject();
        event2.setStartDate(start.minus(2, ChronoUnit.DAYS));
        event2.setEndDate(null);
        dtoService.save(event2);

        var event3 = createBasicObject();
        event3.setStartDate(start.minus(3, ChronoUnit.DAYS));
        event3.setEndDate(start.plus(3, ChronoUnit.DAYS));
        dtoService.save(event3);

        var result = dtoService.findAllBeforeEnd(null);
        Assertions.assertEquals(2, result.size());
        var match = result.stream().map(Event::getId).allMatch(List.of(event1.getId(), event3.getId())::contains);
        Assertions.assertTrue(match);
    }

    @Test
    @Order(13)
    void testLastEventCreated() {
        var event1 = createBasicObject();
        // obligé de set en dur la différence pour ne pas que les deux événements ne se créent à la même date
        event1.setCreationDate(event1.getCreationDate().plus(1, ChronoUnit.DAYS));
        dtoService.save(event1);

        var event2 = createBasicObject();
        event2.setCreationDate(event2.getCreationDate().plus(5, ChronoUnit.DAYS));
        dtoService.save(event2);

        var event3 = createBasicObject();
        dtoService.save(event3);

        var result = dtoService.getLast().orElseThrow(() -> new RuntimeException("Aucun event trouvé."));
        Assertions.assertEquals(event2.getId(), result.getId());
    }

    @Test
    @Order(14)
    void testLastEventCreatedEmpty() {
        var result = dtoService.getLast();
        Assertions.assertTrue(result.isEmpty());
    }

}
