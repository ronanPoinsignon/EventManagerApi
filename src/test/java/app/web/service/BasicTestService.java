package app.web.service;

import app.web.api.AbstractServiceApi;
import app.web.exception.BadRequestException;
import app.web.exception.NotFoundException;
import app.web.pojo.PojoEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
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

    @Test
    void testSave() {
        var entity = createBasicPojo();
        Assertions.assertNull(entity.getId());
        entity = service.save(entity);
        Assertions.assertNotNull(entity.getId());
    }

    @Test
    void testSaveNull() {
        var entity = service.save(null);
        Assertions.assertNull(entity);
    }

    @Test
    void testFindOne() {
        var entity = service.save(createBasicPojo());
        var entityId = entity.getId();
        entity = service.findOne(entityId);
        Assertions.assertEquals(entityId, entity.getId());
    }

    @Test
    void testFindOneNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findOne(0L));
    }

    @Test
    void testFindOneNull() {
        Assertions.assertThrows(BadRequestException.class, () -> service.findOne(null));
    }

}
