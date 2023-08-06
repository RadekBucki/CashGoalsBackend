package pl.cashgoals.notification.business.service.source;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.cashgoals.notification.business.model.Notification;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SendMessageServicesResolver {
    private final EmailService emailService;

    private final Map<Source, SendMessageService> sendMessageServices = Map.of(
            Source.EMAIL, emailService
    );

    public List<SendMessageService> resolve(Notification notification) {
        return resolveSources(notification)
                .stream()
                .map(this.sendMessageServices::get)
                .toList();
    }

    private List<Source> resolveSources(Notification notification) {
        if (notification.source().isEmpty()) {
            return List.of(Source.EMAIL);
        } else {
            return notification.source();
        }
    }

}
