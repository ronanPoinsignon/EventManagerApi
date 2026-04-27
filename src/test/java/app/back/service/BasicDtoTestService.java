package app.back.service;

import app.back.dto.AbstractEntity;
import app.back.repository.AbstractEntityRepository;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Transactional
public abstract class BasicDtoTestService<T extends AbstractEntity, S extends DtoAbstractEntityService<T, ? extends AbstractEntityRepository<T>>> {

    protected final S dtoService;

    protected BasicDtoTestService(S dtoService) {
        this.dtoService = dtoService;
    }

    protected abstract T createBasicObject();

    @Test
    @Order(1)
    void testFindOk() {
        var entity = createBasicObject();
        var result = dtoService.save(entity);
        var resultFind = dtoService.findById(result.getId()).orElseThrow(() -> new RuntimeException("Aucun objet trouvé."));
        Assertions.assertEquals(result, resultFind);
    }

    @Test
    @Order(2)
    void testFindNok() {
        var result = dtoService.findById(3L);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(3)
    void testDelete() {
        var entity = createBasicObject();
        dtoService.save(entity);
        dtoService.delete(entity.getId());
        var result = dtoService.findById(entity.getId());
        Assertions.assertTrue(result.isEmpty());
    }
}
