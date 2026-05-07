package app.utils;

import app.back.dto.TodoEntry;
import app.back.service.DtoUserAttributesService;
import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoTodoEntry;
import app.web.transform.TransformMember;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;
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
    private DtoUserAttributesService discordMemberService;
    @Autowired
    @Lazy
    private UuidUtils uuidUtils;
    @Autowired
    @Lazy
    private TransformMember transformMember;

    @Autowired
    @Lazy
    private UserAttributesUtils discordMemberUtils;
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

    public UUID addUser(TodoEntry todoEntry) {
        var user = uuidUtils.generate();
        todoEntry.addUserId(user);
        return user;
    }

    public UUID addUser(LightPojoTodoEntry todoEntry) {
        var user = uuidUtils.generate();
        todoEntry.getParticipants().add(user);
        return user;
    }

    public UUID addUser(PojoTodoEntry todoEntry) {
        var user = uuidUtils.generate();
        if(todoEntry.getUserIds() == null) {
            todoEntry.setUserIds(new ArrayList<>());
        }
        todoEntry.getUserIds().add(user);

        return user;
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

        addUser(todo);

        return todo;
    }

    public PojoTodoEntry createBasicTodoEntry() {
        var todo = new PojoTodoEntry();
        todo.setTodoValue("todo_" + counterStrategy.get());
        todo.setName("name_" + counterStrategy.get());
        todo.setUserIds(new ArrayList<>());

        return todo;
    }

    public PojoTodoEntry createFullTodoEntry() {
        var todo = new PojoTodoEntry();
        todo.setTodoValue("todo_" + counterStrategy.get());
        todo.setName("name_" + counterStrategy.get());

        addUser(todo);

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
        var baseDiscordMemberList = new ArrayList<>(base.getuserIds());
        var resultDiscordMemberMap = result.getUserIds().stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
        for(int i = 0; i < base.getuserIds().size(); i++) {
            var userId = baseDiscordMemberList.get(i);
            Assertions.assertEquals(baseDiscordMemberList.get(i), resultDiscordMemberMap.get(userId));
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
        var resultDiscordMemberMap = result.getuserIds().stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
        for(var userId : base.getuserIds()) {
            Assertions.assertEquals(userId, resultDiscordMemberMap.get(userId));
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
        var resultDiscordMemberMap = result.getuserIds().stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
        if(base.getUserIds() != null) {
            for(var userid : base.getUserIds()) {
                Assertions.assertEquals(userid, resultDiscordMemberMap.get(userid));
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
        var resultDiscordMemberMap = result.getUserIds().stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
        if(base.getUserIds() != null) {
            for(var userid : base.getUserIds()) {
                Assertions.assertEquals(userid, resultDiscordMemberMap.get(userid));
            }
        }
    }

}
