package app.web.service;

import app.back.dto.AbstractEntity;
import app.back.repository.AbstractEntityRepository;
import app.back.service.DtoAbstractEntityService;
import app.web.pojo.PojoEntity;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Transactional
public abstract class BasicTestService<T extends AbstractEntity, P extends PojoEntity, S extends AbstractService<T, P, ? extends DtoAbstractEntityService<T, ? extends AbstractEntityRepository<T>>>> {

    protected final S service;

    protected BasicTestService(S service) {
        this.service = service;
    }

    protected abstract P createBasicPojo();

}
