package app.utils;

import app.back.dto.Event;
import app.back.dto.TodoEntry;
import app.back.security.UserServiceApi;
import app.back.service.DtoUserAttributesService;
import app.serviceprimary.KeycloakUserServiceTest;
import app.web.pojo.PojoEvent;
import app.web.pojo.PojoTodoEntry;
import app.web.pojo.PojoUser;
import app.web.transform.TransformMember;
import app.web.transform.TransformTodoEntry;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class EventUtils {

    private static final AtomicLong counter = new AtomicLong();
    private Supplier<Long> counterStrategy;
    private Supplier<Instant> dateStrategy;

    private Instant now;

    @Autowired
    @Lazy
    private DtoUserAttributesService discordMemberService;

    @Autowired
    @Lazy
    private UuidUtils uuidUtils;

    @Autowired
    @Lazy
    private TransformTodoEntry transformTodoEntry;

    @Autowired
    @Lazy
    private TransformMember transformMember;

    @Autowired
    @Lazy
    private UserAttributesUtils discordMemberUtils;

    @Autowired
    @Lazy
    private TodoEntryUtils todoEntryUtils;

    @Autowired
    @Lazy
    private UserServiceApi userServiceApi;

    @Autowired
    @Lazy
    private KeycloakUserServiceTest keycloakUserServiceTest;

    public EventUtils() {
        now = Instant.now();
        playCounter();
        playDate();
    }

    public void stopAll() {
        stopCounter();
        stopDate();
    }

    public void playALl() {
        playCounter();
        playDate();
    }

    public void stopCounter() {
        counterStrategy = counter::get;
    }

    public void playCounter() {
        counterStrategy = counter::incrementAndGet;
    }

    public void stopDate() {
        dateStrategy = () -> now;
    }

    public void playDate() {
        dateStrategy = Instant::now;
    }

    public Event createBasicEntity() {
        var event = new Event();
        event.setEventName("eventName_test_" + counterStrategy.get());
        event.setLocation("location_test_" + counterStrategy.get());
        event.setTricountUrl("tricount_test_" + counterStrategy.get());
        event.setStartDate(dateStrategy.get().plus(counterStrategy.get(), ChronoUnit.DAYS));
        event.setEndDate(dateStrategy.get().plus(counterStrategy.get(), ChronoUnit.DAYS));
        event.setOwnerUserId(userServiceApi.getUser().getUserId());

        return event;
    }

    public Event createFullEntity() {
        var event = createBasicEntity();
        addSubEvent(event);
        addTodo(event);
        addUserId(event);

        return event;
    }

    public Event addSubEvent(Event event) {
        if(event.getSubEvents() == null) {
            event.setSubEvents(new ArrayList<>());
        }

        return createSubEvent(event);
    }

    public UUID addUserId(Event event) {
        var userId = uuidUtils.generate();
        if(event.getParticipants() == null) {
            event.setParticipants(new ArrayList<>());
        }
        event.addParticipant(userId);

        keycloakUserServiceTest.addNewUser(userId, "", "");

        return userId;
    }

    public TodoEntry addTodo(Event event) {
        return event.addTodo("todo_name_test_" + counterStrategy.get(), "todo_test_" + counterStrategy.get());
    }

    private Event createSubEvent(Event parent) {
        var event = new Event();
        event.setEventName("eventName_test_" + counterStrategy.get());
        event.setOwnerUserId(userServiceApi.getUser().getUserId());

        parent.addSubEvent(event);
        return event;
    }

    public PojoEvent createBasicPojo() {
        var event = new PojoEvent();
        event.setEventName("eventName_test_" + counterStrategy.get());
        event.setLocation("location_test_" + counterStrategy.get());
        event.setTricountUrl("tricount_test_" + counterStrategy.get());
        event.setStartDate(dateStrategy.get().plus(counterStrategy.get(), ChronoUnit.DAYS));
        event.setEndDate(dateStrategy.get().plus(counterStrategy.get(), ChronoUnit.DAYS));

        return event;
    }

    public PojoEvent createFullPojo() {
        var event = createBasicPojo();
        addSubEvent(event);
        addTodo(event);
        addUserId(event);

        return event;
    }

    public PojoEvent addSubEvent(PojoEvent event) {
        if(event.getSubEvents() == null) {
            event.setSubEvents(new ArrayList<>());
        }

        var result = createSubEvent(event);
        event.getSubEvents().add(result);
        return result;
    }

    public UUID addUserId(PojoEvent event) {
        var userId = uuidUtils.generate();
        if(event.getParticipants() == null) {
            event.setParticipants(new ArrayList<>());
        }

        var pojo = new PojoUser();
        pojo.setId(userId);
        event.getParticipants().add(pojo);

        keycloakUserServiceTest.addNewUser(userId, "", "");

        return userId;
    }

    public PojoTodoEntry addTodo(PojoEvent event) {
        var entry = todoEntryUtils.createBasicTodoEntry();
        if(event.getTodoList() == null) {
            event.setTodoList(new ArrayList<>());
        }
        event.getTodoList().add(entry);

        return entry;
    }

    private PojoEvent createSubEvent(PojoEvent parent) {
        var event = new PojoEvent();
        event.setParentEvent(parent);
        event.setEventName("eventName_" + counterStrategy.get());
        event.setStartDate(dateStrategy.get().plus(counterStrategy.get(), ChronoUnit.DAYS));

        return event;
    }

    public static void compare(Event base, PojoEvent result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(base.getEventName(), result.getEventName());
        Assertions.assertEquals(base.getLocation(), result.getLocation());
        Assertions.assertEquals(base.getTricountUrl(), result.getTricountUrl());
        Assertions.assertEquals(base.getStartDate(), result.getStartDate());
        Assertions.assertEquals(base.getEndDate(), result.getEndDate());
        if(base.getSubEvents() != null) {
            Assertions.assertEquals(base.getSubEvents().size(), result.getSubEvents().size());
            for(int i = 0; i < base.getSubEvents().size(); i++) {
                compare(base.getSubEvents().get(i), result.getSubEvents().get(i));
            }
        }
        if(base.getParticipants() != null) {
            Assertions.assertEquals(base.getParticipants().size(), result.getParticipants().size());
            for(int i = 0; i < base.getParticipants().size(); i++) {
                var participantList = new ArrayList<>(base.getParticipants());
                var resultParticipantList = new ArrayList<>(result.getParticipants());
                Assertions.assertEquals(participantList.get(i), resultParticipantList.get(i).getId());
            }
        }
        if(base.getTodoList() != null) {
            Assertions.assertEquals(base.getTodoList().size(), result.getTodoList().size());
            var resultTodoListMap = result.getTodoList().stream().collect(Collectors.toMap(PojoTodoEntry::getName, Function.identity()));
            for(var baseTodoEntry : base.getTodoList()) {
                var resultTodoEntry = resultTodoListMap.get(baseTodoEntry.getTodoName());
                TodoEntryUtils.compare(baseTodoEntry, resultTodoEntry);
            }
        }

        if(base.getParentEvent() == null && result.getParentEvent() == null) {
            return;
        }

        // pour comparer de façon récursive, on enlève les enfants des parents pour ne pas revenir au point de départ lors de la récupération des sous événements
        var subEvents = base.getParentEvent().getSubEvents();
        var resultSubEvents = result.getParentEvent().getSubEvents();

        base.getParentEvent().setSubEvents(new ArrayList<>());
        result.getParentEvent().setSubEvents(new ArrayList<>());

        compare(base.getParentEvent(), result.getParentEvent());

        base.getParentEvent().setSubEvents(subEvents);
        result.getParentEvent().setSubEvents(resultSubEvents);
    }

    public static void compare(PojoEvent base, Event result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getEventName(), result.getEventName());
        Assertions.assertEquals(base.getLocation(), result.getLocation());
        Assertions.assertEquals(base.getTricountUrl(), result.getTricountUrl());
        Assertions.assertEquals(base.getStartDate(), result.getStartDate());
        Assertions.assertEquals(base.getEndDate(), result.getEndDate());
        if(base.getSubEvents() != null) {
            Assertions.assertEquals(base.getSubEvents().size(), result.getSubEvents().size());
            for(int i = 0; i < base.getSubEvents().size(); i++) {
                compare(base.getSubEvents().get(i), result.getSubEvents().get(i));
            }
        }
        if(base.getParticipants() != null) {
            Assertions.assertEquals(base.getParticipants().size(), result.getParticipants().size());
            for(int i = 0; i < base.getParticipants().size(); i++) {
                var baseParticipantList = new ArrayList<>(base.getParticipants());
                var resultParticipantList = new ArrayList<>(result.getParticipants());
                Assertions.assertEquals(baseParticipantList.get(i).getId(), resultParticipantList.get(i));
            }
        }
        if(base.getTodoList() != null) {
            Assertions.assertEquals(base.getTodoList().size(), result.getTodoList().size());
            var resultTodoListMap = result.getTodoList().stream().collect(Collectors.toMap(TodoEntry::getTodoName, Function.identity()));
            for(var baseTodoEntry : base.getTodoList()) {
                var resultTodoEntry = resultTodoListMap.get(baseTodoEntry.getName());

                TodoEntryUtils.compare(baseTodoEntry, resultTodoEntry);
            }
        }

        if(base.getParentEvent() == null && result.getParentEvent() == null) {
            return;
        }

        // pour comparer de façon récursive, on enlève les enfants des parents pour ne pas revenir au point de départ lors de la récupération des sous événements
        var baseSubEvents = base.getParentEvent().getSubEvents();
        var resultSubEvents = result.getParentEvent().getSubEvents();

        base.getParentEvent().setSubEvents(new ArrayList<>());
        result.getParentEvent().setSubEvents(new ArrayList<>());

        compare(base.getParentEvent(), result.getParentEvent());

        base.getParentEvent().setSubEvents(baseSubEvents);
        result.getParentEvent().setSubEvents(resultSubEvents);
    }

    public static void compare(PojoEvent base, PojoEvent result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getEventName(), result.getEventName());
        Assertions.assertEquals(base.getLocation(), result.getLocation());
        Assertions.assertEquals(base.getTricountUrl(), result.getTricountUrl());
        Assertions.assertEquals(base.getStartDate(), result.getStartDate());
        Assertions.assertEquals(base.getEndDate(), result.getEndDate());
        if(base.getSubEvents() != null) {
            Assertions.assertEquals(base.getSubEvents().size(), result.getSubEvents().size());
            for(int i = 0; i < base.getSubEvents().size(); i++) {
                compare(base.getSubEvents().get(i), result.getSubEvents().get(i));
            }
        }
        if(base.getParticipants() != null) {
            Assertions.assertEquals(base.getParticipants().size(), result.getParticipants().size());
            for(int i = 0; i < base.getParticipants().size(); i++) {
                var baseParticipantList = new ArrayList<>(base.getParticipants());
                var resultParticipantList = new ArrayList<>(result.getParticipants());
                Assertions.assertEquals(baseParticipantList.get(i), resultParticipantList.get(i));
            }
        }
        if(base.getTodoList() != null) {
            Assertions.assertEquals(base.getTodoList().size(), result.getTodoList().size());
            var resultTodoListMap = result.getTodoList().stream().collect(Collectors.toMap(PojoTodoEntry::getName, Function.identity()));
            for(var baseTodoEntry : base.getTodoList()) {
                var resultTodoEntry = resultTodoListMap.get(baseTodoEntry.getName());

                TodoEntryUtils.compare(baseTodoEntry, resultTodoEntry);
            }
        }

        if(base.getParentEvent() == null && result.getParentEvent() == null) {
            return;
        }

        // pour comparer de façon récursive, on enlève les enfants des parents pour ne pas revenir au point de départ lors de la récupération des sous événements
        var baseSubEvents = base.getParentEvent().getSubEvents();
        var resultSubEvents = result.getParentEvent().getSubEvents();

        base.getParentEvent().setSubEvents(new ArrayList<>());
        result.getParentEvent().setSubEvents(new ArrayList<>());

        compare(base.getParentEvent(), result.getParentEvent());

        base.getParentEvent().setSubEvents(baseSubEvents);
        result.getParentEvent().setSubEvents(resultSubEvents);
    }

    public static void compare(Event base, Event result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getEventName(), result.getEventName());
        Assertions.assertEquals(base.getLocation(), result.getLocation());
        Assertions.assertEquals(base.getTricountUrl(), result.getTricountUrl());
        Assertions.assertEquals(base.getStartDate(), result.getStartDate());
        Assertions.assertEquals(base.getEndDate(), result.getEndDate());
        if(base.getSubEvents() != null) {
            Assertions.assertEquals(base.getSubEvents().size(), result.getSubEvents().size());
            for(int i = 0; i < base.getSubEvents().size(); i++) {
                compare(base.getSubEvents().get(i), result.getSubEvents().get(i));
            }
        }
        if(base.getParticipants() != null) {
            Assertions.assertEquals(base.getParticipants().size(), result.getParticipants().size());
            for(int i = 0; i < base.getParticipants().size(); i++) {
                var participantList = new ArrayList<>(base.getParticipants());
                var resultParticipantList = new ArrayList<>(result.getParticipants());
                Assertions.assertEquals(participantList.get(i), resultParticipantList.get(i));
            }
        }
        if(base.getTodoList() != null) {
            Assertions.assertEquals(base.getTodoList().size(), result.getTodoList().size());
            var resultTodoListMap = result.getTodoList().stream().collect(Collectors.toMap(TodoEntry::getTodoName, Function.identity()));
            for(var baseTodoEntry : base.getTodoList()) {
                var resultTodoEntry = resultTodoListMap.get(baseTodoEntry.getTodoName());

                TodoEntryUtils.compare(baseTodoEntry, resultTodoEntry);
            }
        }

        if(base.getParentEvent() == null && result.getParentEvent() == null) {
            return;
        }

        // pour comparer de façon récursive, on enlève les enfants des parents pour ne pas revenir au point de départ lors de la récupération des sous événements
        var baseSubEvents = base.getParentEvent().getSubEvents();
        var resultSubEvents = result.getParentEvent().getSubEvents();

        base.getParentEvent().setSubEvents(new ArrayList<>());
        result.getParentEvent().setSubEvents(new ArrayList<>());

        compare(base.getParentEvent(), result.getParentEvent());

        base.getParentEvent().setSubEvents(baseSubEvents);
        result.getParentEvent().setSubEvents(resultSubEvents);
    }

}
