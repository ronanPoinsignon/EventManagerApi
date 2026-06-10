package app.web.pojo;

import java.time.Instant;
import java.util.List;

public class PojoScheduleNotification extends PojoEntity {

    private Instant executionDate;
    private String entityType;
    private PojoEntity entity;
    private List<PojoKeycloakUserAttributes> users;

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

    public List<PojoKeycloakUserAttributes> getUsers() {
        return users;
    }

    public void setUsers(List<PojoKeycloakUserAttributes> users) {
        this.users = users;
    }
}
