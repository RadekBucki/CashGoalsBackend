package pl.cashgoals.notification.communication.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.communication.configuration.NotificationQueueConfiguration;

@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;
    public void publish(Notification notification) {
        rabbitTemplate.convertAndSend(NotificationQueueConfiguration.QUEUE_NAME, notification);
    }
}
