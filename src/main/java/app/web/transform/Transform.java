package app.web.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Transform<T, U> {

    T toDto(U pojo);
    default List<T> toDto(Collection<U> pojos) {
        if(pojos == null) {
            return new ArrayList<>();
        }

        return pojos.stream().map(this::toDto).toList();
    }

    U toPojo(T dto);
    default List<U> toPojo(Collection<T> dtos) {
        if(dtos == null) {
            return new ArrayList<>();
        }

        return dtos.stream().map(this::toPojo).toList();
    }

}
