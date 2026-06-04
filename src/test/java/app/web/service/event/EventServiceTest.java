package app.web.service.event;

import app.utils.EventUtils;
import app.web.api.EventServiceApi;
import app.web.exception.NotFoundException;
import app.web.pojo.PojoEvent;
import app.web.service.BasicTestService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class EventServiceTest extends BasicTestService<PojoEvent, EventServiceApi> {

    private final EventUtils eventUtils;

    public EventServiceTest(@Autowired EventServiceApi eventService, @Autowired EventUtils eventUtils) {
        super(eventService);
        this.eventUtils = eventUtils;
    }

    @Override
    protected PojoEvent createBasicPojo() {
        return eventUtils.createBasicPojo();
    }

    @Test
    @Order(1)
    void testFindByEventName() {
        var event = createBasicPojo();
        event = service.save(event);

        var result = service.findByEventName(event.getEventName());
        Assertions.assertEquals(event.getId(), result.getId());
    }

    @Test
    @Order(2)
    void testFindByEventNameNotFound() {
        Assertions.assertThrows(NotFoundException.class, () ->  service.findByEventName("test"));
    }

    @Test
    @Order(3)
    void testFindAllBefore() {
        var event = createBasicPojo();
        event.setStartDate(Instant.now());
        event.setEndDate(null);
        event = service.save(event);

        var result = service.findAllBeforeEnd(Instant.now().minus(1, ChronoUnit.DAYS));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(event.getId(), result.getFirst().getId());
    }

    @Test
    @Order(4)
    void testFindAllBeforeEmpty() {
        var event = createBasicPojo();
        event.setStartDate(Instant.now());
        event.setEndDate(null);
        event = service.save(event);

        var result = service.findAllBeforeEnd(Instant.now().plus(1, ChronoUnit.DAYS));
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(5)
    void testGetLast() {
        var event1 = createBasicPojo();
        event1.setStartDate(Instant.now());
        event1.setEndDate(null);
        event1 = service.save(event1);

        var result = service.getLast();
        Assertions.assertEquals(event1.getId(), result.getId());

        var event2 = createBasicPojo();
        event2.setStartDate(Instant.now());
        event2.setEndDate(null);
        event2 = service.save(event2);

        result = service.getLast();
        Assertions.assertEquals(event2.getId(), result.getId());
    }

    @Test
    @Order(6)
    void testGetLastNotFound() {
        Assertions.assertThrows(NotFoundException.class, service::getLast);
    }

}
