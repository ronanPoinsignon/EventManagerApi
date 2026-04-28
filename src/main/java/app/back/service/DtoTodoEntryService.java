package app.back.service;

import app.back.dto.TodoEntry;
import app.back.repository.TodoEntryRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class DtoTodoEntryService extends DtoAbstractEntityService<TodoEntry, @NonNull TodoEntryRepository> {
    protected DtoTodoEntryService(@NonNull TodoEntryRepository repository) {
        super(repository);
    }

    @Override
    protected void update(TodoEntry entityToSave, TodoEntry dbEntity) {
        dbEntity.setTodoValue(entityToSave.getTodoValue());
        dbEntity.setDiscordMemberSet(entityToSave.getDiscordMembers());
    }
}
