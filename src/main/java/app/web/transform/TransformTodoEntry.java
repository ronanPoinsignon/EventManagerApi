package app.web.transform;

import app.back.dto.TodoEntry;
import app.back.service.KeycloakUserService;
import app.web.pojo.PojoTodoEntry;
import app.web.pojo.PojoUser;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransformTodoEntry extends AbstractTransform<TodoEntry, PojoTodoEntry> {

    @Autowired
    @Lazy
    private TransformEvent transformEvent;

    @Autowired
    @Lazy
    private KeycloakUserService keycloakUserService;

    @Autowired
    @Lazy
    private TransformKeycloakUser transformKeycloakUser;

    @Override
    protected PojoTodoEntry from(@Nonnull TodoEntry dto) {
        var pojo = super.from(dto);
        pojo.setName(dto.getTodoName());
        pojo.setTodoValue(dto.getTodoValue());
        pojo.setEvent(transformEvent.toPojo(dto.getEvent()));
        pojo.setParticipants(dto.getuserIds().stream()
                .map(keycloakUserService::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(transformKeycloakUser::toPojoWithAttributes)
                .toList());
        pojo.setDone(dto.isDone());

        return pojo;
    }

    @Override
    protected TodoEntry from(@Nonnull PojoTodoEntry pojo) {
        var entity = super.from(pojo);
        entity.setTodoName(pojo.getName());
        entity.setTodoValue(pojo.getTodoValue());
        entity.setEvent(transformEvent.toDto(pojo.getEvent()));
        if(pojo.getParticipants() != null) {
            entity.setUserIdSet(pojo.getParticipants().stream().map(PojoUser::getId).toList());
        }

        return entity;
    }

    @Override
    protected TodoEntry createDto() {
        return new TodoEntry();
    }

    @Override
    protected PojoTodoEntry createPojo() {
        return new PojoTodoEntry();
    }

}
