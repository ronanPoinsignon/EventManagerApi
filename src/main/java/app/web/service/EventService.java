package app.web.service;

import app.back.dto.DiscordMember;
import app.back.dto.Event;
import app.back.service.DtoDiscordMemberService;
import app.back.service.DtoEventService;
import app.web.api.EventServiceApi;
import app.web.exception.BadRequestException;
import app.web.exception.NotFoundException;
import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoEvent;
import app.web.transform.TransformEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService extends AbstractService<Event, PojoEvent, DtoEventService> implements EventServiceApi {

    private final DtoDiscordMemberService discordMemberService;

    public EventService(DtoEventService eventService, TransformEvent transformEvent, DtoDiscordMemberService discordMemberService) {
        super(eventService, transformEvent);
        this.discordMemberService = discordMemberService;
    }

    @Transactional
    @Override
    public PojoEvent findByEventName(String name) {
        var result =  getService().findByEventName(name)
                .orElseThrow(() -> new NotFoundException("Aucun événement trouvé pour le nom " + name + "."));
        return getTransform().toPojo(result);
    }

    @Override
    @Transactional
    public PojoEvent addSubEvent(long parentEventId, PojoEvent event) {
        if(event == null) {
            throw new BadRequestException("Aucun événement donné.");
        }

        var parentEvent = getService().findById(parentEventId).orElseThrow(() -> new NotFoundException("Aucun parent trouvé."));
        var dtoEvent = getTransform().toDto(event);
        dtoEvent = getService().save(dtoEvent);

        parentEvent.addSubEvent(dtoEvent);
        parentEvent = getService().save(parentEvent);
        return getTransform().toPojo(parentEvent);
    }

    @Override
    @Transactional
    public PojoEvent removeSubEvent(long parentEventId, String subEventName) {
        if(subEventName == null || subEventName.isBlank()) {
            throw new BadRequestException("le nom du sous événement ne peut être null.");
        }

        var event = getService().findById(parentEventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var subEvent = event.getSubEvents().stream().filter(subevent -> subevent.getEventName().equals(subEventName)).findFirst().orElseThrow(() -> new NotFoundException("Aucun sous événement trouvé pour ce nom"));
        event.removeSubEvent(subEvent);
        getService().delete(subEvent.getId());

        return getTransform().toPojo(getService().save(event));
    }

    @Transactional
    @Override
    public List<PojoEvent> findAllBeforeEnd(LocalDateTime date) {
        return getService().findAllBeforeEnd(date).stream().map(getTransform()::toPojo).toList();
    }

    @Transactional
    @Override
    public PojoEvent getLast() {
        var result = getService().getLast().orElseThrow(() -> new NotFoundException("Aucun événement de renseigné."));
        return getTransform().toPojo(result);
    }

    @Transactional
    @Override
    public PojoEvent addTo(long parentEventId, List<Long> discordMemberIdList) {
        var event = getService().findById(parentEventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var discordMembers = findMembers(discordMemberIdList);

        var hasChanged = event.addParticipants(discordMembers);
        if(!hasChanged) {
            return getTransform().toPojo(event);
        }

        return getTransform().toPojo(getService().save(event));
    }

    @Transactional
    @Override
    public PojoEvent removeTo(long parentEventId, List<Long> discordMemberIdList) {
        var event = getService().findById(parentEventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));

        var hasChanged = event.removeParticipants(discordMemberIdList);
        if(!hasChanged) {
            return getTransform().toPojo(event);
        }

        return getTransform().toPojo(getService().save(event));
    }

    @Transactional
    @Override
    public PojoEvent addTodo(long eventId, LightPojoTodoEntry lightPojoTodoEntry) {
        if(lightPojoTodoEntry == null) {
            throw new BadRequestException("Document d'information manquant.");
        }
        
        var event = getService().findById(eventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var discordMemberList = findMembers(lightPojoTodoEntry.getParticipants());
        event.addTodo(lightPojoTodoEntry.getName(), lightPojoTodoEntry.getTodo(), discordMemberList);

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
