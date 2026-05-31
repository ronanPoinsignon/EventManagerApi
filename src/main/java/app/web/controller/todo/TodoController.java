package app.web.controller.todo;

import app.web.api.TodoServiceApi;
import app.web.pojo.PojoTodoEntry;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoServiceApi todoServiceApi;

    public TodoController(TodoServiceApi todoServiceApi) {
        this.todoServiceApi = todoServiceApi;
    }

    @DeleteMapping("/")
    public PojoTodoEntry delete(@RequestParam("todoId") long todoId) {
        return todoServiceApi.deleteTodo(todoId);
    }

    @PostMapping("/done")
    public PojoTodoEntry setDone(@RequestParam("todoId") long todoId, @RequestParam("done") boolean done) {
        return todoServiceApi.setDone(todoId, done);
    }

    @PostMapping("/setUsers")
    public PojoTodoEntry setParticipants(@RequestParam("todoId") long todoId, @RequestParam("userIds") List<UUID> userIds) {
        return todoServiceApi.setParticipants(todoId, userIds);
    }
}
