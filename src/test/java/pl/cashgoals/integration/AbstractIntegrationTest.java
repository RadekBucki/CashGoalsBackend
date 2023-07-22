package pl.cashgoals.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.cashgoals.integration.testcontainers.GreenMailContainer;
import pl.cashgoals.integration.testcontainers.PostgresContainer;
import pl.cashgoals.integration.testcontainers.RabbitMQContainer;
import pl.cashgoals.integration.testcontainers.RedisContainer;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractIntegrationTest {
    @Container
    private static final PostgresContainer postgresContainer = PostgresContainer.getInstance();

    @Container
    private static final RedisContainer redisContainer = RedisContainer.getInstance();

    @Container
    private static final RabbitMQContainer rabbitMQContainer = RabbitMQContainer.getInstance();

    @Container
    private static final GreenMailContainer greenMailContainer = GreenMailContainer.getInstance();

    @Autowired
    protected GraphQlTester graphQlTester;
}
