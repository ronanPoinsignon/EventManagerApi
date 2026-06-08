package app.back.service.notification;

import app.back.service.DtoEventService;
import app.back.service.DtoScheduleNotificationService;
import app.utils.EventUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class EventScheduleNotificationTest {

    @Autowired
    private DtoEventService eventService;
    @Autowired
    private DtoScheduleNotificationService notificationService;
    @Autowired
    @Lazy
    private EventUtils eventUtils;

    @Test
    @Order(1)
    void findFromEvent() {
        var startDate = Instant.now().plus(21, ChronoUnit.DAYS);
        var event = eventUtils.createBasicEntity();
        event.setStartDate(startDate);
        event.setEndDate(null);
        event = eventService.save(event);
        var notifications = notificationService.findNotifications(startDate, 1);
        Assertions.assertEquals(2, notifications.size());
        Assertions.assertEquals(startDate.minus(7, ChronoUnit.DAYS), notifications.getFirst().getExecutionDate());
        Assertions.assertEquals(startDate.minus(1, ChronoUnit.DAYS), notifications.get(1).getExecutionDate());

        notifications = notificationService.findNotifications(startDate, 7);
        Assertions.assertEquals(1, notifications.size());
        Assertions.assertEquals(startDate.minus(7, ChronoUnit.DAYS), notifications.getFirst().getExecutionDate());
    }

    @Test
    @Order(2)
    void findUpdateFromEvent() {
        var startDate = Instant.now().plus(21, ChronoUnit.DAYS);
        var event = eventUtils.createBasicEntity();
        event.setStartDate(startDate);
        event.setEndDate(null);
        event = eventService.save(event);
        event.setStartDate(event.getStartDate().plus(2, ChronoUnit.DAYS));
        startDate = event.getStartDate();
        event = eventService.save(event);

        var notifications = notificationService.findNotifications(startDate, 1);
        Assertions.assertEquals(2, notifications.size());
        Assertions.assertEquals(startDate.minus(7, ChronoUnit.DAYS), notifications.getFirst().getExecutionDate());
        Assertions.assertEquals(startDate.minus(1, ChronoUnit.DAYS), notifications.get(1).getExecutionDate());
    }

    @Test
    @Order(3)
    void findFromEventBadDate() {
        var startDate = Instant.now().plus(21, ChronoUnit.DAYS);
        var event = eventUtils.createBasicEntity();
        event.setStartDate(startDate);
        event.setEndDate(null);
        event = eventService.save(event);

        var notifications = notificationService.findNotifications(startDate, 8);
        Assertions.assertEquals(0, notifications.size());
    }

}
