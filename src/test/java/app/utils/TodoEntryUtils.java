package app.utils;

import app.back.dto.DiscordMember;
import app.back.dto.TodoEntry;
import app.back.service.DtoDiscordMemberService;
import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoDiscordMember;
import app.web.pojo.PojoTodoEntry;
import app.web.transform.TransformMember;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class TodoEntryUtils {

    private static final AtomicLong counter = new AtomicLong();
    private Supplier<Long> counterStrategy;

    @Autowired
    @Lazy
    private DtoDiscordMemberService discordMemberService;
    @Autowired
    @Lazy
    private TransformMember transformMember;

    @Autowired
    @Lazy
    private DiscordMemberUtils discordMemberUtils;
    public TodoEntryUtils() {
        playCounter();
    }

    public void stopAll() {
        stopCounter();
    }

    public void playALl() {
        playCounter();
    }

    public void stopCounter() {
        counterStrategy = counter::get;
    }

    public void playCounter() {
        counterStrategy = counter::incrementAndGet;
    }

    public DiscordMember addDiscordMember(TodoEntry todoEntry) {
        var discordMember = discordMemberService.save(discordMemberUtils.createBasicEntity());
        todoEntry.addDiscordMember(discordMember);
        return discordMember;
    }

    public DiscordMember addDiscordMember(LightPojoTodoEntry todoEntry) {
        var discordMember = discordMemberService.save(discordMemberUtils.createBasicEntity());
        todoEntry.getParticipants().add(discordMember.getId());
        return discordMember;
    }

    public PojoDiscordMember addDiscordMember(PojoTodoEntry todoEntry) {
        var discordMember = transformMember.toPojo(discordMemberService.save(discordMemberUtils.createBasicEntity()));
        if(todoEntry.getDiscordMembers() == null) {
            todoEntry.setDiscordMembers(new ArrayList<>());
        }
        todoEntry.getDiscordMembers().add(discordMember);

        return discordMember;
    }

    public LightPojoTodoEntry createBasicLightTodoEntry() {
        var todo = new LightPojoTodoEntry();
        todo.setTodo("todo_" + counterStrategy.get());
        todo.setName("name_" + counterStrategy.get());
        todo.setParticipants(new ArrayList<>());

        return todo;
    }

    public LightPojoTodoEntry createFullLightTodoEntry() {
        var todo = new LightPojoTodoEntry();
        todo.setTodo("todo_" + counterStrategy.get());
        todo.setName("name_" + counterStrategy.get());
        todo.setParticipants(new ArrayList<>());

        addDiscordMember(todo);

        return todo;
    }

    public PojoTodoEntry createBasicTodoEntry() {
        var todo = new PojoTodoEntry();
        todo.setTodoValue("todo_" + counterStrategy.get());
        todo.setName("name_" + counterStrategy.get());

        return todo;
    }

    public PojoTodoEntry createFullTodoEntry() {
        var todo = new PojoTodoEntry();
        todo.setTodoValue("todo_" + counterStrategy.get());
        todo.setName("name_" + counterStrategy.get());

        addDiscordMember(todo);

        return todo;
    }

    public static void compare(TodoEntry base, PojoTodoEntry result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getTodoValue(), result.getTodoValue());
        Assertions.assertEquals(base.getTodoName(), result.getName());
        if(base.getEvent() != null) {
            Assertions.assertEquals(base.getEvent().getId(), result.getEvent().getId());
        }
        Assertions.assertEquals(base.isDone(), result.isDone());
        var baseDiscordMemberList = new ArrayList<>(base.getDiscordMembers());
        var resultDiscordMemberMap = result.getDiscordMembers().stream().collect(Collectors.toMap(PojoDiscordMember::getDiscordId, Function.identity()));
        for(int i = 0; i < base.getDiscordMembers().size(); i++) {
            var member = baseDiscordMemberList.get(i);
            DiscordMemberUtils.compare(baseDiscordMemberList.get(i), resultDiscordMemberMap.get(member.getDiscordId()));
        }
    }

    public static void compare(TodoEntry base, TodoEntry result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getTodoValue(), result.getTodoValue());
        Assertions.assertEquals(base.getTodoName(), result.getTodoName());
        if(base.getEvent() != null) {
            Assertions.assertEquals(base.getEvent().getId(), result.getEvent().getId());
        }
        Assertions.assertEquals(base.isDone(), result.isDone());
        var resultDiscordMemberMap = result.getDiscordMembers().stream().collect(Collectors.toMap(DiscordMember::getDiscordId, Function.identity()));
        for(var member : base.getDiscordMembers()) {
            DiscordMemberUtils.compare(member, resultDiscordMemberMap.get(member.getDiscordId()));
        }
    }

    public static void compare(PojoTodoEntry base, TodoEntry result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getTodoValue(), result.getTodoValue());
        Assertions.assertEquals(base.getName(), result.getTodoName());
        if(base.getEvent() != null) {
            Assertions.assertEquals(base.getEvent().getId(), result.getEvent().getId());
        }
        Assertions.assertEquals(base.isDone(), result.isDone());
        var resultDiscordMemberMap = result.getDiscordMembers().stream().collect(Collectors.toMap(DiscordMember::getDiscordId, Function.identity()));
        if(base.getDiscordMembers() != null) {
            for(var member : base.getDiscordMembers()) {
                DiscordMemberUtils.compare(member, resultDiscordMemberMap.get(member.getDiscordId()));
            }
        }
    }

    public static void compare(PojoTodoEntry base, PojoTodoEntry result) {
        Assertions.assertEquals(base.getId(), result.getId());
        Assertions.assertEquals(base.getTodoValue(), result.getTodoValue());
        Assertions.assertEquals(base.getName(), result.getName());
        if(base.getEvent() != null) {
            Assertions.assertEquals(base.getEvent().getId(), result.getEvent().getId());
        }
        Assertions.assertEquals(base.isDone(), result.isDone());
        var resultDiscordMemberMap = result.getDiscordMembers().stream().collect(Collectors.toMap(PojoDiscordMember::getDiscordId, Function.identity()));
        if(base.getDiscordMembers() != null) {
            for(var member : base.getDiscordMembers()) {
                DiscordMemberUtils.compare(member, resultDiscordMemberMap.get(member.getDiscordId()));
            }
        }
    }

}
