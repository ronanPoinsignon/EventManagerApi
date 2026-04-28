package app.web.transform;

import app.back.dto.Event;
import app.utils.EventUtils;
import app.web.pojo.PojoEvent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class TransformEventTest {

    @Autowired
    @Lazy
    private TransformEvent transformEvent;

    private final EventUtils eventUtils;

    public TransformEventTest(@Autowired EventUtils eventUtils) {
        this.eventUtils = eventUtils;
    }

    @Test
    @Order(1)
    void testTransformEntityToPojo() {
        var event = eventUtils.createFullEntity();
        var result = transformEvent.toPojo(event);
        EventUtils.compare(event, result);
    }

    @Test
    @Order(2)
    void testTransformEntityToPojoNull() {
        Event dm = null;
        var result = transformEvent.toPojo(dm);
        Assertions.assertNull(result);
    }

    @Test
    @Order(3)
    void testTransformEntityToPojoList() {
        var dm1 = eventUtils.createFullEntity();
        var dm2 = eventUtils.createFullEntity();
        var dmList = List.of(dm1, dm2);
        var result = transformEvent.toPojo(dmList);
        EventUtils.compare(dm1, result.getFirst());
        EventUtils.compare(dm2, result.get(1));
    }

    @Test
    @Order(4)
    void testTransformEntityToPojoListEmpty() {
        var dmList = List.<Event>of();
        var result = transformEvent.toPojo(dmList);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(5)
    void testTransformEntityToPojoListNull() {
        List<Event> dmList = null;
        var result = transformEvent.toPojo(dmList);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(6)
    void testTransformPojoToEntity() {
        var event = eventUtils.createFullPojo();
        var result = transformEvent.toDto(event);
        EventUtils.compare(event, result);
    }

    @Test
    @Order(7)
    void testTransformPojoToEntityNull() {
        PojoEvent event = null;
        var result = transformEvent.toDto(event);
        Assertions.assertNull(result);
    }

    @Test
    @Order(8)
    void testTransformPojoToEntityList() {
        var dm1 = eventUtils.createFullPojo();
        var dm2 = eventUtils.createFullPojo();
        var eventList = List.of(dm1, dm2);
        var result = transformEvent.toDto(eventList);
        EventUtils.compare(dm1, result.getFirst());
        EventUtils.compare(dm2, result.get(1));
    }

    @Test
    @Order(9)
    void testTransformPojoToEntityListEmpty() {
        var eventList = List.<PojoEvent>of();
        var result = transformEvent.toDto(eventList);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(10)
    void testTransformPojoToEntityListNull() {
        List<PojoEvent> eventList = null;
        var result = transformEvent.toDto(eventList);
        Assertions.assertTrue(result.isEmpty());
    }

}
