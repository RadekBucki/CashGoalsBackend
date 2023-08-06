package pl.cashgoals.notification.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.business.service.NotificationService;

@Service
@RequiredArgsConstructor
public class NotificationFacade {
    private final NotificationService notificationService;
    public void sendNotification(Notification notification) {
        notificationService.publish(notification);
    }
}
