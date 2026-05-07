package app.web.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LightPojoTodoEntry {

    private String name;
    private String todo;
    private List<UUID> participants = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public List<UUID> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UUID> participants) {
        this.participants = participants;
    }
}
