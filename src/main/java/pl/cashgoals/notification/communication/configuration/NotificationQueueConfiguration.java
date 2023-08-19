package pl.cashgoals.notification.communication.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class NotificationQueueConfiguration {
    public static final String EXCHANGE = ".exchange";
    public static final String QUEUE_NAME = "notifications";
    public static final String QUEUE_EXCHANGE_NAME = QUEUE_NAME + EXCHANGE;
    public static final String QUEUE_DLQ_NAME = QUEUE_NAME + ".dlq";
    public static final String QUEUE_DLQ_EXCHANGE_NAME = QUEUE_DLQ_NAME + EXCHANGE;
    public static final String QUEUE_PARKING_LOT_NAME = QUEUE_NAME + ".parking-lot";
    public static final String QUEUE_PARKING_LOT_EXCHANGE_NAME = QUEUE_PARKING_LOT_NAME + EXCHANGE;

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(QUEUE_EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange notificationDlqExchange() {
        return new DirectExchange(QUEUE_DLQ_EXCHANGE_NAME);
    }

    @Bean DirectExchange notificationParkingLotExchange() {
        return new DirectExchange(QUEUE_PARKING_LOT_EXCHANGE_NAME);
    }

    @Bean
    public Queue notificationParkingLotQueue() {
        return QueueBuilder.durable(QUEUE_PARKING_LOT_NAME)
                .build();
    }

    @Bean
    public Queue notificationDlqQueue() {
        return QueueBuilder.durable(QUEUE_DLQ_NAME)
                .withArgument("x-dead-letter-exchange", QUEUE_PARKING_LOT_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", QUEUE_PARKING_LOT_NAME)
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
    public Binding notificationParkingLotBinding() {
        return BindingBuilder.bind(notificationParkingLotQueue())
                .to(notificationParkingLotExchange())
                .with(QUEUE_PARKING_LOT_NAME);
    }
}
