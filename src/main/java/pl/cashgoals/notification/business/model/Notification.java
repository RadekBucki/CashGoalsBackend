package pl.cashgoals.notification.business.model;

import pl.cashgoals.notification.business.service.source.Source;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public record Notification(
        Template template,
        Locale locale,
        User user,
        Map<String, String> variables,
        List<Source> source
) {
}
