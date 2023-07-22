package pl.cashgoals.integration.testcontainers;

public class RabbitMQContainer extends org.testcontainers.containers.RabbitMQContainer {
    private static final String IMAGE_VERSION = "rabbitmq:latest";
    private static RabbitMQContainer container;
    private RabbitMQContainer() {
        super(IMAGE_VERSION);
    }

    public static RabbitMQContainer getInstance() {
        if (container == null) {
            container = new RabbitMQContainer();
        }
        return container;
    }
}