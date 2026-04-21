package app.web.pojo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PojoEvent extends PojoEntity {

    private String eventName;
    private Date creationDate;
    private Date startDate;
    private Date endDate;
    private String location;
    private List<PojoEvent> subEvents;
    private PojoEvent parentEvent;
    private List<PojoDiscordMember> participants;
    private Map<String, List<PojoDiscordMember>> todoList;
    private String tricountUrl;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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

    public Map<String, List<PojoDiscordMember>> getTodoList() {
        return todoList;
    }

    public void setTodoList(Map<String, List<PojoDiscordMember>> todoList) {
        this.todoList = todoList;
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
