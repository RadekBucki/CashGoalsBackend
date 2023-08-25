package pl.cashgoals.notification.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.business.service.source.SendMessageServicesResolver;
import pl.cashgoals.user.business.UserFacade;

@Service
@RequiredArgsConstructor
public class NotificationConsumeService {
    private final SendMessageServicesResolver sendMessageServicesResolver;
    private final UserFacade userFacade;

    public void consume(Notification notification) {
        notification.setUser(userFacade.getUserByEmail(notification.getEmail()));
        sendMessageServicesResolver.resolve(notification)
                .forEach(sendMessageService -> sendMessageService.send(notification));
    }
}
