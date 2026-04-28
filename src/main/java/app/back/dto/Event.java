package app.back.dto;

import app.back.entityname.Contrainte;
import app.back.entityname.EntityTable;
import app.web.exception.BadRequestException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = EntityTable.EVENT, uniqueConstraints = @UniqueConstraint(columnNames = { "parent_event_id", "event_name" }, name = Contrainte.EVENT_DUPLICATE_NAME))
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

    private transient boolean shouldUpdateSubEvents;

    @OneToMany(mappedBy = "parentEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Event> subEvents = new ArrayList<>();

    private transient boolean shouldUpdateParentEvent;

    @ManyToOne
    private Event parentEvent;

    private transient boolean shouldUpdateParticipants;

    @ManyToMany
    @JoinTable(
            name = "discord_member_id",
            joinColumns = @JoinColumn(name = "participant_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id", "event_id"}, name = Contrainte.EVENT_DUPLICATE_PARTICIPANT)
    )
    private final Set<DiscordMember> participants = new HashSet<>();

    private transient boolean shouldUpdateTodos;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TodoEntry> todoListEntries = new ArrayList<>();

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

    private Optional<TodoEntry> findByTodo(String todoName) {
        return todoListEntries.stream()
                .filter(t -> t.getTodoName().equals(todoName))
                .findFirst();
    }

    public TodoEntry addTodo(String name, String todo, Collection<DiscordMember> discordMemberCollection) {
        if(discordMemberCollection == null) {
            discordMemberCollection = new ArrayList<>();
        }

        var todoEntry = addTodo(name, todo);
        var result = todoEntry.addDiscordMembers(discordMemberCollection);
        shouldUpdateTodos |= result;
        return todoEntry;
    }

    public TodoEntry addTodo(String name, String todo, DiscordMember discordMember) {
        if(discordMember == null) {
            throw new BadRequestException("Le champ discordMember ne peut être null");
        }

        var todoEntry = addTodo(name, todo);
        var result = todoEntry.addDiscordMember(discordMember);
        shouldUpdateTodos |= result;
        return todoEntry;
    }

    public TodoEntry addTodo(String name, String todo) {
        if(name == null || name.isBlank()) {
            throw new BadRequestException("Le champ name ne peut être null ou vide.");
        }
        if(todo == null || todo.isBlank()) {
            throw new BadRequestException("Le champ todo ne peut être null ou vide.");
        }

        return findByTodo(name).orElseGet(() -> {
            var entry = new TodoEntry(name, todo);
            entry.setEvent(this);
            this.todoListEntries.add(entry);
            shouldUpdateTodos = true;
            return entry;
        });
    }

    public boolean removeTodo(String name) {
        var todoEntryOptional = findByTodo(name);
        if(todoEntryOptional.isEmpty()) {
            return false;
        }
        
        var result = this.todoListEntries.remove(todoEntryOptional.get());
        shouldUpdateTodos |= result;
        return result;
    }

    public void setTodoList(List<TodoEntry> todoListEntries) {
        if(todoListEntries == null) {
            todoListEntries = new ArrayList<>();
        }
        this.todoListEntries.clear();
        todoListEntries.forEach(todo -> addTodo(todo.getTodoName(), todo.getTodoValue(), todo.getDiscordMembers()));
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
        if(discordMemberCollection == null) {
            discordMemberCollection = new ArrayList<>();
        }
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
        if(participants == null) {
            participants = new ArrayList<>();
        }
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
        if(subEventCollection == null) {
            subEventCollection = new ArrayList<>();
        }
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
        if(subEvents == null) {
            subEvents = new ArrayList<>();
        }
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

}
