package app.back.dto;

import app.back.entityname.Contrainte;
import app.back.entityname.EntityTable;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = EntityTable.TODO_ENTRY, uniqueConstraints = @UniqueConstraint(columnNames = {"name", "event_id"}, name = Contrainte.TODO_DUPLICATE_NAME))
public class TodoEntry extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String todoName;

    @Column(name = "todo", nullable = false)
    private String todoValue;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private final Set<UUID> userIdSet = new HashSet<>();

    @Column(name = "wasDone")
    private boolean isDone;

    public TodoEntry() {

    }

    public TodoEntry(String todoName, String todoValue, Collection<UUID> userIdSet) {
        if(userIdSet == null) {
            userIdSet = new ArrayList<>();
        }
        this.todoName = todoName;
        this.todoValue = todoValue;
        this.userIdSet.addAll(userIdSet);
    }

    public TodoEntry(String todoName, String todoValue, UUID userId) {
        this(todoName, todoValue, Set.of(userId));
    }

    public TodoEntry(String todoName, String todoValue) {
        this(todoName, todoValue, Set.of());
    }

    public String getTodoName() {
        return todoName;
    }

    public void setTodoName(String todoName) {
        this.todoName = todoName;
    }

    public String getTodoValue() {
        return todoValue;
    }

    public void setTodoValue(String todoValue) {
        this.todoValue = todoValue;
    }

    public Set<UUID> getuserIds() {
        return Collections.unmodifiableSet(userIdSet);
    }

    public boolean addUserId(UUID userId) {
        return this.userIdSet.add(userId);
    }

    public boolean addUserIds(Collection<UUID> userIdCollection) {
        if(userIdCollection == null) {
            return false;
        }

        return this.userIdSet.addAll(userIdCollection);
    }

    public boolean removeUserId(UUID userId) {
        if(userId == null) {
            return false;
        }

        return removeUserIds(List.of(userId));
    }

    public boolean removeUserIds(Collection<UUID> userIdCollection) {
        if(userIdCollection == null) {
            return false;
        }

        var temp = new ArrayList<>(userIdCollection);
        return this.userIdSet.removeIf(temp::contains);
    }

    public void setUserIdSet(Collection<UUID> userIdSet) {
        if(userIdSet == null) {
            userIdSet = new ArrayList<>();
        }
        this.userIdSet.clear();
        this.userIdSet.addAll(userIdSet);
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean setDone(boolean isDone) {
        var before = this.isDone;
        this.isDone = isDone;

        return before != this.isDone;
    }
}
