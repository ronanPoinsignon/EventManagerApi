package app.web.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class PojoTodoEntry extends PojoEntity {

    private String name;
    private String todoValue;
    private List<PojoDiscordMember> discordMembers;
    @JsonIgnore
    private PojoEvent event;
    private boolean isDone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTodoValue() {
        return todoValue;
    }

    public void setTodoValue(String todoValue) {
        this.todoValue = todoValue;
    }

    public List<PojoDiscordMember> getDiscordMembers() {
        return discordMembers;
    }

    public void setDiscordMembers(List<PojoDiscordMember> discordMembers) {
        this.discordMembers = discordMembers;
    }

    public PojoEvent getEvent() {
        return event;
    }

    public void setEvent(PojoEvent event) {
        this.event = event;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
