package app.back.service;

import app.back.dto.TodoEntry;
import app.back.entityname.EntityTable;
import app.back.exception.BackNotFoundException;
import app.back.repository.TodoEntryRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DtoTodoEntryService extends DtoAbstractEntityService<TodoEntry, @NonNull TodoEntryRepository> {
    protected DtoTodoEntryService(@NonNull TodoEntryRepository repository) {
        super(repository);
    }

    @Override
    public String getTableName() {
        return EntityTable.TODO_ENTRY;
    }

    @Override
    protected void update(TodoEntry entityToSave, TodoEntry dbEntity) {
        dbEntity.setTodoName(entityToSave.getTodoName());
        dbEntity.setTodoValue(entityToSave.getTodoValue());
        dbEntity.setUserIdSet(entityToSave.getuserIds());
    }

    @Transactional
    public Optional<TodoEntry> setDone(long todoId, boolean done) {
        var todo = findById(todoId).orElseThrow(() -> new BackNotFoundException("Aucun todo trouvé pour l'id " + todoId + "."));
        todo.setDone(done);
        return Optional.of(this.save(todo));
    }

    @Transactional
    public Optional<TodoEntry> setParticipants(long todoId, List<UUID> userIds) {
        var todo = findById(todoId).orElseThrow(() -> new BackNotFoundException("Aucun todo trouvé pour l'id " + todoId + "."));
        todo.setUserIdSet(userIds);
        return Optional.of(this.save(todo));
    }
}
