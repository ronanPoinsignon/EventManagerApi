package app.back.service;

import app.back.dto.Event;
import app.back.repository.EventRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DtoEventService extends DtoAbstractEntityService<Event, @NonNull EventRepository> {

    protected DtoEventService(@NonNull EventRepository repository) {
        super(repository);
    }

    @Override
    public Event save(Event entity) {
        return super.save(entity);
    }

    public Optional<Event> findByEventName(String name) {
        return repository.findByEventName(name);
    }

    public List<Event> findAllBeforeEnd(LocalDateTime date) {
        if(date == null) {
            date = LocalDateTime.now();
        }
        return repository.findAllBeforeEnd(date);
    }

    public Event getLast() {
        return repository.getLast();
    }
}
