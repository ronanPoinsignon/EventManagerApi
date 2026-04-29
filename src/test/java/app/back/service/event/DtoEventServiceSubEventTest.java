package app.back.service.event;

import app.back.dto.Event;
import app.back.exception.BackBadRequestException;
import app.back.exception.BackForbiddenException;
import app.back.exception.duplicate.event.BackDuplicateEventNameException;
import app.back.service.DtoEventService;
import app.utils.EventUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class DtoEventServiceSubEventTest {

    private final EventUtils eventUtils;
    private final DtoEventService dtoService;

    public DtoEventServiceSubEventTest(@Autowired DtoEventService dtoService, @Autowired EventUtils eventUtils) {
        this.dtoService = dtoService;
        this.eventUtils = eventUtils;
    }

    @Test
    @Order(1)
    void testLastWithSubEvent() {
        var event = eventUtils.createBasicEntity();
        dtoService.save(event);
        var parent = eventUtils.createBasicEntity();
        dtoService.save(parent);
        eventUtils.addSubEvent(parent);
        dtoService.save(parent);

        var result = dtoService.getLast().orElseThrow(() -> new RuntimeException("Aucun objet trouvé."));
        Assertions.assertEquals(parent.getId(), result.getId());
    }


    @Test
    @Order(2)
    void testUpdateSubEvent() {
        var parent = eventUtils.createBasicEntity();
        eventUtils.addSubEvent(parent);
        parent = dtoService.save(parent);
        parent.getSubEvents().getFirst().setEventName("test");
        parent = dtoService.save(parent);
        Assertions.assertEquals("test", parent.getSubEvents().getFirst().getEventName());
    }

    @Test
    @Order(3)
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
    @Order(4)
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
    @Order(5)
    void testFindSubEvent() {
        var parent = eventUtils.createBasicEntity();
        var subEvent1 = eventUtils.addSubEvent(parent);
        var subEvent2 = eventUtils.addSubEvent(parent);

        dtoService.save(parent);

        var result = dtoService.findByEventName(parent.getId(), subEvent1.getEventName()).orElseThrow(() -> new RuntimeException("Aucun objet trouvé."));
        Assertions.assertEquals(subEvent1.getId(), result.getId());
    }

    @Test
    @Order(6)
    void testFindSubEventWithParentName() {
        var parent = eventUtils.createBasicEntity();
        var subEvent1 = eventUtils.addSubEvent(parent);
        var subEvent2 = eventUtils.addSubEvent(parent);

        dtoService.save(parent);

        var result = dtoService.findByEventName(parent.getId(), parent.getEventName());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(7)
    void testFindSubEventWithSubEventIdName() {
        var parent = eventUtils.createBasicEntity();
        var subEvent1 = eventUtils.addSubEvent(parent);
        var subEvent2 = eventUtils.addSubEvent(parent);

        dtoService.save(parent);

        var result = dtoService.findByEventName(subEvent1.getId(), subEvent1.getEventName());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(8)
    void testFindSubEventWithSubEventNull() {
        var parent = eventUtils.createBasicEntity();
        var subEvent1 = eventUtils.addSubEvent(parent);
        var subEvent2 = eventUtils.addSubEvent(parent);

        dtoService.save(parent);

        var result = dtoService.findByEventName(subEvent1.getId(), null);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(9)
    void testAddSubEventNull() {
        var parent = eventUtils.createFullEntity();
        Assertions.assertThrows(BackBadRequestException.class, () -> parent.addSubEvent(null));
    }

    @Test
    @Order(10)
    void testSetSubEventNull() {
        var parent = eventUtils.createFullEntity();
        Assertions.assertEquals(1, parent.getSubEvents().size());

        parent.setSubEvents(null);
        Assertions.assertTrue(parent.getSubEvents().isEmpty());
    }

    @Test
    @Order(11)
    void testMoveSubEvent() {
        var parent1 = eventUtils.createBasicEntity();
        parent1.setId(1L);
        var parent2 = eventUtils.createBasicEntity();
        parent2.setId(2L);
        var subEvent = eventUtils.createBasicEntity();
        parent1.addSubEvent(subEvent);
        Assertions.assertThrows(BackForbiddenException.class, () -> parent2.addSubEvent(subEvent));
        Assertions.assertTrue(parent2.getSubEvents().isEmpty());
        Assertions.assertEquals(1, parent1.getSubEvents().size());
    }

    @Test
    @Order(12)
    void testAddRecursivity() {
        var event1 = eventUtils.createBasicEntity();
        var event2 = eventUtils.createBasicEntity();
        event1.addSubEvent(event2);
        Assertions.assertThrows(BackForbiddenException.class, () -> event2.addSubEvent(event1));
    }

}
