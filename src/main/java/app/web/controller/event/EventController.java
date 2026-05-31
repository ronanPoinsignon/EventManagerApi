package app.web.controller.event;

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

    @PostMapping("/discord/save")
    public PojoEvent discordUpdate(@RequestParam(value = "parentEventName", required = false) String parentEventName,
                                   @RequestBody PojoEvent event) {
        return eventService.discordSave(event, parentEventName);
    }

    @GetMapping("/findById")
    public PojoEvent findById(@RequestParam("id") long id) {
        return eventService.findOne(id);
    }

    @GetMapping("/findByEventName")
    public PojoEvent findById(@RequestParam("name") String name, @RequestParam(value = "parentName", required = false) String parentName) {
        return eventService.findByEventName(parentName, name);
    }

    @GetMapping("/findActive")
    public List<PojoEvent> findAllBeforeEnd(@RequestParam(name = "date", required = false) LocalDateTime date) {
        return eventService.findAllBeforeEnd(date);
    }

    @GetMapping("/findAll")
    public List<PojoEvent> findAll() {
        return eventService.findAll();
    }

    @GetMapping("/getLast")
    public PojoEvent getLast() {
        return eventService.getLast();
    }

    @DeleteMapping("/delete")
    public PojoEvent delete(@RequestParam("eventId") long eventId) {
        return eventService.delete(eventId);
    }

}
