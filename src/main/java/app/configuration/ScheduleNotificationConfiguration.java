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
        var notifications = scheduleNotificationService.findNotifications(Instant.now(), 1);
        notifications.forEach(this::sendToRabbit);
    }

    private void sendToRabbit(ScheduleNotification notification) {
        rabbitTemplate.convertAndSend("notifications.exchange", "discord", new RabbitNotificationMessage(notification.getId()));
    }

}
