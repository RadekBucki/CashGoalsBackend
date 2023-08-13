package pl.cashgoals.unit.notification.communication.consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.business.service.NotificationConsumeService;
import pl.cashgoals.notification.communication.consumer.NotificationConsumer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {
    @Mock
    NotificationConsumeService notificationConsumeService;

    @InjectMocks
    NotificationConsumer notificationConsumer;

    @DisplayName("Should consume notification")
    @Test
    void shouldConsumeNotification() {
        Notification notification = new Notification();
        notificationConsumer.consume(notification);
        verify(notificationConsumeService, times(1)).consume(notification);
    }
}
