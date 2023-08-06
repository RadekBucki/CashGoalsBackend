package pl.cashgoals.notification.communication.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class NotificationQueueConfiguration {
    public static final String QUEUE_NAME = "notifications";
    public static final String QUEUE_DLQ_NAME = QUEUE_NAME + ".dlq";
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .exclusive()
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ_NAME)
                .build();
    }

    @Bean
    public Queue notificationDlqQueue() {
        return QueueBuilder.durable(QUEUE_DLQ_NAME)
                .exclusive()
                .build();
    }
}
