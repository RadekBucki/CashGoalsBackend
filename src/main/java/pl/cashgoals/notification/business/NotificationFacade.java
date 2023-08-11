package pl.cashgoals.notification.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.notification.business.model.Template;
import pl.cashgoals.notification.business.service.NotificationPublishService;
import pl.cashgoals.notification.business.service.source.Source;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationFacade {
    private final NotificationPublishService notificationPublishService;

    public void sendNotification(Template template, User user, Map<String, String> variables, List<Source> sources) {
        notificationPublishService.publish(template, user, variables, sources);

    }
}
