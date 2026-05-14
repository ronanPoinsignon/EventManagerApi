package app.back.repository;

import app.back.dto.Event;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends AbstractEntityRepository<Event> {

    @NativeQuery("select * from events where event_name = ?1 and parent_event_id is null")
    Optional<Event> findByEventName(String eventName);

    @NativeQuery("select * from events where event_name = ?2 and parent_event_id = ?1")
    Optional<Event> findByEventName(long parentId, String eventName);

    @NativeQuery("select * from events where ((end_date is null AND start_date >= ?1) OR end_date >= ?1) and parent_event_id is null")
    List<Event> findAllBeforeEnd(LocalDateTime date);

    @NativeQuery("select * from events where parent_event_id is null order by creation_date desc limit 1")
    Optional<Event> getLast();

}
