package app.back.service;

import app.back.dto.Event;
import app.back.repository.EventRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DtoEventService extends DtoAbstractEntityService<Event, @NonNull EventRepository> {

    protected DtoEventService(@NonNull EventRepository repository) {
        super(repository);
    }

    @Override
    protected void update(Event entityToSave, Event dbEntity) {
        dbEntity.setEventName(entityToSave.getEventName());
        dbEntity.setLocation(entityToSave.getLocation());
        dbEntity.setTricountUrl(entityToSave.getTricountUrl());
        dbEntity.setStartDate(entityToSave.getStartDate());
        dbEntity.setEndDate(entityToSave.getEndDate());
        dbEntity.setParentEvent(entityToSave.getParentEvent());
        if(entityToSave.shouldUpdateSubEvents()) {
            dbEntity.setSubEvents(entityToSave.getSubEvents());
        }
        if(entityToSave.shouldUpdateParticipants()) {
            dbEntity.setParticipants(entityToSave.getParticipants());
        }
        if(entityToSave.isShouldUpdateTodos()) {
            dbEntity.setTodoList(entityToSave.getTodoList());
        }
    }

    public Optional<Event> findByEventName(String name) {
        return repository.findByEventName(name);
    }

    public List<Event> findAllBeforeEnd(LocalDateTime date) {
        if(date == null) {
            date = LocalDateTime.now();
        }
        return repository.findAllBeforeEnd(date);
    }

    public Optional<Event> getLast() {
        return repository.getLast();
    }
}
