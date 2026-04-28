package app.back.service;

import app.back.dto.Event;
import app.back.exception.BackBadRequestException;
import app.back.exception.duplicate.event.BackDuplicateEventNameException;
import app.utils.DiscordMemberUtils;
import app.utils.EventUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class DtoEventServiceTest extends BasicDtoTestService<Event, DtoEventService> {

    private final EventUtils eventUtils;
    private final DiscordMemberUtils discordMemberUtils;

    public DtoEventServiceTest(@Autowired DtoEventService dtoEventService, @Autowired EventUtils eventUtils, @Autowired DiscordMemberUtils discordMemberUtils) {
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
        new ArrayList<>(base.getParticipants()).getFirst().setId(new ArrayList<>(event.getParticipants()).getFirst().getId());
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
        event1.setEndDate(null);
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
    void testFindBeforeEndNullDate() {
        var start = LocalDateTime.now();

        var event1 = createBasicObject();
        event1.setStartDate(start.plusDays(3));
        event1.setEndDate(null);
        dtoService.save(event1);

        var event2 = createBasicObject();
        event2.setStartDate(start.minusDays(2));
        event2.setEndDate(null);
        dtoService.save(event2);

        var event3 = createBasicObject();
        event3.setStartDate(start.minusDays(3));
        event3.setEndDate(start.plusDays(4));
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

    @Test
    @Order(14)
    void testLastWithSubEvent() {
        var event = createBasicObject();
        dtoService.save(event);
        var parent = createBasicObject();
        dtoService.save(parent);
        eventUtils.addSubEvent(parent);
        dtoService.save(parent);

        var result = dtoService.getLast().orElseThrow(() -> new RuntimeException("Aucun objet trouvé."));
        Assertions.assertEquals(parent.getId(), result.getId());
    }


    @Test
    @Order(15)
    void testUpdateSubEvent() {
        var parent = eventUtils.createBasicEntity();
        eventUtils.addSubEvent(parent);
        parent = dtoService.save(parent);
        parent.getSubEvents().getFirst().setEventName("test");
        parent = dtoService.save(parent);
        Assertions.assertEquals("test", parent.getSubEvents().getFirst().getEventName());
    }

    @Test
    @Order(16)
    void testAddSubEventNameConflict(@Autowired EntityManager entityManager) {
        var parent = eventUtils.createBasicEntity();
        var subEvent1 = eventUtils.addSubEvent(parent);
        parent = dtoService.save(parent);

        var subEvent2 = eventUtils.addSubEvent(parent);
        subEvent2.setEventName(subEvent1.getEventName());

        Event finalParent = parent;
        // obligé pour ne pas qu'hibernate flush les modifications de l'entité à son prochain find, et non à la sauvegarde
        entityManager.detach(finalParent);
        Assertions.assertThrows(BackDuplicateEventNameException.class, () -> dtoService.save(finalParent));
    }

    @Test
    @Order(17)
    void testUpdateSubEventNameConflict(@Autowired EntityManager entityManager) {
        var parent = eventUtils.createBasicEntity();
        eventUtils.addSubEvent(parent);
        parent = dtoService.save(parent);

        eventUtils.addSubEvent(parent);
        parent = dtoService.save(parent);

        var subEvent1 = parent.getSubEvents().getFirst();
        var subEvent2 = parent.getSubEvents().get(1);
        subEvent2.setEventName(subEvent1.getEventName());

        Event finalParent = parent;
        // obligé pour ne pas qu'hibernate flush les modifications de l'entité à son prochain find, et non à la sauvegarde
        entityManager.detach(finalParent);
        Assertions.assertThrows(BackDuplicateEventNameException.class, () -> dtoService.save(finalParent));
    }

    @Test
    @Order(18)
    void testFindSubEvent() {
        var parent = eventUtils.createBasicEntity();
        var subEvent1 = eventUtils.addSubEvent(parent);
        var subEvent2 = eventUtils.addSubEvent(parent);

        dtoService.save(parent);

        var result = dtoService.findByEventName(parent.getId(), subEvent1.getEventName()).orElseThrow(() -> new RuntimeException("Aucun objet trouvé."));
        Assertions.assertEquals(subEvent1.getId(), result.getId());
    }

    @Test
    @Order(19)
    void testFindSubEventWithParentName() {
        var parent = eventUtils.createBasicEntity();
        var subEvent1 = eventUtils.addSubEvent(parent);
        var subEvent2 = eventUtils.addSubEvent(parent);

        dtoService.save(parent);

        var result = dtoService.findByEventName(parent.getId(), parent.getEventName());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(20)
    void testFindSubEventWithSubEventIdName() {
        var parent = eventUtils.createBasicEntity();
        var subEvent1 = eventUtils.addSubEvent(parent);
        var subEvent2 = eventUtils.addSubEvent(parent);

        dtoService.save(parent);

        var result = dtoService.findByEventName(subEvent1.getId(), subEvent1.getEventName());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(21)
    void testFindSubEventWithSubEventNull() {
        var parent = eventUtils.createBasicEntity();
        var subEvent1 = eventUtils.addSubEvent(parent);
        var subEvent2 = eventUtils.addSubEvent(parent);

        dtoService.save(parent);

        var result = dtoService.findByEventName(subEvent1.getId(), null);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(22)
    void testRemoveParticipant() {
        var event = eventUtils.createBasicEntity();
        var member1 = eventUtils.addDiscordMember(event);
        var member2 = eventUtils.addDiscordMember(event);
        Assertions.assertEquals(2, event.getParticipants().size());

        event.removeParticipant(member1);
        Assertions.assertEquals(1, event.getParticipants().size());
    }

    @Test
    @Order(23)
    void testAddParticipants() {
        var event = eventUtils.createBasicEntity();
        var member1 = discordMemberUtils.createBasicEntity();
        var member2 = discordMemberUtils.createBasicEntity();

        event.addParticipants(List.of(member1, member2));
        Assertions.assertEquals(2, event.getParticipants().size());
    }

    @Test
    @Order(24)
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
    @Order(24)
    void testAddParticipantNull() {
        var event = eventUtils.createBasicEntity();

        var result = event.addParticipant(null);
        Assertions.assertFalse(result);
        Assertions.assertEquals(0, event.getParticipants().size());
    }

    @Test
    @Order(25)
    void testAddParticipantsNull() {
        var event = eventUtils.createBasicEntity();

        var result = event.addParticipants(null);
        Assertions.assertFalse(result);
        Assertions.assertEquals(0, event.getParticipants().size());
    }

    @Test
    @Order(26)
    void testSetParticipantsNull() {
        var event = eventUtils.createBasicEntity();
        var member = discordMemberUtils.createBasicEntity();

        event.addParticipant(member);
        event.setParticipants(null);
        Assertions.assertEquals(0, event.getParticipants().size());
    }

}
