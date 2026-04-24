package app.back.repository;

import app.back.dto.Event;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends AbstractEntityRepository<Event> {

    Optional<Event> findByEventName(String eventName);

    @NativeQuery("select * from events where (end_date is null AND start_date < ?1) OR start_date < ?1")
    List<Event> findAllBeforeEnd(LocalDateTime date);

    @NativeQuery("select * from events order by start_date desc limit 1")
    Event getLast();

}
