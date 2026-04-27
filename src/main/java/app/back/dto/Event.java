package app.back.dto;

import jakarta.persistence.*;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "events", uniqueConstraints = @UniqueConstraint(columnNames = { "parent_event_id", "event_name" }))
public class Event extends AbstractEntity {

    @Basic
    @Column(name = "eventName", nullable = false)
    private String eventName;

    @Basic
    @Column(name = "creationDate", nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Basic
    @Column(name = "startDate")
    private LocalDateTime startDate;

    @Basic
    @Column(name = "endDate")
    private LocalDateTime endDate;

    @Basic
    @Column(name = "location")
    private String location;

    @Transient
    private boolean shouldUpdateSubEvents;

    @OneToMany(mappedBy = "parentEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> subEvents = new ArrayList<>();

    @Transient
    private boolean shouldUpdateParentEvent;

    @ManyToOne
    private Event parentEvent;

    @Transient
    private boolean shouldUpdateParticipants;

    @ManyToMany
    @JoinTable(
            name = "discord_member_id",
            joinColumns = @JoinColumn(name = "participant_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id", "event_id"})
    )
    private Set<DiscordMember> participants = new HashSet<>();

    @Transient
    private boolean shouldUpdateTodos;

    @ManyToMany
    @JoinTable(
            name = "todo_list_entries",
            joinColumns = @JoinColumn(name = "todo_entry_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<TodoEntry> todoListEntries = new ArrayList<>();

    @Column(name = "tricount")
    private String tricountUrl;

    public String getTricountUrl() {
        return tricountUrl;
    }

    public void setTricountUrl(String tricountUrl) {
        this.tricountUrl = tricountUrl;
    }

    public boolean isShouldUpdateTodos() {
        return shouldUpdateTodos;
    }

    public List<TodoEntry> getTodoList() {
        return Collections.unmodifiableList(todoListEntries);
    }

    public boolean addTodo(TodoEntry todo) {
        var result = todoListEntries.add(todo);
        shouldUpdateTodos |= result;
        return result;
    }

    public boolean addTodos(Collection<TodoEntry> todoCollection) {
        var result = todoListEntries.addAll(todoCollection);
        shouldUpdateTodos |= result;
        return result;
    }

    public boolean removeTodo(TodoEntry todo) {
        var result = todoListEntries.remove(todo);
        shouldUpdateTodos |= result;
        return result;
    }

    public void setTodoList(List<TodoEntry> todoListEntries) {
        var temp = new ArrayList<>(todoListEntries);
        this.todoListEntries.clear();
        this.todoListEntries.addAll(temp);
        shouldUpdateTodos = true;
    }

    public boolean shouldUpdateParticipants() {
        return shouldUpdateParticipants;
    }

    public Set<DiscordMember> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    public boolean addParticipant(DiscordMember discordMember) {
        var result = this.participants.add(discordMember);
        shouldUpdateParticipants |= result;
        return result;
    }

    public boolean addParticipants(Collection<DiscordMember> discordMemberCollection) {
        var result = this.participants.addAll(discordMemberCollection);
        shouldUpdateParticipants |= result;
        return result;
    }

    public boolean removeParticipant(DiscordMember discordMember) {
        var result = this.participants.remove(discordMember);
        shouldUpdateParticipants |= result;
        return result;
    }

    public void setParticipants(Collection<DiscordMember> participants) {
        var temp = new ArrayList<>(participants);
        this.participants.clear();
        this.participants.addAll(temp);
        shouldUpdateParticipants = true;
    }

    public boolean shouldUpdateSubEvents() {
        return this.shouldUpdateSubEvents;
    }

    public List<Event> getSubEvents() {
        return Collections.unmodifiableList(subEvents);
    }

    public boolean addSubEvent(Event event) {
        var result = this.subEvents.add(event);
        shouldUpdateSubEvents |= result;
        return result;
    }

    public boolean addSubEvents(Collection<Event> subEventCollection) {
        var result = this.subEvents.addAll(subEventCollection);
        shouldUpdateSubEvents |= result;
        return result;
    }

    public boolean removeSubEvent(Event event) {
        var result = this.subEvents.remove(event);
        shouldUpdateSubEvents |= result;
        return result;
    }

    public void setSubEvents(List<Event> subEvents) {
        var temp = new ArrayList<>(subEvents);
        this.subEvents.clear();
        this.subEvents.addAll(temp);
        shouldUpdateSubEvents = true;
    }

    public boolean shouldUpdateParentEvent() {
        return shouldUpdateParentEvent;
    }

    public Event getParentEvent() {
        return parentEvent;
    }

    public void setParentEvent(Event parentEvent) {
        if(parentEvent != null) {
            shouldUpdateParentEvent = true;
        } else {
            if(this.parentEvent != null) {
                shouldUpdateParentEvent = true;
            }
        }
        this.parentEvent = parentEvent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Map<String, Set<DiscordMember>> getTodoListMap() {
        Map<String, Set<DiscordMember>> result = new HashMap<>();
        if(todoListEntries == null) {
            return result;
        }

        for (TodoEntry entry : todoListEntries) {
            result.put(entry.getTodoValue(), entry.getDiscordMembers());
        }
        return result;
    }

    public List<TodoEntry> setTodoListFromMap(Map<String, List<DiscordMember>> todoListMap) {
        if(todoListMap == null) {
            return new ArrayList<>();
        }

        var temp = new HashMap<>(todoListMap);
        this.todoListEntries.clear();
        this.todoListEntries.addAll(temp.entrySet().stream().map(entry -> {
            var todo = entry.getKey();
            var discordMembers = entry.getValue();

            return new TodoEntry(todo, discordMembers);
        }).toList());

        return this.todoListEntries;
    }
}
