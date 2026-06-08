package app.back.service.notification;

import app.back.dto.Event;
import app.back.dto.notification.ScheduleNotification;
import app.back.service.BasicDtoTestService;
import app.back.service.DtoScheduleNotificationService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class ScheduleNotificationTest extends BasicDtoTestService<ScheduleNotification, DtoScheduleNotificationService> {

    protected ScheduleNotificationTest(DtoScheduleNotificationService dtoService) {
        super(dtoService);
    }

    @Override
    protected ScheduleNotification createBasicObject() {
        return new ScheduleNotification(Instant.now(), new Event());
    }

}
