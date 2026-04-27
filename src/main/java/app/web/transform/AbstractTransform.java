package app.web.transform;

import app.back.dto.Identifiable;
import app.web.pojo.PojoEntity;
import jakarta.annotation.Nonnull;

public abstract class AbstractTransform<T extends Identifiable, U extends PojoEntity> implements Transform<T, U> {

    public T toDto(U pojo) {
        if(pojo == null) {
            return null;
        }

        return from(pojo);
    }

    public U toPojo(T dto) {
        if(dto == null) {
            return null;
        }

        return from(dto);
    }

    protected T from(@Nonnull U pojo) {
        var dto = createDto();
        if(pojo.getId() != null) {
            dto.setId(pojo.getId());
        }

        return dto;
    }

    protected U from(@Nonnull T dto) {
        var pojo = createPojo();
        if(dto.getId() != null) {
            pojo.setId(dto.getId());
        }

        return pojo;
    }

    protected abstract T createDto();
    protected abstract U createPojo();

}
