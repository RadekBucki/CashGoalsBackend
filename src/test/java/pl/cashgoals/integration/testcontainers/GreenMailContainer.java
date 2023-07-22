package pl.cashgoals.integration.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class GreenMailContainer extends GenericContainer<GreenMailContainer> {
    private static final String IMAGE_VERSION = "greenmail/standalone:latest";

    private static GreenMailContainer container;

    private GreenMailContainer() {
        super(IMAGE_VERSION);
    }

    public static GreenMailContainer getInstance() {
        if (container == null) {
            container = new GreenMailContainer()
                    .waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1))
                    .withEnv("GREENMAIL_OPTS", "-Dgreenmail.setup.test.smtp -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.users=user:admin")
                    .withExposedPorts(3025);
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
