package app.web.transform;

import app.back.dto.Event;
import app.back.dto.notification.ScheduleNotification;
import app.web.pojo.PojoEntity;
import app.web.pojo.PojoScheduleNotification;
import app.web.service.KeycloakUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransformScheduleNotification implements Transform<ScheduleNotification, PojoScheduleNotification> {

    private final TransformEvent transformEvent;
    private final KeycloakUserService keycloakUserService;


    private final Logger logger = LoggerFactory.getLogger(TransformScheduleNotification.class);

    public TransformScheduleNotification(TransformEvent transformEvent, KeycloakUserService keycloakUserService) {
        this.transformEvent = transformEvent;
        this.keycloakUserService = keycloakUserService;
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
        pojoNotif.setUsers(keycloakUserService.findByIds(dto.getNotifiedUsers()));
        return pojoNotif;
    }
}
