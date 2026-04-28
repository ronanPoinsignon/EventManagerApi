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

    @ManyToMany
    @JoinTable(
            name = "todo_participants",
            joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = @JoinColumn(name = "discord_member_id")
    )
    private final Set<DiscordMember> discordMemberSet = new HashSet<>();

    public TodoEntry() {

    }

    public TodoEntry(String todoName, String todoValue, Collection<DiscordMember> discordMemberSet) {
        this.todoName = todoName;
        this.todoValue = todoValue;
        this.discordMemberSet.addAll(discordMemberSet);
    }

    public TodoEntry(String todoName, String todoValue, DiscordMember discordMember) {
        this(todoName, todoValue, Set.of(discordMember));
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

    public Set<DiscordMember> getDiscordMembers() {
        return Collections.unmodifiableSet(discordMemberSet);
    }

    public boolean addDiscordMember(DiscordMember member) {
        return this.discordMemberSet.add(member);
    }

    public boolean addDiscordMembers(Collection<DiscordMember> discordMemberCollection) {
        return this.discordMemberSet.addAll(discordMemberCollection);
    }

    public boolean remove(DiscordMember discordMember) {
        return this.discordMemberSet.remove(discordMember);
    }

    public void setDiscordMemberSet(Collection<DiscordMember> discordMemberSet) {
        this.discordMemberSet.clear();
        this.discordMemberSet.addAll(discordMemberSet);
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
