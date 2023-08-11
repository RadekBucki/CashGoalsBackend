package pl.cashgoals.notification.communication.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class NotificationQueueConfiguration {
    public static final String QUEUE_NAME = "notifications";
    public static final String QUEUE_EXCHANGE_NAME = QUEUE_NAME + ".exchange";
    public static final String QUEUE_DLQ_NAME = QUEUE_NAME + ".dlq";
    public static final String QUEUE_DLQ_EXCHANGE_NAME = QUEUE_DLQ_NAME + ".exchange";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(QUEUE_EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange notificationDlqExchange() {
        return new DirectExchange(QUEUE_DLQ_EXCHANGE_NAME);
    }

    @Bean
    public Queue notificationDlqQueue() {
        return QueueBuilder.durable(QUEUE_DLQ_NAME)
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", QUEUE_DLQ_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", QUEUE_DLQ_NAME)
                .build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(QUEUE_NAME);
    }

    @Bean
    public Binding notificationDlqBinding() {
        return BindingBuilder.bind(notificationDlqQueue())
                .to(notificationDlqExchange())
                .with(QUEUE_DLQ_NAME);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
