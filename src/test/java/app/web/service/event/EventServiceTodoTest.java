package app.web.service.event;

import app.utils.DiscordMemberUtils;
import app.utils.EventUtils;
import app.utils.TodoEntryUtils;
import app.web.exception.BadRequestException;
import app.web.exception.NotFoundException;
import app.web.service.DiscordMemberService;
import app.web.service.EventService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class EventServiceTodoTest {

    private final EventService service;
    private final DiscordMemberService discordMemberService;
    private final EventUtils eventUtils;
    private final DiscordMemberUtils discordMemberUtils;
    private final TodoEntryUtils todoEntryUtils;

    protected EventServiceTodoTest(@Autowired EventService service,
                                   @Autowired DiscordMemberService discordMemberService,
                                   @Autowired EventUtils eventUtils,
                                   @Autowired DiscordMemberUtils discordMemberUtils,
                                   @Autowired TodoEntryUtils todoEntryUtils) {
        this.service = service;
        this.discordMemberService = discordMemberService;
        this.eventUtils = eventUtils;
        this.discordMemberUtils = discordMemberUtils;
        this.todoEntryUtils = todoEntryUtils;
    }

    @Test
    @Order(1)
    void testAddTodo() {
        var event = eventUtils.createBasicPojo();
        event = service.save(event);
        var todo = todoEntryUtils.createFullLightTodoEntry();
        event = service.addTodo(event.getId(), todo);

        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals(event, event.getTodoList().getFirst().getEvent());
        Assertions.assertEquals(todo.getName(), event.getTodoList().getFirst().getName());
        Assertions.assertEquals(todo.getTodo(), event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());
    }

    @Test
    @Order(2)
    void testAddTodoNull() {
        var event = eventUtils.createBasicPojo();
        event = service.save(event);
        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(BadRequestException.class, () -> service.addTodo(finalEvent.getId(), null));
        Assertions.assertTrue(event.getTodoList().isEmpty());
    }

    @Test
    @Order(3)
    void testAddTodoEventNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.addTodo(2L, todoEntryUtils.createBasicLightTodoEntry()));
    }

    @Test
    @Order(4)
    void testAddTodoWithMemberNotFound() {
        var event = eventUtils.createBasicPojo();
        event = service.save(event);
        var todo = todoEntryUtils.createBasicLightTodoEntry();
        todo.getParticipants().add(2L);

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(NotFoundException.class, () -> service.addTodo(finalEvent.getId(), todo));

        Assertions.assertTrue(event.getTodoList().isEmpty());
    }

    @Test
    @Order(5)
    void testRemoveTodo() {
        var event = eventUtils.createBasicPojo();
        eventUtils.addTodo(event);
        event = service.save(event);

        Assertions.assertEquals(1, event.getTodoList().size());
        event = service.removeTodo(event.getId(), event.getTodoList().getFirst().getName());
        Assertions.assertTrue(event.getTodoList().isEmpty());
    }

    @Test
    @Order(6)
    void testRemoveTodoNull() {
        var event = eventUtils.createBasicPojo();
        eventUtils.addTodo(event);
        event = service.save(event);

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(BadRequestException.class, () -> service.removeTodo(finalEvent.getId(), null));
        Assertions.assertThrows(BadRequestException.class, () -> service.removeTodo(finalEvent.getId(), ""));
    }

    @Test
    @Order(7)
    void testRemoveTodoEventNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.removeTodo(2L, "test"));
    }

    @Test
    @Order(8)
    void testRemoveTodoNotFound() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        event = service.save(event);

        app.web.pojo.PojoEvent finalEvent = event;
        event = service.removeTodo(finalEvent.getId(), "test");
        Assertions.assertEquals(todo.getName(), event.getTodoList().getFirst().getName());
    }

    @Test
    @Order(9)
    void testAddMember() {
        var event = eventUtils.createBasicPojo();
        eventUtils.addTodo(event);
        event = service.save(event);
        var member = discordMemberService.save(discordMemberUtils.createBasicPojo());
        Assertions.assertTrue(event.getTodoList().getFirst().getDiscordMembers().isEmpty());

        event = service.addTodoMembers(event.getId(), event.getTodoList().getFirst().getName(), List.of(member.getId()));
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());
    }

    @Test
    @Order(10)
    void testAddMemberNotFound() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addDiscordMember(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(NotFoundException.class, () -> service.addTodoMembers(finalEvent.getId(), finalEvent.getTodoList().getFirst().getName(), List.of(2L)));
        event = service.findOne(event.getId());
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());
    }

    @Test
    @Order(11)
    void testAddMemberNull() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addDiscordMember(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());

        event = service.addTodoMembers(event.getId(), event.getTodoList().getFirst().getName(), null);
        event = service.findOne(event.getId());
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());
    }

    @Test
    @Order(12)
    void testAddMemberEventNull() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addDiscordMember(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(BadRequestException.class, () -> service.addTodoMembers(finalEvent.getId(), null, List.of(finalEvent.getTodoList().getFirst().getDiscordMembers().getFirst().getId())));
        Assertions.assertThrows(BadRequestException.class, () -> service.addTodoMembers(finalEvent.getId(), "", List.of(finalEvent.getTodoList().getFirst().getDiscordMembers().getFirst().getId())));
    }

    @Test
    @Order(13)
    void testAddMemberTodoNull() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addDiscordMember(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(BadRequestException.class, () -> service.addTodoMembers(finalEvent.getId(), null, List.of(finalEvent.getTodoList().getFirst().getDiscordMembers().getFirst().getId())));
        Assertions.assertThrows(BadRequestException.class, () -> service.addTodoMembers(finalEvent.getId(), "", List.of(finalEvent.getTodoList().getFirst().getDiscordMembers().getFirst().getId())));
    }

    @Test
    @Order(14)
    void testAddMemberTodoNotFound() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addDiscordMember(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(NotFoundException.class, () -> service.addTodoMembers(finalEvent.getId(), "test", List.of(finalEvent.getTodoList().getFirst().getDiscordMembers().getFirst().getId())));
    }

    @Test
    @Order(15)
    void testRemoveMember() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addDiscordMember(todo);
        todoEntryUtils.addDiscordMember(todo);
        event = service.save(event);
        Assertions.assertEquals(2, event.getTodoList().getFirst().getDiscordMembers().size());
        var member1 = event.getTodoList().getFirst().getDiscordMembers().getFirst();
        var member2 = event.getTodoList().getFirst().getDiscordMembers().get(1);

        event = service.removeTodoMembers(event.getId(), event.getTodoList().getFirst().getName(), List.of(member1.getId()));
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());
        Assertions.assertEquals(member2.getId(), event.getTodoList().getFirst().getDiscordMembers().getFirst().getId());
    }

    @Test
    @Order(16)
    void testRemoveMemberNotFound() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
         todoEntryUtils.addDiscordMember(todo);
        todoEntryUtils.addDiscordMember(todo);
        event = service.save(event);
        Assertions.assertEquals(2, event.getTodoList().getFirst().getDiscordMembers().size());

        service.removeTodoMembers(event.getId(), event.getTodoList().getFirst().getName(), List.of(2L));
        event = service.findOne(event.getId());
        Assertions.assertEquals(2, event.getTodoList().getFirst().getDiscordMembers().size());
    }

    @Test
    @Order(17)
    void testRemoveMemberNull() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addDiscordMember(todo);
        todoEntryUtils.addDiscordMember(todo);
        event = service.save(event);
        Assertions.assertEquals(2, event.getTodoList().getFirst().getDiscordMembers().size());

        event = service.addTodoMembers(event.getId(), event.getTodoList().getFirst().getName(), null);
        event = service.findOne(event.getId());
        Assertions.assertEquals(2, event.getTodoList().getFirst().getDiscordMembers().size());
    }

    @Test
    @Order(18)
    void testRemoveMemberTodoNull() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addDiscordMember(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(BadRequestException.class, () -> service.removeTodoMembers(finalEvent.getId(), null, List.of(finalEvent.getTodoList().getFirst().getDiscordMembers().getFirst().getId())));
        Assertions.assertThrows(BadRequestException.class, () -> service.removeTodoMembers(finalEvent.getId(), "", List.of(finalEvent.getTodoList().getFirst().getDiscordMembers().getFirst().getId())));
    }

    @Test
    @Order(19)
    void testRemoveMemberTodoNotFound() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addDiscordMember(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(NotFoundException.class, () -> service.removeTodoMembers(finalEvent.getId(), "test", List.of(finalEvent.getTodoList().getFirst().getDiscordMembers().getFirst().getId())));
    }

}
