package pl.cashgoals.notification.communication.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.communication.configuration.NotificationQueueConfiguration;

@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final AmqpTemplate amqpTemplate;

    public void publish(Notification notification) {
        amqpTemplate.convertAndSend(
                NotificationQueueConfiguration.QUEUE_EXCHANGE_NAME,
                NotificationQueueConfiguration.QUEUE_NAME,
                notification
        );
    }
}
