package app.back.service.event;

import app.back.dto.DiscordMember;
import app.back.dto.Event;
import app.back.dto.TodoEntry;
import app.back.exception.BackBadRequestException;
import app.back.exception.duplicate.todo.BackDuplicateTodoNameException;
import app.back.service.DtoEventService;
import app.utils.EventUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class DtoEventServiceTodoTest {

    private final EventUtils eventUtils;
    private final DtoEventService dtoService;

    public DtoEventServiceTodoTest(@Autowired EventUtils eventUtils, @Autowired DtoEventService dtoService) {
        this.eventUtils = eventUtils;
        this.dtoService = dtoService;
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
        Assertions.assertTrue(event.getTodoList().getFirst().getDiscordMembers().isEmpty());
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
    void testAddTodoWithMember() {
        var event = new Event();
        event.addTodo("test", "todo", new DiscordMember());
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("test", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(1, event.getTodoList().getFirst().getDiscordMembers().size());
    }

    @Test
    @Order(6)
    void testAddTodoWithMemberNull() {
        var event = new Event();
        Assertions.assertThrows(BackBadRequestException.class, () -> event.addTodo("test", "todo", (DiscordMember) null));
        Assertions.assertEquals(0, event.getTodoList().size());
    }

    @Test
    @Order(7)
    void testAddTodoWithMemberList() {
        var event = new Event();
        event.addTodo("test", "todo", List.of(new DiscordMember(), new DiscordMember()));
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("test", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(2, event.getTodoList().getFirst().getDiscordMembers().size());
    }

    @Test
    @Order(8)
    void testAddTodoWithMemberListNull() {
        var event = new Event();
        event.addTodo("test", "todo", (List<DiscordMember>) null);
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("test", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertTrue(event.getTodoList().getFirst().getDiscordMembers().isEmpty());
    }

    @Test
    @Order(9)
    void testAddExistedTodoWithMemberList() {
        var event = new Event();
        event.addTodo("test", "todo", List.of(new DiscordMember(), new DiscordMember()));
        event.addTodo("test", "todo2", List.of(new DiscordMember(), new DiscordMember()));
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("test", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo2", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(4, event.getTodoList().getFirst().getDiscordMembers().size());
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
                new TodoEntry("name2", "todo1", new DiscordMember()),
                new TodoEntry("name2", "todo2"),
                new TodoEntry("name2", "todo3", List.of(new DiscordMember(), new DiscordMember())),
                new TodoEntry("name2", "todo4", (List<DiscordMember>) null)
        ));
        Assertions.assertEquals(1, event.getTodoList().size());
        Assertions.assertEquals("name2", event.getTodoList().getFirst().getTodoName());
        Assertions.assertEquals("todo4", event.getTodoList().getFirst().getTodoValue());
        Assertions.assertEquals(3, event.getTodoList().getFirst().getDiscordMembers().size());
    }

    @Test
    @Order(13)
    void testRemoveMember() {
        var event = new Event();
        Assertions.assertTrue(event.getTodoList().isEmpty());
        var member1 = new DiscordMember();
        member1.setId(1L);
        var member2 = new DiscordMember();
        member2.setId(2L);
        var member3 = new DiscordMember();
        member3.setId(3L);
        event.addTodo("test", "todo", List.of(member1, member2, member3));

        event.getTodoList().getFirst().removeDiscordMember(member2.getId());
        Assertions.assertEquals(2, event.getTodoList().getFirst().getDiscordMembers().size());
        var result = List.of(member1, member3).containsAll(event.getTodoList().getFirst().getDiscordMembers());
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
    void testRemoveMemberListNull() {
        var event = eventUtils.createBasicEntity();
        var todo = eventUtils.addTodo(event);
        var member1 = new DiscordMember();
        member1.setId(1L);
        var member2 = new DiscordMember();
        member2.setId(2L);
        todo.addDiscordMembers(List.of(member1, member2));
        todo.removeDiscordMember(null);
        Assertions.assertEquals(2, todo.getDiscordMembers().size());
    }

    @Test
    @Order(16)
    void testRemoveMemberListNullValue() {
        var event = eventUtils.createBasicEntity();
        var todo = eventUtils.addTodo(event);
        var member1 = new DiscordMember();
        member1.setId(1L);
        var member2 = new DiscordMember();
        member2.setId(2L);
        todo.addDiscordMembers(List.of(member1, member2));
        todo.removeDiscordMember(Collections.singletonList(null));
        Assertions.assertEquals(2, todo.getDiscordMembers().size());
    }

}
