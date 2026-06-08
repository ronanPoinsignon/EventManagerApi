package app.back.dto.notification;

import app.back.dto.AbstractEntity;
import app.back.dto.Event;
import app.back.entityname.Contrainte;
import app.back.entityname.EntityTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyDiscriminatorValue;
import org.hibernate.annotations.AnyDiscriminatorValues;
import org.hibernate.annotations.AnyKeyJavaClass;

import java.time.Instant;

@Entity
@Table(name = EntityTable.SCHEDULE_NOTIFICATION)
public class ScheduleNotification extends AbstractEntity {

    @Column(name = Contrainte.NOTIFICATION_EXECUTION_DATE, nullable = false)
    private Instant executionDate;

    @Any
    @AnyKeyJavaClass(Long.class)
    @JoinColumn(name = "related_id")
    @Column(name = Contrainte.NOTIFICATION_ENTITY_TYPE, nullable = false)
    @AnyDiscriminatorValues({
            @AnyDiscriminatorValue(discriminator = EntityType.EVENT_TYPE, entity = Event.class)
    })
    private AbstractEntity entity;

    @Column(name = "notified")
    private boolean notified;

    // constructeur vide pour hibernate
    public ScheduleNotification() {

    }

    public ScheduleNotification(Instant executionDate, AbstractEntity entity) {
        this.executionDate = executionDate;
        this.entity = entity;
    }

    public Instant getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Instant executionDate) {
        this.executionDate = executionDate;
    }

    public AbstractEntity getEntity() {
        return entity;
    }

    public void setEntity(AbstractEntity entity) {
        this.entity = entity;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

}
