package app.web.service;

import app.back.api.DtoEventServiceApi;
import app.back.api.DtoUserAttributesServiceApi;
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
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class EventService extends AbstractService<Event, PojoEvent, DtoEventServiceApi> implements EventServiceApi {


    public EventService(DtoEventServiceApi eventService, TransformEvent transformEvent, DtoUserAttributesServiceApi discordMemberService) {
        super(eventService, transformEvent);
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
    public PojoEvent addTo(long eventId, List<UUID> userIds) {
        return manageParticipants(eventId, userIds, Event::addParticipants);
    }

    @Transactional
    @Override
    public PojoEvent removeTo(long eventId, List<UUID> userIdList) {
        return manageParticipants(eventId, userIdList, Event::removeParticipants);
    }

    private <T> PojoEvent manageParticipants(long eventId,List<T> participantList, BiFunction<Event, List<T>, Boolean> participantFunction) {
        var event = getService().findById(eventId).orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));

        var hasChanged = participantFunction.apply(event, participantList);
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
        event.addTodo(lightPojoTodoEntry.getName(), lightPojoTodoEntry.getTodo(), lightPojoTodoEntry.getParticipants());

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
    public PojoEvent addTodoUsers(long eventId, String todoName, List<UUID> userIds) {
        return manageTodoMember(eventId, todoName, userIds, TodoEntry::addUserIds);
    }

    @Transactional
    @Override
    public PojoEvent removeTodoUsers(long eventId, String todoName, List<UUID> userIds) {
        return manageTodoMember(eventId, todoName, userIds, TodoEntry::removeUserIds);
    }

    @Transactional
    private <T> PojoEvent manageTodoMember(long eventId, String todoName, T userList, BiFunction<TodoEntry, T, Boolean> memberFunction) {
        return updateTodoInfo(eventId, todoName, todo -> memberFunction.apply(todo, userList));
    }

    @Transactional
    @Override
    public PojoEvent delete(long eventId) {
        return getService().findAndDelete(eventId)
                .map(getTransform()::toPojo)
                .orElse(null);
    }

    @Transactional
    @Override
    public PojoEvent updateTodoStatus(long eventId, String todoName, boolean isDone) {
        return updateTodoInfo(eventId, todoName, todo -> todo.setDone(isDone));
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
