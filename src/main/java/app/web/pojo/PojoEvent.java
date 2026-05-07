package app.web.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PojoEvent extends PojoEntity {

    private String eventName;
    private PojoUser ownerUser;
    private LocalDateTime creationDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private List<PojoEvent> subEvents;
    @JsonIgnore
    private PojoEvent parentEvent;
    private List<PojoUser> participants;
    private List<PojoTodoEntry> todoList;
    private String tricountUrl;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public PojoUser getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(PojoUser ownerUser) {
        this.ownerUser = ownerUser;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<PojoEvent> getSubEvents() {
        return subEvents;
    }

    public void setSubEvents(List<PojoEvent> subEvents) {
        this.subEvents = new ArrayList<>(subEvents);
    }

    public PojoEvent getParentEvent() {
        return parentEvent;
    }

    public void setParentEvent(PojoEvent parentEvent) {
        this.parentEvent = parentEvent;
    }

    public List<PojoUser> getParticipants() {
        return participants;
    }

    public void setParticipants(Collection<? extends PojoUser> participants) {
        this.participants = new ArrayList<>(participants);
    }

    public List<PojoTodoEntry> getTodoList() {
        return todoList;
    }

    public void setTodoList(List<PojoTodoEntry> todoList) {
        this.todoList = new ArrayList<>(todoList);
    }

    public String getTricountUrl() {
        return tricountUrl;
    }

    public void setTricountUrl(String tricountUrl) {
        this.tricountUrl = tricountUrl;
    }
}
