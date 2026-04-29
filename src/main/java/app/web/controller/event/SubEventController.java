package app.web.controller.event;

import app.web.api.EventServiceApi;
import app.web.pojo.PojoEvent;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events/subEvent")
public class SubEventController {

    private final EventServiceApi eventService;

    public SubEventController(EventServiceApi eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/addSubEvent")
    public PojoEvent addSubEvent(@RequestParam("parentEventId") long parentEventId, @RequestBody PojoEvent event) {
        return eventService.addSubEvent(parentEventId, event);
    }

    @PostMapping("/removeSubEvent")
    public PojoEvent removeSubEvent(@RequestParam("parentEventId") long parentEventId, @RequestParam("subEventName") String subEventName) {
        return eventService.removeSubEvent(parentEventId, subEventName);
    }

}
