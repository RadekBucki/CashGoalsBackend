package pl.cashgoals.notification.business.service.source;

import pl.cashgoals.notification.business.model.Notification;

public interface SendMessageService {
    void send(Notification notification);
}
