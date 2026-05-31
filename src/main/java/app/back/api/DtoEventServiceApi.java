package app.back.api;

import app.back.dto.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DtoEventServiceApi extends AbstractDtoServiceApi<Event> {

    Optional<Event> findByEventName(String name);

    Optional<Event> findByEventName(long parentId, String name);

    Optional<Event> findByEventName(String parentName, String name);

    List<Event> findAllBeforeEnd(LocalDateTime date);

    List<Event> findAll();

    Optional<Event> getLast();

    Optional<Event> findEventFromTodoId(long todoId);
}
