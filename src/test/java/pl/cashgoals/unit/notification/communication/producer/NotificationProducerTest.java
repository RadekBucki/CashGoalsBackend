package pl.cashgoals.unit.notification.communication.producer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.communication.configuration.NotificationQueueConfiguration;
import pl.cashgoals.notification.communication.producer.NotificationProducer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationProducerTest {
    @Mock
    AmqpTemplate amqpTemplate = mock(AmqpTemplate.class);

    @InjectMocks
    NotificationProducer notificationProducer;

    @DisplayName("Should publish notification")
    @Test
    void shouldPublishNotification() {
        Notification notification = new Notification();
        notificationProducer.publish(notification);

        verify(amqpTemplate).convertAndSend(
                NotificationQueueConfiguration.QUEUE_EXCHANGE_NAME,
                NotificationQueueConfiguration.QUEUE_NAME,
                notification
        );
    }
}
