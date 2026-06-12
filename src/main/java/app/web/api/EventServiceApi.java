package app.web.api;

import app.web.pojo.LightPojoTodoEntry;
import app.web.pojo.PojoEvent;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventServiceApi extends AbstractServiceApi<PojoEvent> {

    PojoEvent discordSave(PojoEvent event, String parentEventName);

    PojoEvent findByEventName(String name);

    PojoEvent findByEventName(String parentName, String name);

    List<PojoEvent> findAllBeforeEnd(Instant date);

    List<PojoEvent> findAll();

    PojoEvent getLast();

    PojoEvent addSubEvent(long parentEventId, PojoEvent event);

    PojoEvent removeSubEvent(long parentEventId, String subEventName);

    PojoEvent addTo(long eventId, List<UUID> userIds);

    PojoEvent removeTo(long eventId, List<UUID> userIdList);

    PojoEvent addTodo(long eventId, LightPojoTodoEntry lightPojoTodoEntry, List<UUID> userIds, boolean isDone);

    PojoEvent addDiscordTo(long eventId, List<Long> userIds);

    PojoEvent removeDiscordTo(long eventId, List<Long> userIds);

    PojoEvent removeTodo(long eventId, String name);

    PojoEvent addTodoUsers(long eventId, String todoName, List<UUID> userIds);

    PojoEvent removeTodoUsers(long eventId, String todoName, List<UUID> userIds);

    PojoEvent updateTodoStatus(long eventId, String todoName, boolean isDone);

    PojoEvent delete(long eventId);

    PojoEvent setParticipant(long eventId, List<UUID> userIds);

    PojoEvent findEventFromTodoId(long todoId);

    InputStreamResource uploadEventImageFile(long eventId, MultipartFile eventFile);

    InputStreamResource downloadEventImageFile(long eventId);
}
