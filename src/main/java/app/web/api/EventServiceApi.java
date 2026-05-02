package app.web.api;

import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface EventServiceApi extends AbstractServiceApi<PojoEvent> {

    PojoEvent findByEventName(String name);

    List<PojoEvent> findAllBeforeEnd(LocalDateTime date);

    PojoEvent getLast();

    PojoEvent addSubEvent(long parentEventId, PojoEvent event);

    PojoEvent removeSubEvent(long parentEventId, String subEventName);

    PojoEvent addTo(long eventId, List<Long> discordMemberIds);

    PojoEvent removeTo(long eventId, List<Long> discordMemberIdList);

    PojoEvent addTodo(long eventId, LightPojoTodoEntry lightPojoTodoEntry);

    PojoEvent removeTodo(long eventId, String name);

    PojoEvent addTodoMembers(long eventId, String todoName, List<Long> discordMemberIds);

    PojoEvent removeTodoMembers(long eventId, String todoName, List<Long> discordMemberIds);

    PojoEvent updateTodoStatus(long eventId, String todoName, boolean isDone);

    PojoEvent delete(long eventId);

}
