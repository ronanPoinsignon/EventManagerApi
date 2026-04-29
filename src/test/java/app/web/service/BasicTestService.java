package app.web.service;

import app.web.api.AbstractServiceApi;
import app.web.pojo.PojoEntity;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public abstract class BasicTestService<P extends PojoEntity, S extends AbstractServiceApi<P>> {

    protected final S service;

    protected BasicTestService(S service) {
        this.service = service;
    }

    protected abstract P createBasicPojo();

}
