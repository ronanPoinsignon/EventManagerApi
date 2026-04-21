package app.web.service;

import app.back.dto.AbstractEntity;
import app.back.repository.AbstractEntityRepository;
import app.back.service.DtoAbstractEntityService;
import app.web.api.AbstractServiceApi;
import app.web.pojo.PojoEntity;
import app.web.transform.Transform;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class AbstractService<T extends AbstractEntity<T>, U extends PojoEntity, R extends AbstractEntityRepository<T>, V extends DtoAbstractEntityService<T, R>> implements AbstractServiceApi<U> {

    private final V service;
    private final Transform<T, U> transform;

    public AbstractService(V service, Transform<T, U> transform) {
        this.service = service;
        this.transform = transform;
    }

    public U findOne(Long id) {
        var result = service.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun élément trouvé pour l'id " + id + "."));
        return transform.toPojo(result);
    }

    public U save(U pojo) {
        var dto = transform.toDto(pojo);
        if(dto == null) {
            return null;
        }

        return transform.toPojo(service.save(dto));
    }

    protected V getService() {
        return service;
    }

    protected Transform<T, U> getTransform() {
        return transform;
    }
}
