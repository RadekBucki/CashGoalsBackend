package pl.cashgoals.configuration.testcontainers;

public class PostgresContainer extends org.testcontainers.containers.PostgreSQLContainer<PostgresContainer> {
    private static final String IMAGE_VERSION = "postgres:latest";
    private static PostgresContainer container;
    private PostgresContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgresContainer getInstance() {
        if (container == null) {
            container = new PostgresContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("POSTGRES_HOST", container.getHost());
        System.setProperty("POSTGRES_LOCAL_PORT", container.getMappedPort(5432).toString());
        System.setProperty("POSTGRES_DOCKER_PORT", "5432");
        System.setProperty("POSTGRES_USER", container.getUsername());
        System.setProperty("POSTGRES_PASSWORD", container.getPassword());
        System.setProperty("POSTGRES_DB", container.getDatabaseName());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
