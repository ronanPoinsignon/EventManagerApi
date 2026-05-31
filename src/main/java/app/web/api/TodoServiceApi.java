package app.web.api;

import app.web.pojo.PojoTodoEntry;

import java.util.List;
import java.util.UUID;

public interface TodoServiceApi extends AbstractServiceApi<PojoTodoEntry> {

    PojoTodoEntry deleteTodo(long todoId);

    PojoTodoEntry setDone(long todoId, boolean done);

    PojoTodoEntry setParticipants(long todoId, List<UUID> userIds);

}
