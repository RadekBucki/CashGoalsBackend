package pl.cashgoals.notification.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.business.service.source.SendMessageServicesResolver;
import pl.cashgoals.notification.communication.producer.NotificationProducer;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SendMessageServicesResolver sendMessageServicesResolver;
    private final NotificationProducer notificationProducer;
    public void consume(Notification notification) {
        sendMessageServicesResolver.resolve(notification)
                .forEach(sendMessageService -> sendMessageService.send(notification));
    }

    public void publish(Notification notification) {
        notificationProducer.publish(notification);
    }

}
