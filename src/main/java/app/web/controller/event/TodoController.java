package app.web.controller.event;

import app.web.api.EventServiceApi;
import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoEvent;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/addMembers")
    public PojoEvent addTodoMember(@RequestParam(value = "eventId") long eventId, @RequestParam("todoName") String todoName, @RequestParam(value = "discordMemberIds", required = false) List<Long> discordMemberIds) {
        return eventService.addTodoMembers(eventId, todoName, discordMemberIds);
    }

    @PostMapping("/removeMembers")
    public PojoEvent removeTodoMember(@RequestParam(value = "eventId") long eventId, @RequestParam("todoName") String todoName, @RequestParam(value = "discordMemberIds", required = false) List<Long> discordMemberIds) {
        return eventService.removeTodoMembers(eventId, todoName, discordMemberIds);
    }

    @PostMapping("/updateStatus")
    public PojoEvent updateTodoStatus(@RequestParam(value = "eventId") long eventId, @RequestParam("todoName") String todoName, @RequestParam("isDone") boolean isDone) {
        return eventService.updateTodoStatus(eventId, todoName, isDone);
    }

}
