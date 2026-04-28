package app.back.service;

import app.back.dto.AbstractEntity;
import app.back.exception.BackNotFoundException;
import app.back.repository.AbstractEntityRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    void testDelete(@Autowired EntityManager entityManager) {
        var entity1 = createBasicObject();
        var entity2 = createBasicObject();
        dtoService.save(entity1);
        dtoService.save(entity2);

        entityManager.detach(entity1);
        Assertions.assertTrue(dtoService.findById(entity1.getId()).isPresent());
        dtoService.delete(entity1.getId());

        Assertions.assertTrue(dtoService.findById(entity1.getId()).isEmpty());
        Assertions.assertTrue(dtoService.findById(entity2.getId()).isPresent());
    }

    @Test
    @Order(4)
    void testFindAndDelete(@Autowired EntityManager entityManager) {
        var entity1 = createBasicObject();
        var entity2 = createBasicObject();
        dtoService.save(entity1);
        dtoService.save(entity2);

        entityManager.detach(entity1);
        Assertions.assertTrue(dtoService.findById(entity1.getId()).isPresent());

        var result = dtoService.findAndDelete(entity1.getId()).orElseThrow(() -> new RuntimeException("Aucun objet trouvé."));
        Assertions.assertTrue(dtoService.findById(entity1.getId()).isEmpty());
        Assertions.assertTrue(dtoService.findById(entity2.getId()).isPresent());

        Assertions.assertEquals(entity1.getId(), result.getId());
    }

    @Test
    @Order(5)
    void testFindAndDeleteNotFound() {
        var entity1 = createBasicObject();
        var entity2 = createBasicObject();
        dtoService.save(entity1);
        dtoService.save(entity2);

        var longList = Arrays.asList(1L, 2L, 3L);
        longList.remove(entity1.getId());
        longList.remove(entity2.getId());

        var result = dtoService.findAndDelete(longList.getFirst());
        Assertions.assertTrue(result.isEmpty());

        Assertions.assertTrue(dtoService.findById(entity1.getId()).isPresent());
        Assertions.assertTrue(dtoService.findById(entity2.getId()).isPresent());
    }

    @Test
    @Order(6)
    void testUpdateWithoutSave() {
        var entity = createBasicObject();
        entity.setId(1L);
        Assertions.assertThrows(BackNotFoundException.class, () -> dtoService.save(entity));
    }
}
