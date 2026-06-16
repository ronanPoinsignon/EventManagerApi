package app.back.service;

import app.back.api.DtoEventServiceApi;
import app.back.dto.Event;
import app.back.dto.notification.EntityType;
import app.back.dto.notification.ScheduleNotification;
import app.back.entityname.EntityTable;
import app.back.exception.BackBadRequestException;
import app.back.exception.duplicate.event.BackDuplicateEventNameException;
import app.back.repository.EventRepository;
import app.back.security.UserServiceApi;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class DtoEventService extends DtoAbstractEntityService<Event, @NonNull EventRepository> implements DtoEventServiceApi {

    Logger logger = LoggerFactory.getLogger(DtoEventService.class);

    private final UserServiceApi userService;
    private final DtoScheduleNotificationService dtoScheduleNotificationService;

    protected DtoEventService(@NonNull EventRepository repository, UserServiceApi userService, DtoScheduleNotificationService dtoScheduleNotificationService) {
        super(repository);
        this.userService = userService;
        this.dtoScheduleNotificationService = dtoScheduleNotificationService;
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
        if(entity.getEndDate() != null && entity.getStartDate().isAfter(entity.getEndDate())) {
            throw new BackBadRequestException("La date de fin d'événement est antérieure à sa date de début.");
        }

        if(entity.getId() == null) {
            var user = userService.getUser();
            entity.setOwnerUserId(user.getUserId());
        }

        var result = super.save(entity);
        if(result.getParentEvent() == null) {
            createNotifications(result);
        }
        return result;
    }

    private void createNotifications(Event event) {
        if(event == null) {
            return;
        }
        if(event.getId() == null) {
            logger.warn("Impossible de supprimer les notifications pour un événement d'id null.");
            return;
        }

        // suppression des possibles précédentes notifications
        var eventNotificationNumber = dtoScheduleNotificationService.deleteNotificationByRelatedId(EntityType.EVENT_TYPE, event.getId());
        logger.info("{} notification(s) sont supprimées après la modification de l'événement {}", eventNotificationNumber, event.getId());

        var now = Instant.now();
        var startDate = event.getStartDate();
        var duration = Duration.between(now, startDate);
        var seconds = duration.get(ChronoUnit.SECONDS);
        var secondsByDay = 86400;

        if(seconds > 60 * secondsByDay) {
            dtoScheduleNotificationService.save(new ScheduleNotification(startDate.minus(30, ChronoUnit.DAYS), event, event.getParticipants()));
        }
        if(seconds > 20 * secondsByDay) {
            dtoScheduleNotificationService.save(new ScheduleNotification(startDate.minus(7, ChronoUnit.DAYS), event, event.getParticipants()));
        }
        if(seconds > 6 * secondsByDay) {
            dtoScheduleNotificationService.save(new ScheduleNotification(startDate.minus(1, ChronoUnit.DAYS), event, event.getParticipants()));
        }
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
        if(entityToSave.shouldUpdateGuildIds()) {
            dbEntity.setGuildIds(entityToSave.getGuildIds());
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
    public List<Event> getEventsByUserId(UUID userId) {
        return repository.getEventsByUserId(userId.toString());
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
        this.delete(event);
        dtoScheduleNotificationService.deleteNotificationByRelatedId(EntityType.EVENT_TYPE, id);
        return Optional.of(event);
    }
}
