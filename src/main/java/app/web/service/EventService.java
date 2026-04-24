package app.web.service;

import app.back.dto.DiscordMember;
import app.back.dto.Event;
import app.back.repository.EventRepository;
import app.back.service.DtoDiscordMemberService;
import app.back.service.DtoEventService;
import app.web.api.EventServiceApi;
import app.web.exception.NotFoundException;
import app.web.pojo.PojoEvent;
import app.web.transform.TransformEvent;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService extends AbstractService<Event, PojoEvent, @NonNull EventRepository, DtoEventService> implements EventServiceApi {

    private final DtoDiscordMemberService discordMemberService;

    public EventService(DtoEventService eventService, TransformEvent transformEvent, DtoDiscordMemberService discordMemberService) {
        super(eventService, transformEvent);
        this.discordMemberService = discordMemberService;
    }

    @Override
    public PojoEvent findByEventName(String name) {
        var result =  getService().findByEventName(name)
                .orElseThrow(() -> new NotFoundException("Aucun événement trouvé pour le nom " + name + "."));
        return getTransform().toPojo(result);
    }

    @Override
    public PojoEvent addSubEvent(int parentEventId, PojoEvent event) {
        if(event == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucun événement donné.");
        }

        var parentEvent = getService().findById(parentEventId).orElseThrow(() -> new NotFoundException("Aucun parent trouvé."));
        var dtoEvent = getTransform().toDto(event);
        dtoEvent.setParentEvent(parentEvent);
        dtoEvent = getService().save(dtoEvent);
        parentEvent.getSubEvents().add(dtoEvent);
        return getTransform().toPojo(getService().save(parentEvent));
    }

    @Override
    public List<PojoEvent> findAllBeforeEnd(LocalDateTime date) {
        return getService().findAllBeforeEnd(date).stream().map(getTransform()::toPojo).toList();
    }

    @Override
    public PojoEvent getLast() {
        return getTransform().toPojo(getService().getLast());
    }

    @Override
    public PojoEvent addTo(Long eventId, List<String> discordMemberIds) {
        if(eventId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'id de l'événement ne peut être null.");
        }

        var event = getService().findById(eventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var discordMembers = discordMemberIds.stream()
                .map(Long::parseLong)
                .map(discordMemberService::findByDiscordId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        if(discordMembers.size() != discordMemberIds.size()) {
            var newDiscordMemberIds = new ArrayList<>(discordMemberIds);
            newDiscordMemberIds.removeAll(discordMembers.stream().map(DiscordMember::getDiscordId).map(Object::toString).toList());
            throw new NotFoundException("les id de membres suivants n'ont pas été trouvé : " + newDiscordMemberIds + ".");
        }

        var hasChanged = event.getParticipants().addAll(discordMembers);
        if(!hasChanged) {
            return getTransform().toPojo(event);
        }

        return getTransform().toPojo(getService().save(event));
    }

}
