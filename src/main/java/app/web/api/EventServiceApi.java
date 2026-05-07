package app.web.api;

import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventServiceApi extends AbstractServiceApi<PojoEvent> {

    PojoEvent findByEventName(String name);

    List<PojoEvent> findAllBeforeEnd(LocalDateTime date);

    PojoEvent getLast();

    PojoEvent addSubEvent(long parentEventId, PojoEvent event);

    PojoEvent removeSubEvent(long parentEventId, String subEventName);

    PojoEvent addTo(long eventId, List<UUID> userIds);

    PojoEvent removeTo(long eventId, List<UUID> userIdList);

    PojoEvent addTodo(long eventId, LightPojoTodoEntry lightPojoTodoEntry);

    PojoEvent removeTodo(long eventId, String name);

    PojoEvent addTodoUsers(long eventId, String todoName, List<UUID> userIds);

    PojoEvent removeTodoUsers(long eventId, String todoName, List<UUID> userIds);

    PojoEvent updateTodoStatus(long eventId, String todoName, boolean isDone);

    PojoEvent delete(long eventId);

}
