package app.back.api;

import app.back.dto.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DtoEventServiceApi extends AbstractDtoServiceApi<Event> {

    Optional<Event> findByEventName(String name);

    Optional<Event> findByEventName(long parentId, String name);

    List<Event> findAllBeforeEnd(LocalDateTime date);

    Optional<Event> getLast();

}
