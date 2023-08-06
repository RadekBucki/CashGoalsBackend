package pl.cashgoals.configuration;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.cashgoals.configuration.requests.UserRequests;
import pl.cashgoals.configuration.testcontainers.GreenMail;
import pl.cashgoals.configuration.testcontainers.PostgresContainer;
import pl.cashgoals.configuration.testcontainers.RabbitMQContainer;
import pl.cashgoals.configuration.testcontainers.RedisContainer;
import pl.cashgoals.user.persistence.model.TokenType;
import pl.cashgoals.user.persistence.model.User;
import pl.cashgoals.user.persistence.model.UserToken;
import pl.cashgoals.user.persistence.repository.UserRepository;

import java.util.List;
import java.util.Locale;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractIntegrationTest {
    /**
     * Containers
     */
    @Container
    private static final PostgresContainer postgresContainer = PostgresContainer.getInstance();

    @Container
    private static final RedisContainer redisContainer = RedisContainer.getInstance();

    @Container
    private static final RabbitMQContainer rabbitMQContainer = RabbitMQContainer.getInstance();

    @RegisterExtension
    protected static final GreenMailExtension greenMail = GreenMail.getInstance();

    /**
     * Dependencies
     */

    @Autowired
    protected GraphQlTester graphQlTester;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    /**
     * Repositories
     */

    @Autowired
    protected UserRepository userRepository;

    /**
     * Requests
     */
    protected UserRequests userRequests;

    @BeforeAll
    static void beforeAll() {
        Locale.setDefault(Locale.ROOT);
        Locale.setDefault(Locale.Category.DISPLAY, Locale.ROOT);
        System.setProperty("spring.profiles.active", "test");
    }

    @BeforeEach
    public void setup() {
        // Repositories
        userRepository.deleteAll();

        //Requests
        userRequests = new UserRequests(graphQlTester);

        // Initial data
        User user = User.builder()
                .enabled(true)
                .username("test")
                .password(passwordEncoder.encode("Test123!"))
                .email("test@example.com")
                .build();

        User inactiveUser = User.builder()
                .enabled(false)
                .username("inactive")
                .password(passwordEncoder.encode("Test123!"))
                .email("inactive@example.com")
                .build();
        inactiveUser.getTokens().add(
                UserToken.builder()
                        .token("token")
                        .type(TokenType.ACTIVATION)
                        .user(inactiveUser)
                        .build()
        );

        userRepository.saveAllAndFlush(List.of(user, inactiveUser));
    }
}
