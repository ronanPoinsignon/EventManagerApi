package app.web.transform;

import app.back.dto.Event;
import app.web.pojo.PojoEvent;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TransformEvent extends AbstractTransform<Event, PojoEvent> {

    @Autowired
    @Lazy
    private TransformMember transformMember;

    @Autowired
    @Lazy
    private TransformTodoEntry transformTodoEntry;

    @Override
    protected Event from(@Nonnull PojoEvent pojo) {
        return from(pojo, new HashMap<>());
    }

    private Event from(PojoEvent pojo, Map<PojoEvent, Event> eventMap) {
        if(pojo == null) {
            return null;
        }

        var event = super.from(pojo);
        eventMap.put(pojo, event);

        event.setEventName(pojo.getEventName());
        event.setStartDate(pojo.getStartDate());
        event.setEndDate(pojo.getEndDate());
        event.setLocation(pojo.getLocation());
        if(pojo.getSubEvents() != null) {
            event.setSubEvents(pojo.getSubEvents().stream().map(subEvent -> {
                if(eventMap.containsKey(subEvent)) {
                    return eventMap.get(subEvent);
                } else {
                    return from(subEvent, eventMap);
                }
            }).toList());
        }
        if(pojo.getParticipants() != null) {
            event.setParticipants(pojo.getParticipants());
        }
        if(pojo.getTodoList() != null) {
            pojo.getTodoList().stream().map(pojoTodo -> {
                var pojoTodoEvent = pojoTodo.getEvent();
                pojoTodo.setEvent(null);
                var todo = transformTodoEntry.toDto(pojoTodo);
                pojoTodo.setEvent(pojoTodoEvent);
                todo.setEvent(event);

                return todo;
            }).forEach(todo -> event.addTodo(todo.getTodoName(), todo.getTodoValue(), todo.getuserIds()));
        }
        event.setTricountUrl(pojo.getTricountUrl());

        return event;
    }

    @Override
    protected PojoEvent from(@Nonnull Event dto) {
        return from(dto, new HashMap<>());
    }

    private PojoEvent from(Event dto, Map<Event, PojoEvent> pojoEventMap) {
        if(dto == null) {
            return null;
        }

        var pojoEvent = super.from(dto);
        pojoEventMap.put(dto, pojoEvent);

        pojoEvent.setEventName(dto.getEventName());
        pojoEvent.setCreationDate(dto.getCreationDate());
        pojoEvent.setStartDate(dto.getStartDate());
        pojoEvent.setEndDate(dto.getEndDate());
        pojoEvent.setLocation(dto.getLocation());
        if(dto.getParentEvent() != null) {
            if(pojoEventMap.containsKey(dto.getParentEvent())) {
                pojoEvent.setParentEvent(pojoEventMap.get(dto.getParentEvent()));
            } else {
                var parentEvent = from(dto.getParentEvent(), pojoEventMap);
                pojoEvent.setParentEvent(parentEvent);
            }
        }
        pojoEvent.setSubEvents(dto.getSubEvents().stream().map(subEvent -> {
            if(pojoEventMap.containsKey(subEvent)) {
                return pojoEventMap.get(subEvent);
            } else {
                return from(subEvent, pojoEventMap);
            }
        }).toList());
        pojoEvent.setParticipants(dto.getParticipants());
        pojoEvent.setTodoList(dto.getTodoList().stream().map(todo -> {
            var todoEvent = todo.getEvent();
            todo.setEvent(null);
            var pojo = transformTodoEntry.toPojo(todo);
            todo.setEvent(todoEvent);
            pojo.setEvent(pojoEvent);

            return pojo;
        }).toList());
        pojoEvent.setTricountUrl(dto.getTricountUrl());

        return pojoEvent;
    }

    @Override
    protected Event createDto() {
        return new Event();
    }

    @Override
    protected PojoEvent createPojo() {
        return new PojoEvent();
    }

}
