package app.web.transform;

import app.back.dto.Event;
import jakarta.annotation.Nonnull;
import app.web.pojo.PojoEvent;
import org.springframework.stereotype.Service;

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
        var event = super.from(dto);

        event.setEventName(dto.getEventName());
        event.setCreationDate(dto.getCreationDate());
        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());
        event.setLocation(dto.getLocation());
        if((dto.getSubEvents() != null)) {
            event.setSubEvents(dto.getSubEvents().stream().map(this::toPojo).toList());
        }
        // ne pas gérer ici la gestion des parents à cause de la dépendance cyclique
        if(dto.getParticipants() != null) {
            event.setParticipants(dto.getParticipants().stream().map(transformMember::toPojo).toList());
        }
        if(dto.getTodoListMap() != null) {
            event.setTodoList(dto.getTodoListMap().entrySet()
                    .stream().collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> transformMember.toPojo(entry.getValue())
                    )));
        }
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
