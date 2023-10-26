package pl.cashgoals.integration.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.budget.persistence.model.UserRight;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.expence.persistence.model.Category;
import pl.cashgoals.goal.persistence.model.Goal;
import pl.cashgoals.goal.persistence.model.GoalType;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static graphql.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateGoalsTest extends AbstractIntegrationTest {
    @DisplayName("Should update goal")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldUpdateGoal() {
        Budget budget = budgetRepository.findAll().get(0);
        budget.setInitializationStep(Step.GOALS);
        budgetRepository.saveAndFlush(budget);

        String budgetId = budget.getId().toString();
        goalRequests.updateGoals(
                        budgetId,
                        List.of(
                                Goal.builder()
                                        .id(1L)
                                        .name("test")
                                        .description("test")
                                        .type(GoalType.PERCENTAGE_MAX)
                                        .value(0.6)
                                        .category(Category.builder().id(1L).build())
                                        .build(),
                                Goal.builder()
                                        .name("test")
                                        .description("test")
                                        .type(GoalType.PERCENTAGE_MIN)
                                        .value(0.4)
                                        .category(Category.builder().id(1L).build())
                                        .build()
                        )
                )
                .errors().verify()
                .path("updateGoals").entityList(Goal.class)
                .hasSize(1)
                .satisfies(goals -> {
                    Optional<Goal> goal1 = goals.stream()
                            .filter(g -> g.getName().equals("test"))
                            .filter(g -> g.getDescription().equals("test"))
                            .filter(g -> g.getType().equals(GoalType.PERCENTAGE_MAX))
                            .filter(g -> g.getValue().equals(0.6))
                            .filter(g -> g.getCategory().getName().equals("test"))
                            .findFirst();
                    assertTrue(goal1.isPresent());

                    Optional<Goal> goal2 = goals.stream()
                            .filter(g -> g.getName().equals("test"))
                            .filter(g -> g.getDescription().equals("test"))
                            .filter(g -> g.getType().equals(GoalType.PERCENTAGE_MIN))
                            .filter(g -> g.getValue().equals(0.4))
                            .filter(g -> g.getCategory().getName().equals("test"))
                            .findFirst();
                    assertTrue(goal2.isPresent());
                });

        budget = budgetRepository.findAll().get(0);
        assertEquals(Step.USERS_AND_RIGHTS, budget.getInitializationStep());
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        goalRequests.updateGoals(
                        budgetId,
                        List.of(
                                Goal.builder()
                                        .id(1L)
                                        .name("test")
                                        .description("test")
                                        .type(GoalType.PERCENTAGE_MAX)
                                        .value(0.6)
                                        .category(Category.builder().id(1L).build())
                                        .build(),
                                Goal.builder()
                                        .name("test")
                                        .description("test")
                                        .type(GoalType.PERCENTAGE_MIN)
                                        .value(0.4)
                                        .category(Category.builder().id(1L).build())
                                        .build()
                        )
                )
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }

    @DisplayName("Should return access denied when")
    @WithMockUser(username = "test2@example.com", authorities = {"USER"})
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "user has no rights to budget",
            "user has no EDIT_EXPENSES right, EDIT_EXPENSES",
    })
    void shouldReturnAccessDenied(String testCase, String right) {
        User user = userRepository.getUserByEmail("test2@example.com").orElseThrow();
        Budget budget = budgetRepository.findAll().get(0);
        UserRight userRight = UserRight.builder()
                .user(user)
                .budget(budget)
                .right(Right.valueOf(right))
                .build();
        userRightsRepository.saveAndFlush(userRight);

        String budgetId = budgetRepository.findAll().get(0).getId().toString();

        goalRequests.updateGoals(
                        budgetId,
                        List.of(
                                Goal.builder()
                                        .id(1L)
                                        .name("test")
                                        .description("test")
                                        .type(GoalType.PERCENTAGE_MAX)
                                        .value(0.6)
                                        .category(Category.builder().id(1L).build())
                                        .build(),
                                Goal.builder()
                                        .name("test")
                                        .description("test")
                                        .type(GoalType.PERCENTAGE_MIN)
                                        .value(0.4)
                                        .category(Category.builder().id(1L).build())
                                        .build()
                        )
                )
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }
}
