package app.web.controller;

import app.web.api.EventServiceApi;
import app.web.exception.BadRequestException;
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
    public PojoEvent addSubEvent(int parentEventId, @RequestBody PojoEvent event) {
        return eventService.addSubEvent(parentEventId, event);
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
    public PojoEvent addTo(@RequestParam(value = "eventId", required = false) String eventId, @RequestParam(value = "discordMemberIds", required = false) List<String> discordMemberIds) {
        if(eventId == null) {
            throw new BadRequestException("Le champ eventId ne peut être null.");
        }

        return eventService.addTo(Long.parseLong(eventId), discordMemberIds);
    }

}
