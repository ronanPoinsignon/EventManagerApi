package app.back.repository;

import app.back.dto.Event;
import app.back.entityname.Contrainte;
import app.back.entityname.EntityTable;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends AbstractEntityRepository<Event> {

    @NativeQuery("select * from " + EntityTable.EVENT + " where event_name = ?1 and parent_event_id is null")
    Optional<Event> findByEventName(String eventName);

    @NativeQuery("select * from " + EntityTable.EVENT + " where event_name = ?2 and parent_event_id = ?1")
    Optional<Event> findByEventName(long parentId, String eventName);

    @NativeQuery("select child.* from " + EntityTable.EVENT + " child join " + EntityTable.EVENT + " parent on child.parent_event_id = parent.id where parent." + Contrainte.EVENT_NAME + " = ?1 and child." + Contrainte.EVENT_NAME + " = ?2")
    Optional<Event> findByEventName(String parentId, String eventName);

    @NativeQuery("select * from " + EntityTable.EVENT + " where ((end_date is null AND start_date >= ?1) OR end_date >= ?1) and parent_event_id is null")
    List<Event> findAllBeforeEnd(LocalDateTime date);

    @NativeQuery("select * from " + EntityTable.EVENT + " where parent_event_id is null order by creation_date desc limit 1")
    Optional<Event> getLast();

}
