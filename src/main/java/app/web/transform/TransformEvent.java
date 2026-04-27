package app.web.transform;

import app.back.dto.Event;
import app.web.pojo.PojoEvent;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransformEvent extends AbstractTransform<Event, PojoEvent> {

    private final TransformMember transformMember;

    public TransformEvent(TransformMember transformMember) {
        this.transformMember = transformMember;
    }

    @Override
    protected Event from(@Nonnull PojoEvent pojo) {
        return from(pojo, new HashMap<>());
    }

    private Event from(PojoEvent pojo, Map<Long, Event> eventMap) {
        if(pojo == null) {
            return null;
        }

        var event = super.from(pojo);
        eventMap.put(event.getId(), event);

        event.setEventName(pojo.getEventName());
        event.setStartDate(pojo.getStartDate());
        event.setEndDate(pojo.getEndDate());
        event.setLocation(pojo.getLocation());
        if(pojo.getParentEvent() != null) {
            if(eventMap.containsKey(pojo.getParentEvent().getId())) {
                event.setParentEvent(eventMap.get(pojo.getParentEvent().getId()));
            } else {
                var parentEvent = from(pojo.getParentEvent(), eventMap);
                event.setParentEvent(parentEvent);
            }
        }
        if(pojo.getSubEvents() != null) {
            event.setSubEvents(pojo.getSubEvents().stream().map(subEvent -> {
                if(eventMap.containsKey(subEvent.getId())) {
                    return eventMap.get(subEvent.getId());
                } else {
                    return from(subEvent, eventMap);
                }
            }).toList());
        }
        if(pojo.getParticipants() != null) {
            event.setParticipants(pojo.getParticipants().stream().map(transformMember::toDto).toList());
        }
        if(pojo.getTodoListMap() != null) {
            event.setTodoListFromMap(pojo.getTodoListMap().entrySet()
                    .stream().collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> transformMember.toDto(entry.getValue())
                    )));
        }
        event.setTricountUrl(pojo.getTricountUrl());

        return event;
    }

    @Override
    protected PojoEvent from(@Nonnull Event dto) {
        return from(dto, new HashMap<>());
    }

    private PojoEvent from(Event dto, Map<Long, PojoEvent> pojoEventMap) {
        if(dto == null) {
            return null;
        }

        var event = super.from(dto);
        pojoEventMap.put(event.getId(), event);

        event.setEventName(dto.getEventName());
        event.setCreationDate(dto.getCreationDate());
        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());
        event.setLocation(dto.getLocation());
        if(dto.getParentEvent() != null) {
            if(pojoEventMap.containsKey(dto.getParentEvent().getId())) {
                event.setParentEvent(pojoEventMap.get(dto.getParentEvent().getId()));
            } else {
                var parentEvent = from(dto.getParentEvent(), pojoEventMap);
                event.setParentEvent(parentEvent);
            }
        }
        event.setSubEvents(dto.getSubEvents().stream().map(subEvent -> {
            if(pojoEventMap.containsKey(subEvent.getId())) {
                return pojoEventMap.get(subEvent.getId());
            } else {
                return from(subEvent, pojoEventMap);
            }
        }).toList());
        event.setParticipants(dto.getParticipants().stream().map(transformMember::toPojo).toList());
        event.setTodoListMap(dto.getTodoListMap().entrySet()
                .stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> transformMember.toPojo(entry.getValue())
                )));
        event.setTricountUrl(dto.getTricountUrl());

        return event;
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
