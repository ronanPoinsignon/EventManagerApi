package app.web.pojo;

import java.time.Instant;

public class PojoScheduleNotification extends PojoEntity {

    private Instant executionDate;
    private String entityType;
    private PojoEntity entity;

    public Instant getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Instant executionDate) {
        this.executionDate = executionDate;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public PojoEntity getEntity() {
        return entity;
    }

    public void setEntity(PojoEntity entity) {
        this.entity = entity;
    }

}
