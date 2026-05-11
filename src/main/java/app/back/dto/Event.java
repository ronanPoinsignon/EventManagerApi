package app.back.dto;

import app.back.entityname.Contrainte;
import app.back.entityname.EntityTable;
import app.back.exception.BackBadRequestException;
import app.back.exception.BackForbiddenException;
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
    @Column(name = "owner", nullable = false)
    private UUID ownerUserId;

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

    @ManyToOne
    private Event parentEvent;

    private transient boolean shouldUpdateParticipants;

    private final Set<UUID> participants = new HashSet<>();

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

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
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

    public TodoEntry addTodo(String name, String todo, Collection<UUID> userIdCollection) {
        if(userIdCollection == null) {
            userIdCollection = new ArrayList<>();
        }

        var todoEntry = addTodo(name, todo);
        var result = todoEntry.addUserIds(userIdCollection);
        shouldUpdateTodos |= result;
        return todoEntry;
    }

    public TodoEntry addTodo(String name, String todo, UUID userId) {
        if(userId == null) {
            throw new BackBadRequestException("Le userId ne peut être null");
        }

        var todoEntry = addTodo(name, todo);
        var result = todoEntry.addUserId(userId);
        shouldUpdateTodos |= result;
        return todoEntry;
    }

    public TodoEntry addTodo(String name, String todo) {
        if(name == null || name.isBlank()) {
            throw new BackBadRequestException("Le champ name ne peut être null ou vide.");
        }
        if(todo == null || todo.isBlank()) {
            throw new BackBadRequestException("Le champ todo ne peut être null ou vide.");
        }

        var result =  findByTodo(name).orElseGet(() -> {
            var entry = new TodoEntry(name, todo);
            entry.setEvent(this);
            this.todoListEntries.add(entry);
            return entry;
        });
        result.setTodoValue(todo);

        shouldUpdateTodos = true;

        return result;
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
        if(todoListEntries == null || todoListEntries.isEmpty()) {
            this.shouldUpdateTodos = !this.todoListEntries.isEmpty();
            this.todoListEntries.clear();
            return;
        }
        
        this.todoListEntries.clear();
        todoListEntries.forEach(todo -> addTodo(todo.getTodoName(), todo.getTodoValue(), todo.getuserIds()));
    }

    public TodoEntry findTodoEntryByName(String name) {
        return this.todoListEntries.stream()
                .filter(todo -> todo.getTodoName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean shouldUpdateParticipants() {
        return shouldUpdateParticipants;
    }

    public Set<UUID> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    public boolean addParticipant(UUID userId) {
        if(userId == null) {
            throw new BackBadRequestException("Le userId ne peut être null");
        }

        var result = this.participants.add(userId);
        shouldUpdateParticipants |= result;
        return result;
    }

    public boolean addParticipants(Collection<UUID> userIdCollection) {
        if(userIdCollection == null || userIdCollection.isEmpty()) {
            return false;
        }

        var result = this.participants.addAll(userIdCollection);
        shouldUpdateParticipants |= result;
        return result;
    }

    public boolean removeParticipant(UUID userId) {
        return removeParticipants(List.of(userId));
    }

    public boolean removeParticipants(Collection<UUID> userIdCollection) {
        if(userIdCollection == null || userIdCollection.isEmpty()) {
            return false;
        }

        var temp = new ArrayList<>(userIdCollection);
        var result = this.participants.removeIf(temp::contains);
        shouldUpdateParticipants |= result;
        return result;
    }

    public void setParticipants(Collection<UUID> userIdCollection) {
        if(userIdCollection == null || userIdCollection.isEmpty()) {
            shouldUpdateParticipants = !this.participants.isEmpty();
            this.participants.clear();
            return;
        }

        var temp = new ArrayList<>(userIdCollection);
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
        checkSubEventBeforeAdd(event);

        event.setParentEvent(this);
        shouldUpdateSubEvents = true;
        return this.subEvents.add(event);
    }

    private void checkSubEventBeforeAdd(Event event) {
        if(event == null) {
            throw new BackBadRequestException("L'événement ne peut être null");
        }
        if(event.getParentEvent() != null && !Objects.equals(event.getParentEvent().getId(), this.getId())) {
            throw new BackForbiddenException("Ce sous événement a déjà un parent");
        }

        Event parent = this;
        while((parent = parent.getParentEvent()) != null) {
            // premier check pour savoir s'il y a ajout récursif avant de sauvegarder l'objet global
            if(parent == event || parent.getId() != null && event.getId() != null && parent.getId().equals(event.getId())) {
                throw new BackForbiddenException("l'événement à ajouter est déjà parent d'un événement hiérarchique supérieur.");
            }
        }
    }

    public boolean removeSubEvent(Event event) {
        var result = this.subEvents.remove(event);
        shouldUpdateSubEvents |= result;
        return result;
    }

    public void setSubEvents(List<Event> subEvents) {
        if(subEvents == null || subEvents.isEmpty()) {
            shouldUpdateSubEvents = !this.subEvents.isEmpty();
            this.subEvents.clear();
            return;
        }

        var temp = new ArrayList<>(subEvents);
        for(Event subEvent : temp) {
            checkSubEventBeforeAdd(subEvent);
        }
        this.subEvents.clear();
        for(Event event : temp) {
            this.addSubEvent(event);
        }
        shouldUpdateSubEvents = true;
    }

    public Event getParentEvent() {
        return parentEvent;
    }

    private void setParentEvent(Event parentEvent) {
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
