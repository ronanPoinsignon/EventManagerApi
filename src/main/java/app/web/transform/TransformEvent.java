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
        var event = super.from(pojo);

        event.setEventName(pojo.getEventName());
        event.setStartDate(pojo.getStartDate());
        event.setEndDate(pojo.getEndDate());
        event.setLocation(pojo.getLocation());
        event.setParentEvent(this.toDto(pojo.getParentEvent()));
        if(pojo.getSubEvents() != null) {
            event.setSubEvents(pojo.getSubEvents().stream().map(this::toDto).toList());
        }
        // ne pas gérer ici la gestion des parents à cause de la dépendance cyclique
        if(pojo.getParticipants() != null) {
            event.setParticipants(pojo.getParticipants().stream().map(transformMember::toDto).toList());
        }
        if(pojo.getTodoList() != null) {
            event.setTodoListFromMap(pojo.getTodoList().entrySet()
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
        Map<Long, PojoEvent> pojoEventMap = new HashMap<>();
        return from(dto, pojoEventMap);
    }

    private PojoEvent from(Event dto, Map<Long, PojoEvent> pojoEventMap) {
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
        event.setTodoList(dto.getTodoListMap().entrySet()
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
