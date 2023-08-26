package pl.cashgoals.unit.notification.business.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.business.model.Template;
import pl.cashgoals.notification.business.service.NotificationPublishService;
import pl.cashgoals.notification.business.service.source.Source;
import pl.cashgoals.notification.communication.producer.NotificationProducer;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationPublishServiceTest {
    @Mock
    NotificationProducer notificationProducer;
    @InjectMocks
    NotificationPublishService notificationPublishService;
    @Captor
    ArgumentCaptor<Notification> notificationArgumentCaptor;

    @DisplayName("Should publish notification")
    @Test
    void shouldPublishNotification() {
        notificationPublishService.publish(
                Template.ACTIVATION,
                User.builder()
                        .enabled(true)
                        .email("test@example.com")
                        .build(),
                Map.of("key", "value"),
                List.of(Source.EMAIL)
        );

        verify(notificationProducer).publish(notificationArgumentCaptor.capture());

        Notification notification = notificationArgumentCaptor.getValue();
        assertNotNull(notification);
        assertEquals(Template.ACTIVATION, notification.getTemplate());
        assertEquals("test@example.com", notification.getEmail());
        assertEquals(Map.of("key", "value"), notification.getVariables());
        assertEquals(List.of(Source.EMAIL), notification.getSource());
    }
}
