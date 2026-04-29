package app.web.service;

import app.utils.EventUtils;
import app.web.api.EventServiceApi;
import app.web.pojo.PojoEvent;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class EventServiceTest extends BasicTestService<PojoEvent, EventServiceApi> {

    private final EventUtils eventUtils;

    public EventServiceTest(@Autowired EventServiceApi eventService, @Autowired EventUtils eventUtils) {
        super(eventService);
        this.eventUtils = eventUtils;
    }

    @Override
    protected PojoEvent createBasicPojo() {
        return eventUtils.createBasicPojo();
    }

}
