package app.back.service;

import app.back.dto.Event;
import app.back.repository.EventRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DtoEventService extends DtoAbstractEntityService<Event, @NonNull EventRepository> {

    protected DtoEventService(@NonNull EventRepository repository) {
        super(repository);
    }

    public Optional<Event> findByEventName(String name) {
        return repository.findByEventName(name);
    }
}
