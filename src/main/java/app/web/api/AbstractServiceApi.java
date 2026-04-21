package app.web.api;

import app.web.pojo.PojoEntity;

public interface AbstractServiceApi<T extends PojoEntity> {

    T findOne(Long id);
    T save(T pojo);

}
