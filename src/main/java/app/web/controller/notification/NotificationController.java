package app.web.controller.notification;

import app.web.pojo.PojoScheduleNotification;
import app.web.service.ScheduleNotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final ScheduleNotificationService scheduleNotificationService;

    public NotificationController(ScheduleNotificationService scheduleNotificationService) {
        this.scheduleNotificationService = scheduleNotificationService;
    }

    @GetMapping("/findById")
    public PojoScheduleNotification findById(@RequestParam("id") long id) {
        return scheduleNotificationService.findOne(id);
    }

}
