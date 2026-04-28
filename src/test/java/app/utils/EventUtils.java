package app.utils;

import app.back.dto.DiscordMember;
import app.back.dto.Event;
import app.back.dto.TodoEntry;
import app.back.service.DtoDiscordMemberService;
import app.back.service.DtoTodoEntryService;
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

    @Autowired
    @Lazy
    private DtoTodoEntryService dtoTodoEntryService;

    @Autowired
    @Lazy
    private DtoDiscordMemberService discordMemberService;

    @Autowired
    @Lazy
    private TransformTodoEntry transformTodoEntry;

    @Autowired
    @Lazy
    private TransformMember transformMember;

    public EventUtils() {
        countCounter();
    }

    public void stopCounter() {
        counterStrategy = counter::get;
    }

    public void countCounter() {
        counterStrategy = counter::incrementAndGet;
    }

    public Event createBasicEntity() {
        var event = new Event();
        event.setEventName("eventName_test_" + counter.getAndIncrement());
        event.setLocation("location_test_" + counter.getAndIncrement());
        event.setTricountUrl("tricount_test_" + counter.getAndIncrement());
        event.addSubEvent(createSubEvent(event));
        event.setStartDate(LocalDateTime.now().plusDays(counter.getAndIncrement()));
        event.setEndDate(LocalDateTime.now().plusDays(counter.getAndIncrement()));

        return event;
    }

    public Event createFullEntity() {
        var event = createBasicEntity();
        addSubEvent(event);
        addTodo(event);
        addDiscordMember(event);

        return event;
    }

    public void addSubEvent(Event event) {
        if(event.getSubEvents() == null) {
            event.setSubEvents(new ArrayList<>());
        }

        event.addSubEvent(createSubEvent(event));
    }

    public void addDiscordMember(Event event) {
        var discordMember = discordMemberService.save(DiscordMemberUtils.createBasicEntity());
        if(event.getParticipants() == null) {
            event.setParticipants(new ArrayList<>());
        }
        event.addParticipant(discordMember);
    }

    public void addTodo(Event event) {
        event.addTodo("todo_name_test_" + counter.getAndIncrement(), "todo_test_" + counter.getAndIncrement());
    }

    private Event createSubEvent(Event parent) {
        var event = new Event();
        event.setParentEvent(parent);
        event.setEventName("eventName_test_" + counter.getAndIncrement());

        return event;
    }

    public PojoEvent createBasicPojo() {
        var event = new PojoEvent();
        event.setEventName("eventName_test_" + counter.getAndIncrement());
        event.setLocation("location_test_" + counter.getAndIncrement());
        event.setTricountUrl("tricount_test_" + counter.getAndIncrement());
        event.setStartDate(LocalDateTime.now().plusDays(counter.getAndIncrement()));
        event.setEndDate(LocalDateTime.now().plusDays(counter.getAndIncrement()));

        return event;
    }

    public PojoEvent createFullPojo() {
        var pojo = createBasicPojo();
        addSubEvent(pojo);
        addTodo(pojo);
        addDiscordMember(pojo);

        return pojo;
    }

    public void addSubEvent(PojoEvent event) {
        if(event.getSubEvents() == null) {
            event.setSubEvents(new ArrayList<>());
        }

        event.getSubEvents().add(createSubEvent(event));
    }

    public void addDiscordMember(PojoEvent event) {
        var discordMember = discordMemberService.save(DiscordMemberUtils.createBasicEntity());
        if(event.getParticipants() == null) {
            event.setParticipants(new ArrayList<>());
        }
        event.getParticipants().add(transformMember.toPojo(discordMember));
    }

    public void addTodo(PojoEvent event) {
        var entry = new PojoTodoEntry();
        entry.setName("todo_name_test_" + counter.getAndIncrement());
        entry.setTodoValue("todo_test_" + counter.getAndIncrement());
        if(event.getTodoList() == null) {
            event.setTodoList(new ArrayList<>());
        }
        event.getTodoList().add(entry);
    }

    private PojoEvent createSubEvent(PojoEvent parent) {
        var event = new PojoEvent();
        event.setParentEvent(parent);
        event.setEventName("eventName_" + counter.getAndIncrement());
        event.setStartDate(LocalDateTime.now().plusDays(counter.getAndIncrement()));

        return event;
    }

    public static void compare(Event event, PojoEvent result) {
        Assertions.assertEquals(event.getId(), result.getId());
        Assertions.assertEquals(event.getEventName(), result.getEventName());
        Assertions.assertEquals(event.getLocation(), result.getLocation());
        Assertions.assertEquals(event.getTricountUrl(), result.getTricountUrl());
        Assertions.assertEquals(event.getStartDate(), result.getStartDate());
        Assertions.assertEquals(event.getEndDate(), result.getEndDate());
        if(event.getSubEvents() != null) {
            Assertions.assertEquals(event.getSubEvents().size(), result.getSubEvents().size());
            for(int i = 0; i < event.getSubEvents().size(); i++) {
                compare(event.getSubEvents().get(i), result.getSubEvents().get(i));
            }
        }
        if(event.getParticipants() != null) {
            Assertions.assertEquals(event.getParticipants().size(), result.getParticipants().size());
            for(int i = 0; i < event.getParticipants().size(); i++) {
                var participantList = new ArrayList<>(event.getParticipants());
                var pojoParticipantList = new ArrayList<>(result.getParticipants());
                DiscordMemberUtils.compare(participantList.get(i), pojoParticipantList.get(i));
            }
        }
        if(event.getTodoList() != null) {
            Assertions.assertEquals(event.getTodoList().size(), result.getTodoList().size());
            for(int i = 0; i < event.getTodoList().size(); i++) {
                var pojoTodoList = event.getTodoList();
                var resultTodoListMap = result.getTodoList().stream().collect(Collectors.toMap(PojoTodoEntry::getName, Function.identity()));
                var pojoTodoEntry = pojoTodoList.get(i);
                var resultTodoEntry = resultTodoListMap.get(pojoTodoEntry.getTodoName());

                var resultDiscordMemberMap = resultTodoEntry.getDiscordMembers().stream().collect(Collectors.toMap(PojoDiscordMember::getDiscordId, Function.identity()));
                if(pojoTodoEntry.getDiscordMembers() == null) {
                    pojoTodoEntry.setDiscordMemberSet(new ArrayList<>());
                }
                Assertions.assertEquals(pojoTodoEntry.getDiscordMembers().size(), resultDiscordMemberMap.size());
                var pojoTodoDiscordMembers = new ArrayList<>(pojoTodoEntry.getDiscordMembers());
                for(DiscordMember pojoTodoMember : pojoTodoDiscordMembers) {
                    DiscordMemberUtils.compare(pojoTodoMember, resultDiscordMemberMap.get(pojoTodoMember.getDiscordId()));
                }
            }
        }

        if(event.getParentEvent() == null && result.getParentEvent() == null) {
            return;
        }

        // pour comparer de façon récursive, on enlève les enfants des parents pour ne pas revenir au point de départ lors de la récupération des sous événements
        var subEvents = event.getParentEvent().getSubEvents();
        var resultSubEvents = result.getParentEvent().getSubEvents();

        event.getParentEvent().setSubEvents(new ArrayList<>());
        result.getParentEvent().setSubEvents(new ArrayList<>());

        compare(event.getParentEvent(), result.getParentEvent());

        event.getParentEvent().setSubEvents(subEvents);
        result.getParentEvent().setSubEvents(resultSubEvents);
    }

    public static void compare(PojoEvent pojo, Event result) {
        Assertions.assertEquals(pojo.getId(), result.getId());
        Assertions.assertEquals(pojo.getEventName(), result.getEventName());
        Assertions.assertEquals(pojo.getLocation(), result.getLocation());
        Assertions.assertEquals(pojo.getTricountUrl(), result.getTricountUrl());
        Assertions.assertEquals(pojo.getStartDate(), result.getStartDate());
        Assertions.assertEquals(pojo.getEndDate(), result.getEndDate());
        if(pojo.getSubEvents() != null) {
            Assertions.assertEquals(pojo.getSubEvents().size(), result.getSubEvents().size());
            for(int i = 0; i < pojo.getSubEvents().size(); i++) {
                compare(pojo.getSubEvents().get(i), result.getSubEvents().get(i));
            }
        }
        if(pojo.getParticipants() != null) {
            Assertions.assertEquals(pojo.getParticipants().size(), result.getParticipants().size());
            for(int i = 0; i < pojo.getParticipants().size(); i++) {
                var pojoParticipantList = new ArrayList<>(pojo.getParticipants());
                var resultParticipantList = new ArrayList<>(result.getParticipants());
                DiscordMemberUtils.compare(pojoParticipantList.get(i), resultParticipantList.get(i));
            }
        }
        if(pojo.getTodoList() != null) {
            Assertions.assertEquals(pojo.getTodoList().size(), result.getTodoList().size());
            for(int i = 0; i < pojo.getTodoList().size(); i++) {
                var pojoTodoList = pojo.getTodoList();
                var resultTodoListMap = result.getTodoList().stream().collect(Collectors.toMap(TodoEntry::getTodoName, Function.identity()));
                var pojoTodoEntry = pojoTodoList.get(i);
                var resultTodoEntry = resultTodoListMap.get(pojoTodoEntry.getName());

                var resultDiscordMemberMap = resultTodoEntry.getDiscordMembers().stream().collect(Collectors.toMap(DiscordMember::getDiscordId, Function.identity()));
                if(pojoTodoEntry.getDiscordMembers() == null) {
                    pojoTodoEntry.setDiscordMembers(new ArrayList<>());
                }
                Assertions.assertEquals(pojoTodoEntry.getDiscordMembers().size(), resultDiscordMemberMap.size());
                for(var pojoTodoMember : pojoTodoEntry.getDiscordMembers()) {
                    DiscordMemberUtils.compare(pojoTodoMember, resultDiscordMemberMap.get(pojoTodoMember.getDiscordId()));
                }
            }
        }

        if(pojo.getParentEvent() == null && result.getParentEvent() == null) {
            return;
        }

        // pour comparer de façon récursive, on enlève les enfants des parents pour ne pas revenir au point de départ lors de la récupération des sous événements
        var pojoSubEvents = pojo.getParentEvent().getSubEvents();
        var resultSubEvents = result.getParentEvent().getSubEvents();

        pojo.getParentEvent().setSubEvents(new ArrayList<>());
        result.getParentEvent().setSubEvents(new ArrayList<>());

        compare(pojo.getParentEvent(), result.getParentEvent());

        pojo.getParentEvent().setSubEvents(pojoSubEvents);
        result.getParentEvent().setSubEvents(resultSubEvents);
    }

}
