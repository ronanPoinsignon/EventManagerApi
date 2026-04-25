package app.back.service;

import app.back.dto.TodoEntry;
import app.back.repository.TodoEntryRepository;
import org.jspecify.annotations.NonNull;

public class DtoTodoEntryService extends DtoAbstractEntityService<TodoEntry, @NonNull TodoEntryRepository> {
    protected DtoTodoEntryService(@NonNull TodoEntryRepository repository) {
        super(repository);
    }
}
