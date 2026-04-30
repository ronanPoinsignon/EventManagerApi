package app.web.service;

import app.back.api.DtoDiscordMemberServiceApi;
import app.back.api.DtoEventServiceApi;
import app.back.dto.DiscordMember;
import app.back.dto.Event;
import app.back.dto.TodoEntry;
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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class EventService extends AbstractService<Event, PojoEvent, DtoEventServiceApi> implements EventServiceApi {

    private final DtoDiscordMemberServiceApi discordMemberService;

    public EventService(DtoEventServiceApi eventService, TransformEvent transformEvent, DtoDiscordMemberServiceApi discordMemberService) {
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
    public PojoEvent addTo(long eventId, List<Long> discordMemberIdList) {
        return manageParticipants(eventId, () -> findAllMembers(discordMemberIdList), Event::addParticipants);
    }

    @Transactional
    @Override
    public PojoEvent removeTo(long eventId, List<Long> discordMemberIdList) {
        return manageParticipants(eventId, () -> discordMemberIdList, Event::removeParticipants);
    }

    private <T> PojoEvent manageParticipants(long eventId, Supplier<List<T>> participantList, BiFunction<Event, List<T>, Boolean> participantFunction) {
        var event = getService().findById(eventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));

        var hasChanged = participantFunction.apply(event, participantList.get());
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
        var discordMemberList = findAllMembers(lightPojoTodoEntry.getParticipants());
        event.addTodo(lightPojoTodoEntry.getName(), lightPojoTodoEntry.getTodo(), discordMemberList);

        return getTransform().toPojo(getService().save(event));
    }

    @Transactional
    @Override
    public PojoEvent removeTodo(long eventId, String name) {
        if(name == null || name.isBlank()) {
            throw new BadRequestException("Le nom est obligatoire.");
        }

        var event = getService().findById(eventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var result = event.removeTodo(name);
        if(!result) {
            return getTransform().toPojo(event);
        }

        return getTransform().toPojo(getService().save(event));
    }

    @Transactional
    @Override
    public PojoEvent addTodoMembers(long eventId, String todoName, List<Long> discordMemberIds) {
        return manageTodoMember(eventId, todoName, () -> findAllMembers(discordMemberIds), TodoEntry::addDiscordMembers);
    }

    @Transactional
    @Override
    public PojoEvent removeTodoMembers(long eventId, String todoName, List<Long> discordMemberIds) {
        return manageTodoMember(eventId, todoName, () -> discordMemberIds, TodoEntry::removeDiscordMember);
    }

    private <T> PojoEvent manageTodoMember(long eventId, String todoName, Supplier<T> discordMemberList, BiFunction<TodoEntry, T, Boolean> memberFunction) {
        return updateTodoInfo(eventId, todoName, todo -> memberFunction.apply(todo, discordMemberList.get()));
    }

    @Override
    public PojoEvent updateTodoStatus(long eventId, String todoName, boolean isDone) {
        return updateTodoInfo(eventId, todoName, todo -> todo.setDone(isDone));
    }

    /**
     *
     * @param discordMemberIdList
     * @throws NotFoundException Si certains utilisateurs n'ont pas été trouvés, une {@link NotFoundException} est levée indiquant quels utilisateurs n'ont pas été récupérés.
     * @return
     */
    private List<DiscordMember> findAllMembers(List<Long> discordMemberIdList) throws NotFoundException {
        if(discordMemberIdList == null) {
            discordMemberIdList = new ArrayList<>();
        }
        var result = this.findMembers(discordMemberIdList);

        if(result.size() != discordMemberIdList.size()) {
            var newDiscordMemberIds = new ArrayList<>(discordMemberIdList);
            newDiscordMemberIds.removeAll(result.stream().map(DiscordMember::getId).toList());
            throw new NotFoundException("les ids de membres suivants n'ont pas été trouvé : " + newDiscordMemberIds + ".");
        }

        return result;
    }

    private List<DiscordMember> findMembers(List<Long> discordMemberIdList) {
        if(discordMemberIdList == null || discordMemberIdList.isEmpty()) {
            return new ArrayList<>();
        }

        return discordMemberService.findByDiscordId(discordMemberIdList);
    }

    private PojoEvent updateTodoInfo(long eventId, String todoName, Function<TodoEntry, Boolean> todoFunction) {
        if(todoName == null || todoName.isBlank()) {
            throw new BadRequestException("Le nom est obligatoire.");
        }

        var event = getService().findById(eventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var todo = event.findTodoEntryByName(todoName);
        if(todo == null) {
            throw new NotFoundException("Aucun todo enregistré avec ce nom pour l'événement " + event.getEventName() + ".");
        }

        var result = todoFunction.apply(todo);

        if(!result) {
            return getTransform().toPojo(event);
        }

        return getTransform().toPojo(getService().save(event));
    }

}
