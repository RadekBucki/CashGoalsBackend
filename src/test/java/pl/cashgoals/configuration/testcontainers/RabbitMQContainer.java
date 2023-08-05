package pl.cashgoals.configuration.testcontainers;

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

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}