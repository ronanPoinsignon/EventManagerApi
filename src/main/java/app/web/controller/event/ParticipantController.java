package app.web.controller.event;

import app.web.api.EventServiceApi;
import app.web.pojo.PojoEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events/participants")
public class ParticipantController {

    private final EventServiceApi eventService;

    public ParticipantController(EventServiceApi eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/add")
    public PojoEvent addTo(@RequestParam(value = "eventId") long eventId,
                           @RequestParam(value = "discordMemberIds", required = false) List<Long> discordMemberIds) {
        return eventService.addTo(eventId, discordMemberIds);
    }

    @PostMapping("/remove")
    public PojoEvent removeTo(@RequestParam(value = "eventId") long eventId,
                              @RequestParam(value = "discordMemberIds", required = false) List<Long> discordMemberIds) {
        return eventService.removeTo(eventId, discordMemberIds);
    }

}
