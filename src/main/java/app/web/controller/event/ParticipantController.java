package app.web.controller.event;

import app.web.api.EventServiceApi;
import app.web.pojo.PojoEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events/participants")
public class ParticipantController {

    private final EventServiceApi eventService;

    public ParticipantController(EventServiceApi eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/add")
    public PojoEvent addTo(@RequestParam(value = "eventId") long eventId,
                           @RequestParam(value = "userIds", required = false) List<UUID> userIds) {
        return eventService.addTo(eventId, userIds);
    }

    @PostMapping("/remove")
    public PojoEvent removeTo(@RequestParam(value = "eventId") long eventId,
                              @RequestParam(value = "userIds", required = false) List<UUID> userIds) {
        return eventService.removeTo(eventId, userIds);
    }

}
