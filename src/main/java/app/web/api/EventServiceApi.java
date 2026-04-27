package app.web.api;

import app.web.pojo.PojoEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface EventServiceApi extends AbstractServiceApi<PojoEvent> {

    PojoEvent findByEventName(String name);

    PojoEvent addSubEvent(long parentEventId, PojoEvent event);

    List<PojoEvent> findAllBeforeEnd(LocalDateTime date);

    PojoEvent getLast();

    PojoEvent addTo(long parentEventId, List<Long> discordMemberIds);

    PojoEvent addTodo(long eventId, String todo, List<Long> discordMemberIdList);

    PojoEvent removeSubEvent(long parentEventId, String subEventName);
}
