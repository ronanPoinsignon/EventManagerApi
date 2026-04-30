package app.back.api;

import app.back.dto.AbstractEntity;

import java.util.Optional;

public interface AbstractDtoServiceApi<T extends AbstractEntity> {

    T save(T entity);
    Optional<T> findById(Long id);
    void delete(Long id);
    void delete(T entity);
    Optional<T> findAndDelete(Long id);

}
