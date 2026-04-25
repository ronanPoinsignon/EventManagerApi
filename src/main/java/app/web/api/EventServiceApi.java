package app.web.api;

import app.web.pojo.PojoEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface EventServiceApi extends AbstractServiceApi<PojoEvent> {

    PojoEvent findByEventName(String name);

    PojoEvent addSubEvent(int parentEventId, PojoEvent event);

    List<PojoEvent> findAllBeforeEnd(LocalDateTime date);

    PojoEvent getLast();

    PojoEvent addTo(Long eventId, List<Long> discordMemberIds);

    PojoEvent addTodo(Long eventId, String todo, List<Long> discordMemberIdList);
}
