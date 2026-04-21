package app.web.controller;

import app.web.api.EventServiceApi;
import app.web.pojo.PojoEvent;
import org.springframework.web.bind.annotation.*;

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
    public PojoEvent addSubEvent(int parentEventId, @RequestBody PojoEvent event) {
        return eventService.addSubEvent(parentEventId, event);
    }
}
