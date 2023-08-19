package pl.cashgoals.notification.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.business.model.Template;
import pl.cashgoals.notification.business.service.source.Source;
import pl.cashgoals.notification.communication.producer.NotificationProducer;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationPublishService {
    private final NotificationProducer notificationProducer;

    public void publish(Template template, User user, Map<String, String> variables, List<Source> sources) {
        Notification notification = Notification.builder()
                .template(template)
                .locale(LocaleContextHolder.getLocale())
                .username(user.getUsername())
                .variables(variables)
                .source(sources)
                .build();
        notificationProducer.publish(notification);
    }
}
