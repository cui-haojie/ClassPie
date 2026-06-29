package org.example.classpiserver.controller.notification;

import org.example.classpiserver.dto.account.AccountRequest;
import org.example.classpiserver.entity.Notification;
import org.example.classpiserver.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/editor")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/notifications")
    public List<Notification> getNotifications(@RequestBody AccountRequest request) {
        return notificationService.getNotifications(request.getAccount());
    }

    @PostMapping("/notificationCount")
    public Integer getNotificationCount(@RequestBody AccountRequest request) {
        return notificationService.getUnreadNotificationCount(request.getAccount());
    }

    @PutMapping("/readNotification")
    public boolean readNotification(@RequestBody Notification request) {
        return notificationService.markNotificationRead(request.getId(), request.getAccount());
    }

    @PutMapping("/readAllNotifications")
    public boolean readAllNotifications(@RequestBody AccountRequest request) {
        return notificationService.markAllNotificationsRead(request.getAccount());
    }
}
