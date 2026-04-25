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
import java.util.HashSet;
import java.util.List;

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
    public PojoEvent addTo(Long eventId, List<Long> discordMemberIdList) {
        if(eventId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'id de l'événement ne peut être null.");
        }

        var event = getService().findById(eventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var discordMembers = findMembers(discordMemberIdList);

        var hasChanged = event.getParticipants().addAll(discordMembers);
        if(!hasChanged) {
            return getTransform().toPojo(event);
        }

        return getTransform().toPojo(getService().save(event));
    }

    @Override
    public PojoEvent addTodo(Long eventId, String todo, List<Long> discordMemberIdList) {
        if(eventId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'id de l'événement ne peut être null.");
        }

        var event = getService().findById(eventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var discordMemberList = findMembers(discordMemberIdList);
        event.getTodoListMap().getOrDefault(todo, new HashSet<>()).addAll(discordMemberList);

        return getTransform().toPojo(getService().save(event));
    }

    /**
     *
     * @param discordMemberIdList
     * @throws NotFoundException Si certains utilisateurs n'ont pas été trouvés, une {@link NotFoundException} est levée indiquant quels utilisateurs n'ont pas été récupérés.
     * @return
     */
    private List<DiscordMember> findMembers(List<Long> discordMemberIdList) {
        if(discordMemberIdList == null || discordMemberIdList.isEmpty()) {
            return new ArrayList<>();
        }

        var result = discordMemberService.findByDiscordId(discordMemberIdList);
        if(result.size() != discordMemberIdList.size()) {
            var newDiscordMemberIds = new ArrayList<>(discordMemberIdList);
            newDiscordMemberIds.removeAll(result.stream().map(DiscordMember::getId).toList());
            throw new NotFoundException("les ids de membres suivants n'ont pas été trouvé : " + newDiscordMemberIds + ".");
        }

        return result;
    }

}
