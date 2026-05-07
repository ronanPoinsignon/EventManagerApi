package app.web.transform;

import app.back.dto.TodoEntry;
import app.web.pojo.PojoTodoEntry;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TransformTodoEntry extends AbstractTransform<TodoEntry, PojoTodoEntry> {

    @Autowired
    @Lazy
    private TransformEvent transformEvent;

    @Override
    protected PojoTodoEntry from(@Nonnull TodoEntry dto) {
        var pojo = super.from(dto);
        pojo.setName(dto.getTodoName());
        pojo.setTodoValue(dto.getTodoValue());
        pojo.setEvent(transformEvent.toPojo(dto.getEvent()));
        pojo.setUserIds(dto.getuserIds());
        pojo.setDone(dto.isDone());

        return pojo;
    }

    @Override
    protected TodoEntry from(@Nonnull PojoTodoEntry pojo) {
        var entity = super.from(pojo);
        entity.setTodoName(pojo.getName());
        entity.setTodoValue(pojo.getTodoValue());
        entity.setEvent(transformEvent.toDto(pojo.getEvent()));
        entity.setUserIdSet(pojo.getUserIds());

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
