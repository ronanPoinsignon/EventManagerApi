package app.back.service;

import app.back.dto.Event;
import app.back.exception.BackBadRequestException;
import app.back.repository.EventRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DtoEventService extends DtoAbstractEntityService<Event, @NonNull EventRepository> {

    protected DtoEventService(@NonNull EventRepository repository) {
        super(repository);
    }

    @EntityGraph(attributePaths = {
            "subEvents",
            "participants",
            "todoListEntries"
    })
    @Override
    public Optional<Event> findById(long id) {
        return super.findById(id);
    }

    @Override
    public Event save(Event entity) {
        if(entity.getEventName() == null || entity.getEventName().isBlank()) {
            throw new BackBadRequestException("L'événement doit obligatoirement avoir un nom.");
        }
        if(entity.getParentEvent() == null) {
            var result = this.findByEventName(entity.getEventName());
            if(result.isPresent() && !Objects.equals(result.get().getId(), entity.getId())) {
                throw new BackBadRequestException("L'événement " + entity.getEventName() + " est déjà existant.");
            }
        } else {
            var result = this.findByEventName(entity.getParentEvent().getId(), entity.getEventName());
            if(result.isPresent() && !Objects.equals(result.get().getId(), entity.getId())) {
                throw new BackBadRequestException("Le sous événement " + entity.getEventName() + " est déjà existant.");
            }
        }

        return super.save(entity);
    }

    @Override
    protected void update(Event entityToSave, Event dbEntity) {
        dbEntity.setEventName(entityToSave.getEventName());
        dbEntity.setLocation(entityToSave.getLocation());
        dbEntity.setTricountUrl(entityToSave.getTricountUrl());
        dbEntity.setStartDate(entityToSave.getStartDate());
        dbEntity.setEndDate(entityToSave.getEndDate());
        if(entityToSave.shouldUpdateParentEvent()) {
            dbEntity.setParentEvent(entityToSave.getParentEvent());
        }
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
