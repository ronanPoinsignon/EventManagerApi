package app.back.service.event;

import app.back.dto.Event;
import app.back.dto.TodoEntry;
import app.back.exception.BackBadRequestException;
import app.back.exception.duplicate.todo.BackDuplicateTodoNameException;
import app.back.service.DtoEventService;
import app.utils.EventUtils;
import app.utils.UuidUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class DtoEventServiceTodoTest {

    private final EventUtils eventUtils;
    private final DtoEventService dtoService;
    private final UuidUtils uuidUtils;

    public DtoEventServiceTodoTest(@Autowired EventUtils eventUtils, @Autowired DtoEventService dtoService, @Autowired UuidUtils uuidUtils) {
        this.eventUtils = eventUtils;
        this.dtoService = dtoService;
        this.uuidUtils = uuidUtils;
    }


    @Test
    @Order(1)
    void testAddTodo() {
        var event = new Event();
        Assertions.assertTrue(event.getTodoList().isEmpty());
        event.addTodo("test", "todo");
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("test", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertTrue(event.getTodoList().getFirst().getuserIds().isEmpty());
    }

    @Test
    @Order(2)
    void testAddTodoNullName() {
        var event = new Event();
        Assertions.assertThrows(BackBadRequestException.class, () -> event.addTodo(null, "todo"));
        Assertions.assertThrows(BackBadRequestException.class, () -> event.addTodo("", "todo"));
    }

    @Test
    @Order(3)
    void testAddTodoNullTodo() {
        var event = new Event();
        Assertions.assertThrows(BackBadRequestException.class, () -> event.addTodo("name", null));
        Assertions.assertThrows(BackBadRequestException.class, () -> event.addTodo("name", ""));
    }

    @Test
    @Order(4)
    void testAddTodoTwice() {
        var event = new Event();
        event.addTodo("test", "todo");
        Assertions.assertEquals(1, event.getTodoList().size());
        event.addTodo("test", "todo2");
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("todo2", event.getTodoList().getFirst().getTodoValue());
    }

    @Test
    @Order(5)
    void testAddTodoWithUser() {
        var event = new Event();
        event.addTodo("test", "todo", uuidUtils.generate());
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("test", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(1, event.getTodoList().getFirst().getuserIds().size());
    }

    @Test
    @Order(6)
    void testAddTodoWithUserNull() {
        var event = new Event();
        Assertions.assertThrows(BackBadRequestException.class, () -> event.addTodo("test", "todo", (UUID) null));
        Assertions.assertEquals(0, event.getTodoList().size());
    }

    @Test
    @Order(7)
    void testAddTodoWithUserList() {
        var event = new Event();
        event.addTodo("test", "todo", List.of(uuidUtils.generate(), uuidUtils.generate()));
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("test", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(2, event.getTodoList().getFirst().getuserIds().size());
    }

    @Test
    @Order(8)
    void testAddTodoWithUserListNull() {
        var event = new Event();
        event.addTodo("test", "todo", (List<UUID>) null);
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("test", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertTrue(event.getTodoList().getFirst().getuserIds().isEmpty());
    }

    @Test
    @Order(9)
    void testAddExistedTodoWithUserList() {
        var event = new Event();
        event.addTodo("test", "todo", List.of(uuidUtils.generate(), uuidUtils.generate()));
        event.addTodo("test", "todo2", List.of(uuidUtils.generate(), uuidUtils.generate()));
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("test", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo2", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(4, event.getTodoList().getFirst().getuserIds().size());
    }

    @Test
    @Order(10)
    void testRemoveTodo() {
        var event = new Event();
        event.addTodo("test", "todo");
        event.removeTodo("test");
        Assertions.assertTrue(event.getTodoList().isEmpty());
    }

    @Test
    @Order(11)
    void testRemoveTodoNotFound() {
        var event = new Event();
        event.addTodo("test", "todo");
        event.removeTodo("tests");
        Assertions.assertEquals(1, event.getTodoList().size());
    }

    @Test
    @Order(12)
    void setTodoList() {
        var event = new Event();
        event.addTodo("test", "todo");
        event.setTodoList(List.of(new TodoEntry("name2", "todo2"), new TodoEntry("name3", "todo2")));
        Assertions.assertEquals(2, event.getTodoList().size());
        Assertions.assertEquals("name2", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo2", event.getTodoList().getFirst().getTodoValue());
    }

    @Test
    @Order(13)
    void setTodoListEmpty() {
        var event = new Event();
        event.addTodo("test", "todo");
        event.setTodoList(List.of(new TodoEntry("name2", "todo2")));
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("name2", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo2", event.getTodoList().getFirst().getTodoValue());
    }

    @Test
    @Order(13)
    void setTodoListNull() {
        var event = new Event();
        event.addTodo("test", "todo");
        event.setTodoList(null);
        Assertions.assertTrue(event.getTodoList().isEmpty());
    }

    @Test
    @Order(12)
    void setTodoListMultipleSameTodo() {
        var event = new Event();
        event.addTodo("test", "todo");
        event.setTodoList(List.of(
                new TodoEntry("name2", "todo1", uuidUtils.generate()),
                new TodoEntry("name2", "todo2"),
                new TodoEntry("name2", "todo3", List.of(uuidUtils.generate(), uuidUtils.generate())),
                new TodoEntry("name2", "todo4", (List<UUID>) null)
        ));
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("name2", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo4", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(3, event.getTodoList().getFirst().getuserIds().size());
    }

    @Test
    @Order(13)
    void testRemoveUser() {
        var event = new Event();
        Assertions.assertTrue(event.getTodoList().isEmpty());
        var user1 = uuidUtils.generate();
        var user2 = uuidUtils.generate();
        var user3 = uuidUtils.generate();
        event.addTodo("test", "todo", List.of(user1, user2, user3));

        event.getTodoList().getFirst().removeUserId(user2);
        Assertions.assertEquals(2, event.getTodoList().getFirst().getuserIds().size());
        var result = List.of(user1, user3).containsAll(event.getTodoList().getFirst().getuserIds());
        Assertions.assertTrue(result);
    }

    @Test
    @Order(14)
    void testAddTodoNameConflict(@Autowired EntityManager entityManager) {
        var event = eventUtils.createBasicEntity();
        var todo1 = eventUtils.addTodo(event);
        event = dtoService.save(event);

        var todo2 = eventUtils.addTodo(event);
        todo2.setTodoName(todo1.getTodoName());

        Event finalParent = event;
        // obligé pour ne pas qu'hibernate flush les modifications de l'entité à son prochain find, et non à la sauvegarde
        entityManager.detach(finalParent);
        Assertions.assertThrows(BackDuplicateTodoNameException.class, () -> dtoService.save(finalParent));
    }

    @Test
    @Order(15)
    void testUpdateTodoNameConflict(@Autowired EntityManager entityManager) {
        var event = eventUtils.createBasicEntity();
        eventUtils.addTodo(event);
        event = dtoService.save(event);

        eventUtils.addTodo(event);
        event = dtoService.save(event);

        var todo1 = event.getTodoList().getFirst();
        var todo2 = event.getTodoList().get(1);
        todo2.setTodoName(todo1.getTodoName());

        Event finalParent = event;
        // obligé pour ne pas qu'hibernate flush les modifications de l'entité à son prochain find, et non à la sauvegarde
        entityManager.detach(finalParent);
        Assertions.assertThrows(BackDuplicateTodoNameException.class, () -> dtoService.save(finalParent));
    }

    @Test
    @Order(15)
    void testRemoveUserListNull() {
        var event = eventUtils.createBasicEntity();
        var todo = eventUtils.addTodo(event);
        var user1 = uuidUtils.generate();
        var user2 = uuidUtils.generate();
        todo.addUserIds(List.of(user1, user2));
        todo.removeUserId(null);
        Assertions.assertEquals(2, todo.getuserIds().size());
    }

    @Test
    @Order(16)
    void testRemoveUserListNullValue() {
        var event = eventUtils.createBasicEntity();
        var todo = eventUtils.addTodo(event);
        var user1 = uuidUtils.generate();
        var user2 = uuidUtils.generate();
        todo.addUserIds(List.of(user1, user2));
        todo.removeUserIds(Collections.singletonList(null));
        Assertions.assertEquals(2, todo.getuserIds().size());
    }

}
