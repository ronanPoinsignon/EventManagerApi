package app.back.repository;

import app.back.dto.AbstractEntity;
import org.springframework.data.repository.CrudRepository;

public interface AbstractEntityRepository<T extends AbstractEntity<T>> extends CrudRepository<T, Long> {

}
