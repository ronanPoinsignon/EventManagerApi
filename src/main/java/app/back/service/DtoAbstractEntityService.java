package app.back.service;

import app.back.dto.AbstractEntity;
import app.back.repository.AbstractEntityRepository;

import java.util.Optional;

public abstract class DtoAbstractEntityService<T extends AbstractEntity<T>, U extends AbstractEntityRepository<T>> {

    protected final U repository;

    protected DtoAbstractEntityService(U repository) {
        this.repository = repository;
    }

    public T save(T entity) {
        T dbEntity;
        if(entity.getId() == null) {
            dbEntity = entity;
        } else {
            dbEntity = repository.findById(entity.getId()).orElse(entity);
        }

        entity.write(dbEntity);

        return repository.save(dbEntity);
    }

    public Optional<T> findById(long id) {
        return repository.findById(id);
    }

}
