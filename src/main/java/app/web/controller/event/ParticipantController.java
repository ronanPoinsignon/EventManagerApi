package app.web.controller.event;

import app.web.api.EventServiceApi;
import app.web.pojo.PojoEvent;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/set")
    public PojoEvent set(@RequestParam(value = "eventId") long eventId,
                         @RequestParam(value = "userIds", required = false) List<UUID> userIds) {
        return eventService.setParticipant(eventId, userIds);
    }

    @PostMapping("/discord/add")
    public PojoEvent addDiscordTo(@RequestParam(value = "eventId") long eventId,
                                  @RequestParam(value = "userIds", required = false) List<Long> userIds) {
        return eventService.addDiscordTo(eventId, userIds);
    }

    @PostMapping("/discord/remove")
    public PojoEvent removeDiscordTo(@RequestParam(value = "eventId") long eventId,
                                     @RequestParam(value = "userIds", required = false) List<Long> userIds) {
        return eventService.removeDiscordTo(eventId, userIds);
    }

}
