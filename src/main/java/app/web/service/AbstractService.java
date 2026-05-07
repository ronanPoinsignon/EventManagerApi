package app.web.service;

import app.back.api.AbstractDtoServiceApi;
import app.back.dto.AbstractEntity;
import app.web.api.AbstractServiceApi;
import app.web.exception.BadRequestException;
import app.web.exception.NotFoundException;
import app.web.pojo.PojoEntity;
import app.web.transform.Transform;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractService<T extends AbstractEntity, U extends PojoEntity, V extends AbstractDtoServiceApi<T>> implements AbstractServiceApi<U> {

    private final V service;
    private final Transform<T, U> transform;

    public AbstractService(V service, Transform<T, U> transform) {
        this.service = service;
        this.transform = transform;
    }

    @Transactional
    @Override
    public U findOne(Long id) {
        if(id == null) {
            throw new BadRequestException("L'id ne peut être null.");
        }

        var result = service.findById(id).orElseThrow(() -> new NotFoundException("Aucun élément trouvé pour l'id " + id + "."));
        return transform.toPojo(result);
    }

    @Transactional
    @Override
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
