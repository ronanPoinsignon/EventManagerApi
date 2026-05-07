package app.web.service.event;

import app.back.dto.TodoEntry;
import app.utils.EventUtils;
import app.utils.TodoEntryUtils;
import app.utils.UuidUtils;
import app.web.exception.BadRequestException;
import app.web.exception.NotFoundException;
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
    private final EventUtils eventUtils;
    private final TodoEntryUtils todoEntryUtils;
    private final UuidUtils uuidUtils;

    protected EventServiceTodoTest(@Autowired EventService service,
                                   @Autowired EventUtils eventUtils,
                                   @Autowired TodoEntryUtils todoEntryUtils,
                                   @Autowired UuidUtils uuidUtils) {
        this.service = service;
        this.eventUtils = eventUtils;
        this.todoEntryUtils = todoEntryUtils;
        this.uuidUtils = uuidUtils;
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
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());
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
    void testAddTodoWithUserNotFound() {
        var event = eventUtils.createBasicPojo();
        event = service.save(event);
        var todo = todoEntryUtils.createBasicLightTodoEntry();
        todo.getParticipants().add(uuidUtils.generate());

        app.web.pojo.PojoEvent finalEvent = event;
        event = service.addTodo(finalEvent.getId(), todo);

        Assertions.assertEquals(1, event.getTodoList().size());
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
    void testAdduser() {
        var event = eventUtils.createBasicPojo();
        eventUtils.addTodo(event);
        event = service.save(event);
        var user = uuidUtils.generate();
        Assertions.assertTrue(event.getTodoList().getFirst().getUserIds().isEmpty());

        event = service.addTodoUsers(event.getId(), event.getTodoList().getFirst().getName(), List.of(user));
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());
    }

    @Test
    @Order(10)
    void testAdduserNotFound() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addUser(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());

        app.web.pojo.PojoEvent finalEvent = event;
        service.addTodoUsers(finalEvent.getId(), finalEvent.getTodoList().getFirst().getName(), List.of(uuidUtils.generate()));
        event = service.findOne(event.getId());
        Assertions.assertEquals(2, event.getTodoList().getFirst().getUserIds().size());
    }

    @Test
    @Order(11)
    void testAdduserNull() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addUser(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());

        event = service.addTodoUsers(event.getId(), event.getTodoList().getFirst().getName(), null);
        event = service.findOne(event.getId());
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());
    }

    @Test
    @Order(12)
    void testAdduserEventNull() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addUser(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(BadRequestException.class, () -> service.addTodoUsers(finalEvent.getId(), null, List.of(finalEvent.getTodoList().getFirst().getUserIds().getFirst())));
        Assertions.assertThrows(BadRequestException.class, () -> service.addTodoUsers(finalEvent.getId(), "", List.of(finalEvent.getTodoList().getFirst().getUserIds().getFirst())));
    }

    @Test
    @Order(13)
    void testAdduserTodoNull() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addUser(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(BadRequestException.class, () -> service.addTodoUsers(finalEvent.getId(), null, List.of(finalEvent.getTodoList().getFirst().getUserIds().getFirst())));
        Assertions.assertThrows(BadRequestException.class, () -> service.addTodoUsers(finalEvent.getId(), "", List.of(finalEvent.getTodoList().getFirst().getUserIds().getFirst())));
    }

    @Test
    @Order(14)
    void testAdduserTodoNotFound() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addUser(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(NotFoundException.class, () -> service.addTodoUsers(finalEvent.getId(), "test", List.of(finalEvent.getTodoList().getFirst().getUserIds().getFirst())));
    }

    @Test
    @Order(15)
    void testRemoveuser() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addUser(todo);
        todoEntryUtils.addUser(todo);
        event = service.save(event);
        Assertions.assertEquals(2, event.getTodoList().getFirst().getUserIds().size());
        var user1 = event.getTodoList().getFirst().getUserIds().getFirst();
        var user2 = event.getTodoList().getFirst().getUserIds().get(1);

        event = service.removeTodoUsers(event.getId(), event.getTodoList().getFirst().getName(), List.of(user1));
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());
        Assertions.assertEquals(user2, event.getTodoList().getFirst().getUserIds().getFirst());
    }

    @Test
    @Order(16)
    void testRemoveuserNotFound() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
         todoEntryUtils.addUser(todo);
        todoEntryUtils.addUser(todo);
        event = service.save(event);
        Assertions.assertEquals(2, event.getTodoList().getFirst().getUserIds().size());

        service.removeTodoUsers(event.getId(), event.getTodoList().getFirst().getName(), List.of(uuidUtils.generate()));
        event = service.findOne(event.getId());
        Assertions.assertEquals(2, event.getTodoList().getFirst().getUserIds().size());
    }

    @Test
    @Order(17)
    void testRemoveuserNull() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addUser(todo);
        todoEntryUtils.addUser(todo);
        event = service.save(event);
        Assertions.assertEquals(2, event.getTodoList().getFirst().getUserIds().size());

        event = service.addTodoUsers(event.getId(), event.getTodoList().getFirst().getName(), null);
        event = service.findOne(event.getId());
        Assertions.assertEquals(2, event.getTodoList().getFirst().getUserIds().size());
    }

    @Test
    @Order(18)
    void testRemoveuserTodoNull() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addUser(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(BadRequestException.class, () -> service.removeTodoUsers(finalEvent.getId(), null, List.of(finalEvent.getTodoList().getFirst().getUserIds().getFirst())));
        Assertions.assertThrows(BadRequestException.class, () -> service.removeTodoUsers(finalEvent.getId(), "", List.of(finalEvent.getTodoList().getFirst().getUserIds().getFirst())));
    }

    @Test
    @Order(19)
    void testRemoveuserTodoNotFound() {
        var event = eventUtils.createBasicPojo();
        var todo = eventUtils.addTodo(event);
        todoEntryUtils.addUser(todo);
        event = service.save(event);
        Assertions.assertEquals(1, event.getTodoList().getFirst().getUserIds().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(NotFoundException.class, () -> service.removeTodoUsers(finalEvent.getId(), "test", List.of(finalEvent.getTodoList().getFirst().getUserIds().getFirst())));
    }

    @Test
    @Order(20)
    void testTodoStatus() {
        var todo = new TodoEntry();
        Assertions.assertFalse(todo.isDone());

        var result = todo.setDone(true);
        Assertions.assertTrue(todo.isDone());
        Assertions.assertTrue(result);

        result = todo.setDone(true);
        Assertions.assertTrue(todo.isDone());
        Assertions.assertFalse(result);
    }

    @Test
    @Order(21)
    void testUpdateTodoStatus() {
        var event = eventUtils.createBasicPojo();
        eventUtils.addTodo(event);
        event = service.save(event);

        Assertions.assertFalse(event.getTodoList().getFirst().isDone());
        event = service.updateTodoStatus(event.getId(), event.getTodoList().getFirst().getName(), true);
        Assertions.assertTrue(event.getTodoList().getFirst().isDone());
    }
}
