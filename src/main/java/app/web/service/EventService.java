package app.web.service;

import app.back.dto.Event;
import app.back.service.DtoEventService;
import app.web.api.EventServiceApi;
import app.web.pojo.PojoEvent;
import app.back.repository.EventRepository;
import app.web.transform.TransformEvent;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EventService extends AbstractService<Event, PojoEvent, @NonNull EventRepository, DtoEventService> implements EventServiceApi {

    public EventService(DtoEventService eventService, TransformEvent transformEvent) {
        super(eventService, transformEvent);
    }

    @Override
    public PojoEvent findByEventName(String name) {
        var result =  getService().findByEventName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun événement trouvé pour le nom " + name + "."));
        return getTransform().toPojo(result);
    }

    @Override
    public PojoEvent addSubEvent(int parentEventId, PojoEvent event) {
        if(event == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucun événement donné.");
        }

        var parentEvent = getService().findById(parentEventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun parent trouvé."));
        var dtoEvent = getTransform().toDto(event);
        dtoEvent.setParentEvent(parentEvent);
        dtoEvent = getService().save(dtoEvent);
        parentEvent.getSubEvents().add(dtoEvent);
        return getTransform().toPojo(getService().save(parentEvent));
    }

}
