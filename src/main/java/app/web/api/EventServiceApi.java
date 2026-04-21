package app.web.api;

import app.web.pojo.PojoEvent;

public interface EventServiceApi extends AbstractServiceApi<PojoEvent> {

    PojoEvent findByEventName(String name);

    PojoEvent addSubEvent(int parentEventId, PojoEvent event);
}
