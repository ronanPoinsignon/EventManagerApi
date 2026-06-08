package app.web.service;

import app.back.dto.notification.ScheduleNotification;
import app.back.service.DtoScheduleNotificationService;
import app.web.pojo.PojoScheduleNotification;
import app.web.transform.TransformScheduleNotification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ScheduleNotificationService extends AbstractService<ScheduleNotification, PojoScheduleNotification, DtoScheduleNotificationService> {

    public ScheduleNotificationService(DtoScheduleNotificationService service, TransformScheduleNotification transform) {
        super(service, transform);
    }

    @Transactional
    public List<PojoScheduleNotification> find24hoursNotification() {
        return findNotifications(() -> getService().findNotifications(Instant.now(), 1));
    }

    @Transactional
    public List<PojoScheduleNotification> find7DaysNotification() {
        return findNotifications(() -> getService().findNotifications(Instant.now(), 7));
    }

    @Transactional
    public List<PojoScheduleNotification> find1MonthNotification() {
        return findNotifications(() -> getService().findNotifications(Instant.now(), 30));
    }

    @Transactional
    private List<PojoScheduleNotification> findNotifications(Supplier<List<ScheduleNotification>> findNotifs) {
        return findNotifs.get()
                .stream()
                .map(getTransform()::toPojo)
                .toList();
    }

}
