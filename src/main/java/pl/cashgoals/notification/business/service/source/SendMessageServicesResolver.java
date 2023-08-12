package pl.cashgoals.notification.business.service.source;

import org.springframework.stereotype.Component;
import pl.cashgoals.notification.business.model.Notification;

import java.util.List;
import java.util.Map;

@Component
public class SendMessageServicesResolver {
    private final Map<Source, SendMessageService> sendMessageServices;

    public SendMessageServicesResolver(EmailService emailService) {
        sendMessageServices = Map.of(
                Source.EMAIL, emailService
        );
    }

    public List<SendMessageService> resolve(Notification notification) {
        return resolveSources(notification)
                .stream()
                .map(this.sendMessageServices::get)
                .toList();
    }

    private static List<Source> resolveSources(Notification notification) {
        if (notification.getSource().isEmpty()) {
            return List.of(Source.EMAIL);
        } else {
            return notification.getSource();
        }
    }

}
