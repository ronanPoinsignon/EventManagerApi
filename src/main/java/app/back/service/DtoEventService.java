package app.back.service;

import app.back.api.DtoEventServiceApi;
import app.back.dto.Event;
import app.back.entityname.EntityTable;
import app.back.exception.BackBadRequestException;
import app.back.exception.duplicate.event.BackDuplicateEventNameException;
import app.back.repository.EventRepository;
import app.back.security.UserServiceApi;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DtoEventService extends DtoAbstractEntityService<Event, @NonNull EventRepository> implements DtoEventServiceApi {

    private final UserServiceApi userService;

    protected DtoEventService(@NonNull EventRepository repository, UserServiceApi userService) {
        super(repository);
        this.userService = userService;
    }

    @Override
    public String getTableName() {
        return EntityTable.EVENT;
    }

    @EntityGraph(attributePaths = {
            "subEvents",
            "participants",
            "todoListEntries"
    })
    @Override
    public Optional<Event> findById(Long id) {
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
                throw new BackDuplicateEventNameException("Un événement de nom " + entity.getEventName() + " est déjà existant.");
            }
        }

        if(entity.getId() == null) {
            var user = userService.getUser();
            entity.setOwnerUserId(user.getUserId());
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

    @Override
    public Optional<Event> findByEventName(String name) {
        return repository.findByEventName(name);
    }

    @Override
    public Optional<Event> findByEventName(long parentId, String name) {
        return repository.findByEventName(parentId, name);
    }

    @Override
    public Optional<Event> findByEventName(String parentName, String name) {
        return repository.findByEventName(parentName, name);
    }

    @Override
    public List<Event> findAllBeforeEnd(Instant date) {
        if(date == null) {
            date =  LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59)).toInstant(ZoneOffset.UTC);
        }
        return repository.findAllBeforeEnd(date);
    }

    @Override
    public List<Event> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Event> getLast() {
        return repository.getLast();
    }

    @Override
    public Optional<Event> findEventFromTodoId(long todoId) {
        return repository.findEventFromTodoId(todoId);
    }

    @Override
    public Optional<Event> findAndDelete(Long id) {
        if(id == null) {
            return Optional.empty();
        }

        var resultOptional = repository.findById(id);
        if(resultOptional.isEmpty()) {
            return Optional.empty();
        }

        var event = resultOptional.get();
        var parentEvent = event.getParentEvent();
        if(parentEvent == null) {
            this.delete(event);
            return Optional.of(event);
        }

        parentEvent.removeSubEvent(event);
        this.save(parentEvent);
        return Optional.of(event);
    }
}
