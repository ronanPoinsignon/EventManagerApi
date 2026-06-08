package app.back.repository;

import app.back.dto.notification.ScheduleNotification;
import app.back.entityname.Contrainte;
import app.back.entityname.EntityTable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;

import java.time.Instant;
import java.util.List;

public interface ScheduleNotificationRepository extends AbstractEntityRepository<ScheduleNotification> {

    @NativeQuery("select * from " + EntityTable.SCHEDULE_NOTIFICATION + " where " + Contrainte.NOTIFICATION_EXECUTION_DATE + " <= DATE_SUB(?1, INTERVAL ?2 DAY) && notified = false order by " + Contrainte.NOTIFICATION_EXECUTION_DATE + " asc")
    List<ScheduleNotification> findNotifications(Instant before, int dayNumber);

    @Modifying
    @NativeQuery("update " + EntityTable.SCHEDULE_NOTIFICATION + " set " + Contrainte.NOTIFICATION_EXECUTION_DATE + " = date_add(" + Contrainte.NOTIFICATION_EXECUTION_DATE + ", interval ?1 microsecond) where related_id = ?1")
    int updateDateByRelatedId(long relatedId, long microsecond);

    @Modifying
    @NativeQuery("delete from " + EntityTable.SCHEDULE_NOTIFICATION + " where related_id = ?2 && " + Contrainte.NOTIFICATION_ENTITY_TYPE + " = ?1")
    int deleteNotificationByRelatedId(String entityType, long id);

}
