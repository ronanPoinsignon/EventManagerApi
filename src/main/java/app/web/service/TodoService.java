package app.web.service;

import app.back.dto.TodoEntry;
import app.back.exception.BackNotFoundException;
import app.back.service.DtoTodoEntryService;
import app.web.api.TodoServiceApi;
import app.web.pojo.PojoTodoEntry;
import app.web.transform.Transform;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TodoService extends AbstractService<TodoEntry, PojoTodoEntry, DtoTodoEntryService> implements TodoServiceApi {

    public TodoService(DtoTodoEntryService service, Transform<TodoEntry, PojoTodoEntry> transform) {
        super(service, transform);
    }

    @Override
    public PojoTodoEntry deleteTodo(long todoId) {
        return getService().findAndDelete(todoId)
                .map(todo -> getTransform().toPojo(todo))
                .orElse(null);
    }

    @Override
    public PojoTodoEntry setDone(long todoId, boolean done) {
        var todo = getService().setDone(todoId, done)
                .orElseThrow(() -> new BackNotFoundException("Aucun todo trouvé pour l'id " + todoId + "."));
        return getTransform().toPojo(todo);
    }

    @Override
    public PojoTodoEntry setParticipants(long todoId, List<UUID> userIds) {
        var todo = getService().setParticipants(todoId, userIds)
                .orElseThrow(() -> new BackNotFoundException("Aucun todo trouvé pour l'id " + todoId + "."));
        return getTransform().toPojo(todo);
    }

}
