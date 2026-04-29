package app.utils;

import app.back.dto.DiscordMember;
import app.back.dto.TodoEntry;
import app.back.service.DtoDiscordMemberService;
import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoDiscordMember;
import app.web.pojo.PojoTodoEntry;
import app.web.transform.TransformMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

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

}
