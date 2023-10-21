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
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.budget.persistence.model.UserRights;
import pl.cashgoals.budget.persistence.repository.BudgetRepository;
import pl.cashgoals.budget.persistence.repository.UserRightsRepository;
import pl.cashgoals.configuration.requests.*;
import pl.cashgoals.configuration.testcontainers.GreenMail;
import pl.cashgoals.configuration.testcontainers.PostgresContainer;
import pl.cashgoals.configuration.testcontainers.RabbitMQContainer;
import pl.cashgoals.configuration.testcontainers.RedisContainer;
import pl.cashgoals.expence.persistence.model.Category;
import pl.cashgoals.expence.persistence.repository.CategoryRepository;
import pl.cashgoals.user.persistence.model.Theme;
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
    @Autowired
    protected BudgetRepository budgetRepository;
    @Autowired
    protected UserRightsRepository userRightsRepository;

    /**
     * Requests
     */
    protected UserRequests userRequests;
    protected BudgetRequests budgetRequests;
    protected ExpenceRequests expenceRequests;
    protected GoalRequests goalRequests;
    protected IncomeRequests incomeRequests;
    protected CategoryRepository categoryRepository;

    @BeforeAll
    static void beforeAll() {
        Locale locale = new Locale.Builder()
                .setLanguage("test")
                .build();
        Locale.setDefault(locale);
        Locale.setDefault(Locale.Category.DISPLAY, locale);
        System.setProperty("spring.profiles.active", "test");
    }

    @BeforeEach
    public void setup() {
        // Repositories
        userRepository.deleteAll();
        budgetRepository.deleteAll();
        userRightsRepository.deleteAll();
        categoryRepository.deleteAll();

        //Requests
        userRequests = new UserRequests(graphQlTester);
        budgetRequests = new BudgetRequests(graphQlTester);
        expenceRequests = new ExpenceRequests(graphQlTester);
        goalRequests = new GoalRequests(graphQlTester);
        incomeRequests = new IncomeRequests(graphQlTester);

        // Initial data
        User user = User.builder()
                .enabled(true)
                .name("test")
                .password(passwordEncoder.encode("Test123!"))
                .email("test@example.com")
                .theme(Theme.DARK)
                .locale(Locale.ENGLISH)
                .build();
        User user2 = User.builder()
                .enabled(true)
                .name("test2")
                .password(passwordEncoder.encode("Test123!"))
                .email("test2@example.com")
                .theme(Theme.DARK)
                .locale(Locale.ENGLISH)
                .build();

        User inactiveUser = User.builder()
                .enabled(false)
                .name("inactive")
                .password(passwordEncoder.encode("Test123!"))
                .email("inactive@example.com")
                .theme(Theme.LIGHT)
                .locale(Locale.ENGLISH)
                .build();
        inactiveUser.getTokens().add(
                UserToken.builder()
                        .token("token")
                        .type(TokenType.ACTIVATION)
                        .user(inactiveUser)
                        .build()
        );
        userRepository.saveAllAndFlush(List.of(user, user2, inactiveUser));

        Budget budget = Budget.builder()
                .name("test")
                .initializationStep(Step.INCOMES)
                .build();
        budgetRepository.saveAndFlush(budget);

        UserRights userRight = UserRights.builder()
                .budget(budget)
                .user(user)
                .right(Right.OWNER)
                .build();
        userRightsRepository.saveAllAndFlush(List.of(userRight));

        Category testCategory = Category.builder()
                .name("test")
                .description("test")
                .visible(true)
                .children(List.of(
                        Category.builder()
                                .name("test2")
                                .description("test2")
                                .visible(true)
                                .budgetId(budget.getId())
                                .build()
                ))
                .budgetId(budget.getId())
                .build();
        Category unvisibleCategory = Category.builder()
                .name("unvisible")
                .description("unvisible")
                .visible(false)
                .budgetId(budget.getId())
                .build();
        categoryRepository.saveAllAndFlush(List.of(testCategory, unvisibleCategory));
    }
}
