package app.web.api;

import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface EventServiceApi extends AbstractServiceApi<PojoEvent> {

    PojoEvent findByEventName(String name);

    PojoEvent addSubEvent(long parentEventId, PojoEvent event);

    List<PojoEvent> findAllBeforeEnd(LocalDateTime date);

    PojoEvent getLast();

    PojoEvent addTo(long parentEventId, List<Long> discordMemberIds);

    PojoEvent removeTo(long parentEventId, List<Long> discordMemberIdList);

    PojoEvent addTodo(long eventId, LightPojoTodoEntry lightPojoTodoEntry);

    PojoEvent removeSubEvent(long parentEventId, String subEventName);

    PojoEvent removeTodo(long eventId, String name);

    PojoEvent addTodoMembers(long eventId, String todoName, List<Long> discordMemberIds);

    PojoEvent removeTodoMembers(long eventId, String todoName, List<Long> discordMemberIds);
}
