package app.configuration;

import app.back.dto.notification.ScheduleNotification;
import app.back.service.DtoScheduleNotificationService;
import app.rabbit.RabbitNotificationMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class ScheduleNotificationConfiguration {

    private final DtoScheduleNotificationService scheduleNotificationService;
    private final RabbitTemplate rabbitTemplate;

    public ScheduleNotificationConfiguration(DtoScheduleNotificationService scheduleNotificationService, RabbitTemplate rabbitTemplate) {
        this.scheduleNotificationService = scheduleNotificationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void notifyEvents() {
        var notifications30D = scheduleNotificationService.findNotifications(Instant.now(), 30);
        var notifications7D = scheduleNotificationService.findNotifications(Instant.now(), 7);
        var notifications1D = scheduleNotificationService.findNotifications(Instant.now(), 1);

        var entityId1DSet = notifications1D.stream().map(notification -> notification.getEntity().getId()).collect(Collectors.toSet());
        var entityId7DSet = notifications7D.stream().map(notification -> notification.getEntity().getId()).collect(Collectors.toSet());

        var iterator7d = notifications7D.iterator();
        while(iterator7d.hasNext()) {
            var notification = iterator7d.next();
            var entityId = notification.getEntity().getId();
            if(!entityId1DSet.contains(entityId)) {
                continue;
            }

            notification.setNotified(true);
            scheduleNotificationService.save(notification);
            iterator7d.remove();
        }

        var iterator30d = notifications30D.iterator();
        while(iterator30d.hasNext()) {
            var notification = iterator30d.next();
            var entityId = notification.getEntity().getId();
            if(!entityId1DSet.contains(entityId) || entityId7DSet.contains(entityId)) {
                continue;
            }

            notification.setNotified(true);
            scheduleNotificationService.save(notification);
            iterator30d.remove();
        }

        this.notify(notifications30D);
        this.notify(notifications7D);
        this.notify(notifications1D);
    }

    private void notify(Collection<ScheduleNotification> notifications) {
        notifications.forEach(notification -> {
            this.sendToRabbit(notification);
            notification.setNotified(true);
            scheduleNotificationService.save(notification);
        });
    }

    private void sendToRabbit(ScheduleNotification notification) {
        rabbitTemplate.convertAndSend("notifications.exchange", "discord", new RabbitNotificationMessage(notification.getId()));
    }

}
