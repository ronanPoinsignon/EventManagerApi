package app.back.repository;

import app.back.dto.AbstractEntity;
import org.springframework.data.repository.CrudRepository;

public interface AbstractEntityRepository<T extends AbstractEntity> extends CrudRepository<T, Long> {

}
