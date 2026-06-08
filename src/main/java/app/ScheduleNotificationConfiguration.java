package app;

import app.web.service.ScheduleNotificationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
public class ScheduleNotificationConfiguration {

    private final ScheduleNotificationService scheduleNotificationService;

    public ScheduleNotificationConfiguration(ScheduleNotificationService scheduleNotificationService) {
        this.scheduleNotificationService = scheduleNotificationService;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void notifyEvents() {
        var notifications = scheduleNotificationService.find1MonthNotification();
        System.out.println(notifications);
    }

}
