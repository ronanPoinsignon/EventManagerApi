package app.web.service;

import app.back.api.DtoEventServiceApi;
import app.back.dto.Event;
import app.back.dto.TodoEntry;
import app.utils.FileService;
import app.web.api.EventServiceApi;
import app.web.exception.BadRequestException;
import app.web.exception.NotFoundException;
import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoEvent;
import app.web.pojo.PojoUserAttributes;
import app.web.transform.TransformEvent;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class EventService extends AbstractService<Event, PojoEvent, DtoEventServiceApi> implements EventServiceApi {

    private final UserAttributesService userAttributesService;
    private final FileService fileService;

    public EventService(DtoEventServiceApi eventService, TransformEvent transformEvent, UserAttributesService userAttributesService, FileService fileService) {
        super(eventService, transformEvent);
        this.userAttributesService = userAttributesService;
        this.fileService = fileService;
    }

    @Transactional
    @Override
    public PojoEvent discordSave(PojoEvent event, String parentEventName) {
        var eventName = event.getEventName();
        if(parentEventName != null) {
            getService().findByEventName(parentEventName, eventName).ifPresent(evt -> event.setId(evt.getId()));
        } else {
            getService().findByEventName(eventName).ifPresent(evt -> event.setId(evt.getId()));
        }
        return this.save(event);
    }

    @Transactional
    @Override
    public PojoEvent findByEventName(String name) {
        var result =  getService().findByEventName(name)
                .orElseThrow(() -> new NotFoundException("Aucun événement trouvé pour le nom " + name + "."));
        return getTransform().toPojo(result);
    }

    @Override
    public PojoEvent findByEventName(String parentName, String name) {
        if(parentName == null) {
            return this.findByEventName(name);
        }

        var event = getService().findByEventName(parentName, name)
                .orElseThrow(() -> new NotFoundException("Aucun programme trouvé pour le nom " + name + "."));
        return getTransform().toPojo(event);
    }

    @Override
    @Transactional
    public PojoEvent addSubEvent(long parentEventId, PojoEvent event) {
        if(event == null) {
            throw new BadRequestException("Aucun événement donné.");
        }

        return addSubEvent(eventSupplierById(parentEventId), event);
    }

    @Override
    @Transactional
    public PojoEvent removeSubEvent(long parentEventId, String subEventName) {
        if(subEventName == null || subEventName.isBlank()) {
            throw new BadRequestException("le nom du sous événement ne peut être null.");
        }

        return removeSubEvent(eventSupplierById(parentEventId), subEventName);
    }

    private PojoEvent addSubEvent(Supplier<Optional<Event>> eventSupplier, PojoEvent event) {
        var parentEvent = eventSupplier.get().orElseThrow(() -> new NotFoundException("Aucun parent trouvé."));
        var dtoEvent = getTransform().toDto(event);
        parentEvent.addSubEvent(dtoEvent);
        getService().save(dtoEvent);
        dtoEvent.setParticipants(parentEvent.getParticipants());

        parentEvent = getService().save(parentEvent);
        return getTransform().toPojo(parentEvent);
    }

    public PojoEvent removeSubEvent(Supplier<Optional<Event>> eventSupplier, String subEventName) {
        if(subEventName == null || subEventName.isBlank()) {
            throw new BadRequestException("le nom du sous événement ne peut être null.");
        }

        var event = eventSupplier.get().orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var subEvent = event.getSubEvents().stream().filter(subevent -> subevent.getEventName().equals(subEventName)).findFirst().orElseThrow(() -> new NotFoundException("Aucun sous événement trouvé pour ce nom"));
        event.removeSubEvent(subEvent);
        getService().delete(subEvent.getId());

        return getTransform().toPojo(getService().save(event));
    }

    @Transactional
    @Override
    public List<PojoEvent> findAllBeforeEnd(Instant date) {
        return getService().findAllBeforeEnd(date).stream().map(getTransform()::toPojo).toList();
    }

    @Override
    public List<PojoEvent> findAll() {
        return getService().findAll().stream().map(getTransform()::toPojo).toList();
    }

    @Transactional
    @Override
    public PojoEvent getLast() {
        var result = getService().getLast().orElseThrow(() -> new NotFoundException("Aucun événement de renseigné."));
        return getTransform().toPojo(result);
    }

    @Transactional
    @Override
    public PojoEvent addTo(long eventId, List<UUID> userIds) {
        return manageParticipants(() -> getService().findById(eventId), userIds, Event::addParticipants);
    }

    @Transactional
    @Override
    public PojoEvent removeTo(long eventId, List<UUID> userIdList) {
        return manageParticipants(() -> getService().findById(eventId), userIdList, Event::removeParticipants);
    }

    private <T> PojoEvent manageParticipants(Supplier<Optional<Event>> eventSupplier, List<T> participantList, BiFunction<Event, List<T>, Boolean> participantFunction) {
        var event = eventSupplier.get().orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));

        var hasChanged = participantFunction.apply(event, participantList);
        if(!hasChanged) {
            return getTransform().toPojo(event);
        }

        return getTransform().toPojo(getService().save(event));
    }

    @Transactional
    @Override
    public PojoEvent addTodo(long eventId, LightPojoTodoEntry lightPojoTodoEntry, List<UUID> userIds, boolean isDone) {
        if(lightPojoTodoEntry == null) {
            throw new BadRequestException("Document d'information manquant.");
        }

        return addTodo(eventSupplierById(eventId), lightPojoTodoEntry, userIds, isDone);
    }

    @Transactional
    @Override
    public PojoEvent addDiscordTo(long eventId, List<Long> userIds) {
        return manageParticipants(() -> getService().findById(eventId), findAllKeycloakUserIds(userIds), Event::addParticipants);
    }

    @Transactional
    @Override
    public PojoEvent removeDiscordTo(long eventId, List<Long> userIds) {
        return manageParticipants(() -> getService().findById(eventId), findAllKeycloakUserIds(userIds), Event::removeParticipants);
    }


    /**
     *
     * @param discordUserIdList
     * @throws NotFoundException Si certains utilisateurs n'ont pas été trouvés, une {@link NotFoundException} est levée indiquant quels utilisateurs n'ont pas été récupérés.
     * @return
     */
    private List<UUID> findAllKeycloakUserIds(List<Long> discordUserIdList) throws NotFoundException {
        if(discordUserIdList == null) {
            discordUserIdList = new ArrayList<>();
        }
        var result = this.findKeycloakUserIds(discordUserIdList);

        if(result.size() != discordUserIdList.size()) {
            var newDiscordMemberIds = new ArrayList<>(discordUserIdList);
            for(int i = 0; i < discordUserIdList.size(); i++) {
                if(result.get(i) == null) {
                    newDiscordMemberIds.remove(discordUserIdList.get(i));
                }
            }
            throw new NotFoundException("les ids de membres suivants n'ont pas été trouvé : " + newDiscordMemberIds + ".");
        }

        return result;
    }

    private List<UUID> findKeycloakUserIds(List<Long> discordUserIdList) {
        if(discordUserIdList == null || discordUserIdList.isEmpty()) {
            return new ArrayList<>();
        }

        return discordUserIdList.stream()
                .map(userAttributesService::findByDiscordId)
                .map(PojoUserAttributes::getKeycloakUserId)
                .map(UUID::fromString)
                .toList();
    }

    @Transactional
    @Override
    public PojoEvent removeTodo(long eventId, String name) {
        if(name == null || name.isBlank()) {
            throw new BadRequestException("Le nom est obligatoire.");
        }

        return removeTodo(() -> getService().findById(eventId), name);
    }

    private PojoEvent addTodo(Supplier<Optional<Event>> eventSupplier, LightPojoTodoEntry lightPojoTodoEntry, List<UUID> userIds, boolean isDone) {
        if(lightPojoTodoEntry == null) {
            throw new BadRequestException("Document d'information manquant.");
        }

        var event = eventSupplier.get().orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var todo = event.addTodo(lightPojoTodoEntry.getName(), lightPojoTodoEntry.getTodo(), lightPojoTodoEntry.getParticipants());
        todo.setUserIdSet(userIds);
        todo.setDone(isDone);

        var result = getService().save(event);
        return getTransform().toPojo(result);
    }

    private PojoEvent removeTodo(Supplier<Optional<Event>> eventSupplier, String name) {
        if(name == null || name.isBlank()) {
            throw new BadRequestException("Le nom est obligatoire.");
        }

        var event = eventSupplier.get().orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var result = event.removeTodo(name);
        if(!result) {
            return getTransform().toPojo(event);
        }

        return getTransform().toPojo(getService().save(event));
    }

    @Transactional
    @Override
    public PojoEvent addTodoUsers(long eventId, String todoName, List<UUID> userIds) {
        return manageTodoMember(eventSupplierById(eventId), todoName, userIds, TodoEntry::addUserIds);
    }

    @Transactional
    @Override
    public PojoEvent removeTodoUsers(long eventId, String todoName, List<UUID> userIds) {
        return manageTodoMember(eventSupplierById(eventId), todoName, userIds, TodoEntry::removeUserIds);
    }

    @Transactional
    private <T> PojoEvent manageTodoMember(Supplier<Optional<Event>> eventSupplier, String todoName, T userList, BiFunction<TodoEntry, T, Boolean> memberFunction) {
        return updateTodoInfo(eventSupplier, todoName, todo -> memberFunction.apply(todo, userList));
    }

    @Transactional
    @Override
    public PojoEvent delete(long eventId) {
        return getService().findAndDelete(eventId)
                .map(getTransform()::toPojo)
                .orElse(null);
    }

    @Transactional
    @Override
    public PojoEvent setParticipant(long eventId, List<UUID> userIds) {
        return manageParticipants(eventSupplierById(eventId), userIds, Event::setParticipants);
    }

    @Override
    public PojoEvent findEventFromTodoId(long todoId) {
        var event = getService().findEventFromTodoId(todoId).orElseThrow(() -> new NotFoundException("Aucun événement rattaché au todo d'id " + todoId + "."));
        return getTransform().toPojo(event);
    }

    @Override
    public InputStreamResource uploadEventImageFile(long eventId, MultipartFile eventFile) {
        if (eventFile.getContentType() == null || !eventFile.getContentType().equals("image/png")) {
            throw new BadRequestException("Seuls les PNG sont autorisés.");
        }

        var result =  fileService.storeFile("/events", eventFile, eventId + ".png");
        return new InputStreamResource(result);
    }

    @Override
    public InputStreamResource downloadEventImageFile(long eventId) {
        var fileInputStream = fileService.getFileInputStream("/events", eventId + ".png");
        if(fileInputStream == null) {
            return null;
        }

        return new InputStreamResource(fileInputStream);
    }

    @Transactional
    @Override
    public PojoEvent updateTodoStatus(long eventId, String todoName, boolean isDone) {
        return updateTodoInfo(eventSupplierById(eventId), todoName, todo -> todo.setDone(isDone));
    }

    private Supplier<Optional<Event>> eventSupplierById(long eventId) {
        return () -> getService().findById(eventId);
    }

    private PojoEvent updateTodoInfo(Supplier<Optional<Event>> eventSupplier, String todoName, Function<TodoEntry, Boolean> todoFunction) {
        if(todoName == null || todoName.isBlank()) {
            throw new BadRequestException("Le nom est obligatoire.");
        }

        var event = eventSupplier.get().orElseThrow(() -> new NotFoundException("Aucun événement trouvé."));
        var todo = event.findTodoEntryByName(todoName);
        if(todo == null) {
            throw new NotFoundException("Aucun todo enregistré avec ce nom pour l'événement " + event.getEventName() + ".");
        }

        var result = todoFunction.apply(todo);

        if(!result) {
            return getTransform().toPojo(event);
        }

        return getTransform().toPojo(getService().save(event));
    }

}
