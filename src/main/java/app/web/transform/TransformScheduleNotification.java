package app.web.transform;

import app.back.dto.Event;
import app.back.dto.notification.ScheduleNotification;
import app.back.service.DtoScheduleNotificationService;
import app.web.pojo.PojoEntity;
import app.web.pojo.PojoScheduleNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformScheduleNotification implements Transform<ScheduleNotification, PojoScheduleNotification> {

    private final DtoScheduleNotificationService dtoScheduleNotificationService;
    private final TransformEvent transformEvent;

    private Logger logger = LoggerFactory.getLogger(TransformScheduleNotification.class);

    public TransformScheduleNotification(DtoScheduleNotificationService dtoScheduleNotificationService, TransformEvent transformEvent) {
        this.dtoScheduleNotificationService = dtoScheduleNotificationService;
        this.transformEvent = transformEvent;
    }

    @Override
    public ScheduleNotification toDto(PojoScheduleNotification pojo) {
        return null;
    }

    @Override
    public PojoScheduleNotification toPojo(ScheduleNotification dto) {
        var pojoNotif = new PojoScheduleNotification();
        pojoNotif.setExecutionDate(dto.getExecutionDate());
        PojoEntity pojoRelatedEntity = switch(dto.getEntity()) {
            case Event _ -> transformEvent.toPojo((Event) dto.getEntity());
            default -> {
                logger.warn("Le type d'entité {} est inconnu.", dto.getEntity().getClass());
                yield null;
            }
        };
        pojoNotif.setEntity(pojoRelatedEntity);
        return pojoNotif;
    }
}
