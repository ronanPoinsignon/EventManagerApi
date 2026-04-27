package app.web.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PojoEvent extends PojoEntity {

    private String eventName;
    private LocalDateTime creationDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private List<PojoEvent> subEvents;
    @JsonIgnore
    private PojoEvent parentEvent;
    private List<PojoDiscordMember> participants;
    private Map<String, List<PojoDiscordMember>> todoListMap;
    private String tricountUrl;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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
        this.subEvents = subEvents;
    }

    public PojoEvent getParentEvent() {
        return parentEvent;
    }

    public void setParentEvent(PojoEvent parentEvent) {
        this.parentEvent = parentEvent;
    }

    public List<PojoDiscordMember> getParticipants() {
        return participants;
    }

    public void setParticipants(List<PojoDiscordMember> participants) {
        this.participants = participants;
    }

    public Map<String, List<PojoDiscordMember>> getTodoListMap() {
        return todoListMap;
    }

    public void setTodoListMap(Map<String, List<PojoDiscordMember>> todoListMap) {
        this.todoListMap = todoListMap;
    }

    public String getTricountUrl() {
        return tricountUrl;
    }

    public void setTricountUrl(String tricountUrl) {
        this.tricountUrl = tricountUrl;
    }

    @Override
    public String toString() {
        return "PojoEvent{" +
                "eventName='" + eventName + '\'' +
                ", creationDate=" + creationDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", location='" + location + '\'' +
                ", subEvents=" + subEvents +
                ", participants=" + participants +
                ", todoList=" + todoList +
                ", tricountUrl='" + tricountUrl + '\'' +
                '}';
    }
}
