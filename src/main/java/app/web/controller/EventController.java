package app.web.controller;

import app.web.api.EventServiceApi;
import app.web.pojo.PojoEvent;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventServiceApi eventService;

    public EventController(EventServiceApi eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/save")
    public PojoEvent create(@RequestBody PojoEvent event) {
        return eventService.save(event);
    }

    @GetMapping("/findById")
    public PojoEvent findById(long id) {
        return eventService.findOne(id);
    }

    @GetMapping("/findByEventName")
    public PojoEvent findById(String name) {
        return eventService.findByEventName(name);
    }

    @PostMapping("/addSubEvent")
    public PojoEvent addSubEvent(@RequestParam("parentEventId") long parentEventId, @RequestBody PojoEvent event) {
        return eventService.addSubEvent(parentEventId, event);
    }

    @PostMapping("/removeSubEvent")
    public PojoEvent removeSubEvent(@RequestParam("parentEventId") long parentEventId, @RequestParam("subEventName") String subEventName) {
        return eventService.removeSubEvent(parentEventId, subEventName);
    }

    @GetMapping("/findActive")
    public List<PojoEvent> findAllBeforeEnd(@RequestParam(required = false) LocalDateTime date) {
        return eventService.findAllBeforeEnd(date);
    }

    @GetMapping("/getLast")
    public PojoEvent getLast() {
        return eventService.getLast();
    }

    @PostMapping("/addTo")
    public PojoEvent addTo(@RequestParam(value = "eventId") long eventId, @RequestParam(value = "discordMemberIds", required = false) List<Long> discordMemberIds) {
        return eventService.addTo(eventId, discordMemberIds);
    }

    @PostMapping("/addTodo")
    public PojoEvent addTodo(@RequestParam(value = "eventId") long eventId, @RequestParam("todo") String todo, @RequestParam(value = "discordMemberIds", required = false) List<Long> discordMemberIds) {
        return eventService.addTodo(eventId, todo, discordMemberIds);
    }

}
