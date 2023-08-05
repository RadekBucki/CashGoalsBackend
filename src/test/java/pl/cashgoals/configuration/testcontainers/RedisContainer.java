package pl.cashgoals.configuration.testcontainers;

import org.testcontainers.containers.GenericContainer;

import java.util.List;

public class RedisContainer extends GenericContainer<RedisContainer> {
    private static final String IMAGE_VERSION = "redis:latest";
    private static RedisContainer container;

    private RedisContainer() {
        super(IMAGE_VERSION);
        this.setExposedPorts(List.of(6379));
    }

    public static RedisContainer getInstance() {
        if (container == null) {
            container = new RedisContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("spring.data.redis.port", container.getMappedPort(6379).toString());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}

