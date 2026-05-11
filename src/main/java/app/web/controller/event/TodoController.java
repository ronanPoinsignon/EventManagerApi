package app.web.controller.event;

import app.web.api.EventServiceApi;
import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoEvent;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events/todos")
public class TodoController {

    private final EventServiceApi eventService;

    public TodoController(EventServiceApi eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/addTodo")
    public PojoEvent addTodo(@RequestParam(value = "eventId") long eventId, @RequestBody LightPojoTodoEntry todoEntry) {
        return eventService.addTodo(eventId, todoEntry);
    }

    @PostMapping("/removeTodo")
    public PojoEvent addTodo(@RequestParam(value = "eventId") long eventId, @RequestParam("todoName") String todoName) {
        return eventService.removeTodo(eventId, todoName);
    }

    @PostMapping("/addUsers")
    public PojoEvent addTodoUsers(@RequestParam(value = "eventId") long eventId, @RequestParam("todoName") String todoName, @RequestParam(value = "userIds", required = false) List<UUID> userIdList) {
        return eventService.addTodoUsers(eventId, todoName, userIdList);
    }

    @PostMapping("/removeUsers")
    public PojoEvent removeTodoUsers(@RequestParam(value = "eventId") long eventId, @RequestParam("todoName") String todoName, @RequestParam(value = "userIds", required = false) List<UUID> userIdList) {
        return eventService.removeTodoUsers(eventId, todoName, userIdList);
    }

    @PostMapping("/updateStatus")
    public PojoEvent updateTodoStatus(@RequestParam(value = "eventId") long eventId, @RequestParam("todoName") String todoName, @RequestParam("isDone") boolean isDone) {
        return eventService.updateTodoStatus(eventId, todoName, isDone);
    }

}
