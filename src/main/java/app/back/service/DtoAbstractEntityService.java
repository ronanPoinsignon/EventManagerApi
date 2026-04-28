package app.back.service;

import app.back.dto.AbstractEntity;
import app.back.exception.BackNotFoundException;
import app.back.repository.AbstractEntityRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public abstract class DtoAbstractEntityService<T extends AbstractEntity, U extends AbstractEntityRepository<T>> {

    @Autowired
    private EntityManager em;

    protected final U repository;

    protected DtoAbstractEntityService(U repository) {
        this.repository = repository;
    }

    public T save(T entity) {
        // si l'objet est rattaché à spring, pas besoin de le find
        if(entity.getId() == null || em.contains(entity)) {
            var result = repository.save(entity);
            em.flush();
            return result;
        }

        var dbEntity = this.findById(entity.getId()).orElseThrow(() -> new BackNotFoundException("Aucun élément trouvé."));
        update(entity, dbEntity);
        var result = repository.save(dbEntity);
        em.flush();
        return result;
    }

    protected abstract void update(T entityToSave, T dbEntity);

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
