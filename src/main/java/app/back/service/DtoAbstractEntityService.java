package app.back.service;

import app.back.dto.AbstractEntity;
import app.back.entityname.ContrainteUtiles;
import app.back.exception.BackNotFoundException;
import app.back.repository.AbstractEntityRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public abstract class DtoAbstractEntityService<T extends AbstractEntity, U extends AbstractEntityRepository<T>> {

    @Autowired
    private EntityManager em;

    private final Logger logger = LoggerFactory.getLogger(DtoAbstractEntityService.class);

    protected final U repository;

    protected DtoAbstractEntityService(U repository) {
        this.repository = repository;
    }

    public T save(T entity) {
        // si l'objet est rattaché à spring, pas besoin de le find
        if(entity.getId() == null || em.contains(entity)) {
            return saveInternal(entity);
        }

        var dbEntity = this.findById(entity.getId()).orElseThrow(() -> new BackNotFoundException("Aucun élément trouvé."));
        update(entity, dbEntity);
        return saveInternal(entity);
    }

    private T saveInternal(T entity) {
        T result;
        try {
            result = repository.save(entity);
            em.flush();
        } catch(ConstraintViolationException e) {
            manageConstraintViolation(e);
            throw e;
        }
        return result;
    }

    private void manageConstraintViolation(ConstraintViolationException constraintException) {
        var contraintName = constraintException.getConstraintName();
        if(contraintName == null || contraintName.isBlank()) {
            throw constraintException;
        }

        var split = contraintName.split("\\.");
        if(split.length != 2) {
            logger.error("La clé de la contrainte ne correspond pas au format voulu : {}", contraintName);
            throw constraintException;
        }

        var table = split[0];
        var name = split[1];
        var exceptionContraintMap = ContrainteUtiles.CONTRAINTE_EXCEPTION_MAP.get(table);
        if(exceptionContraintMap == null) {
            logger.error("La map de contraintes n'a pas été trouvée pour la table {}", table);
            throw constraintException;
        }

        var exceptionSupplier = exceptionContraintMap.get(name);
        if(exceptionSupplier == null) {
            logger.error("La classe d'exception n'a pas été trouvée pour le nom {}", name);
            throw constraintException;
        }

        throw exceptionSupplier.get();
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

    public Optional<T> findAndDelete(long id) {
        var resultOptional = repository.findById(id);
        resultOptional.ifPresent(this::delete);
        return resultOptional;
    }

}
