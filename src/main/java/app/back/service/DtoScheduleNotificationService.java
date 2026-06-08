package app.back.service;

import app.back.dto.notification.ScheduleNotification;
import app.back.entityname.EntityTable;
import app.back.repository.ScheduleNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class DtoScheduleNotificationService extends DtoAbstractEntityService<ScheduleNotification, ScheduleNotificationRepository> {

    protected DtoScheduleNotificationService(ScheduleNotificationRepository repository) {
        super(repository);
    }

    @Override
    protected void update(ScheduleNotification entityToSave, ScheduleNotification dbEntity) {
        // rien à update ici
    }

    @Override
    public String getTableName() {
        return EntityTable.SCHEDULE_NOTIFICATION;
    }

    public int deleteNotificationByRelatedId(String entity_type, long id) {
        return repository.deleteNotificationByRelatedId(entity_type, id);
    }

    @Transactional
    public List<ScheduleNotification> findNotifications(Instant before, int dayNumber) {
        return repository.findNotifications(before, dayNumber);
    }

    public int updateDateByRelatedId(long relatedId, long microseconds) {
        return repository.updateDateByRelatedId(relatedId, microseconds);
    }

}
