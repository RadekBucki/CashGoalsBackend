package pl.cashgoals.notification.communication.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.business.service.NotificationConsumeService;
import pl.cashgoals.notification.communication.configuration.NotificationQueueConfiguration;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationConsumeService notificationPublishService;

    @RabbitListener(queues = NotificationQueueConfiguration.QUEUE_NAME)
    @Async("notificationTaskExecutor")
    public void consume(Notification notification) {
        notificationPublishService.consume(notification);
    }

    @RabbitListener(queues = NotificationQueueConfiguration.QUEUE_DLQ_NAME)
    @Async("notificationTaskExecutor")
    public void dlqConsume(Message<Notification> message) {
        notificationPublishService.consume(message.getPayload());
    }
}
