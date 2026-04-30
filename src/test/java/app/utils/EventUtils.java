package app.utils;

import app.back.dto.DiscordMember;
import app.back.dto.Event;
import app.back.dto.TodoEntry;
import app.back.service.DtoDiscordMemberService;
import app.web.pojo.PojoDiscordMember;
import app.web.pojo.PojoEvent;
import app.web.pojo.PojoTodoEntry;
import app.web.transform.TransformMember;
import app.web.transform.TransformTodoEntry;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class EventUtils {

    private static final AtomicLong counter = new AtomicLong();
    private Supplier<Long> counterStrategy;
    private Supplier<LocalDateTime> dateStrategy;

    private LocalDateTime now;

    @Autowired
    @Lazy
    private DtoDiscordMemberService discordMemberService;

    @Autowired
    @Lazy
    private TransformTodoEntry transformTodoEntry;

    @Autowired
    @Lazy
    private TransformMember transformMember;

    @Autowired
    @Lazy
    private DiscordMemberUtils discordMemberUtils;

    @Autowired
    @Lazy
    private TodoEntryUtils todoEntryUtils;

    public EventUtils() {
        now = LocalDateTime.now();
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
        dateStrategy = LocalDateTime::now;
    }

    public Event createBasicEntity() {
        var event = new Event();
        event.setEventName("eventName_test_" + counterStrategy.get());
        event.setLocation("location_test_" + counterStrategy.get());
        event.setTricountUrl("tricount_test_" + counterStrategy.get());
        event.setStartDate(dateStrategy.get().plusDays(counterStrategy.get()));
        event.setEndDate(dateStrategy.get().plusDays(counterStrategy.get()));

        return event;
    }

    public Event createFullEntity() {
        var event = createBasicEntity();
        addSubEvent(event);
        addTodo(event);
        addDiscordMember(event);

        return event;
    }

    public Event addSubEvent(Event event) {
        if(event.getSubEvents() == null) {
            event.setSubEvents(new ArrayList<>());
        }

        return createSubEvent(event);
    }

    public DiscordMember addDiscordMember(Event event) {
        var discordMember = discordMemberService.save(discordMemberUtils.createBasicEntity());
        if(event.getParticipants() == null) {
            event.setParticipants(new ArrayList<>());
        }
        event.addParticipant(discordMember);

        return discordMember;
    }

    public TodoEntry addTodo(Event event) {
        return event.addTodo("todo_name_test_" + counterStrategy.get(), "todo_test_" + counterStrategy.get());
    }

    private Event createSubEvent(Event parent) {
        var event = new Event();
        event.setEventName("eventName_test_" + counterStrategy.get());

        parent.addSubEvent(event);
        return event;
    }

    public PojoEvent createBasicPojo() {
        var event = new PojoEvent();
        event.setEventName("eventName_test_" + counterStrategy.get());
        event.setLocation("location_test_" + counterStrategy.get());
        event.setTricountUrl("tricount_test_" + counterStrategy.get());
        event.setStartDate(dateStrategy.get().plusDays(counterStrategy.get()));
        event.setEndDate(dateStrategy.get().plusDays(counterStrategy.get()));

        return event;
    }

    public PojoEvent createFullPojo() {
        var event = createBasicPojo();
        addSubEvent(event);
        addTodo(event);
        addDiscordMember(event);

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

    public PojoDiscordMember addDiscordMember(PojoEvent event) {
        var discordMember = discordMemberService.save(discordMemberUtils.createBasicEntity());
        if(event.getParticipants() == null) {
            event.setParticipants(new ArrayList<>());
        }

        var result = transformMember.toPojo(discordMember);
        event.getParticipants().add(result);

        return result;
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
        event.setStartDate(dateStrategy.get().plusDays(counterStrategy.get()));

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
                DiscordMemberUtils.compare(participantList.get(i), resultParticipantList.get(i));
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
                DiscordMemberUtils.compare(baseParticipantList.get(i), resultParticipantList.get(i));
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
                DiscordMemberUtils.compare(baseParticipantList.get(i), resultParticipantList.get(i));
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
                DiscordMemberUtils.compare(participantList.get(i), resultParticipantList.get(i));
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
