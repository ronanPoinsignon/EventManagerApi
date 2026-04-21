package app.back.repository;

import app.back.dto.Event;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends AbstractEntityRepository<Event> {

    Optional<Event> findByEventName(String eventName);

}
