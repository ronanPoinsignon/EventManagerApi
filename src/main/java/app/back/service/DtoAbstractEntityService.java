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

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void delete(T id) {
        repository.delete(id);
    }

    public Optional<T> findAndDelete(Long id) {
        var resultOptional = repository.findById(id);
        resultOptional.ifPresent(this::delete);
        return resultOptional;
    }

}
