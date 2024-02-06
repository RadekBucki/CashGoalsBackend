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
import pl.cashgoals.goal.business.model.GoalInput;
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
        Long categoryId = categoryRepository.findAll()
                .stream()
                .filter(category -> category.getBudgetId().toString().equals(budgetId))
                .filter(category -> category.getName().equals("test"))
                .findFirst()
                .orElseThrow()
                .getId();
        Long goalId = goalRepository.findAll()
                .stream()
                .filter(goal -> goal.getBudgetId().toString().equals(budgetId))
                .filter(goal -> goal.getName().equals("test"))
                .filter(goal -> goal.getDescription().equals("test"))
                .findFirst()
                .orElseThrow()
                .getId();
        goalRequests.updateGoals(
                        budgetId,
                        List.of(
                                new GoalInput(
                                        goalId,
                                        "test",
                                        "test",
                                        GoalType.PERCENTAGE,
                                        null,
                                        0.6,
                                        categoryId
                                ),
                                new GoalInput(
                                        null,
                                        "test",
                                        "test",
                                        GoalType.PERCENTAGE,
                                        0.4,
                                        null,
                                        categoryId
                                )
                        )
                )
                .errors().verify()
                .path("updateGoals").entityList(Goal.class)
                .hasSize(2)
                .satisfies(goals -> {
                    Optional<Goal> goal1 = goals.stream()
                            .filter(g -> g.getName().equals("test"))
                            .filter(g -> g.getDescription().equals("test"))
                            .filter(g -> g.getType().equals(GoalType.PERCENTAGE))
                            .filter(g -> g.getMin() == null)
                            .filter(g -> g.getMax() != null && g.getMax() == 0.6)
                            .filter(g -> g.getCategory().getName().equals("test"))
                            .findFirst();
                    assertTrue(goal1.isPresent());

                    Optional<Goal> goal2 = goals.stream()
                            .filter(g -> g.getName().equals("test"))
                            .filter(g -> g.getDescription().equals("test"))
                            .filter(g -> g.getType().equals(GoalType.PERCENTAGE))
                            .filter(g -> g.getMin() != null && g.getMin() == 0.4)
                            .filter(g -> g.getMax() == null)
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
                                new GoalInput(
                                        1L,
                                        "test",
                                        "test",
                                        GoalType.PERCENTAGE,
                                        null,
                                        0.6,
                                        1L
                                ),
                                new GoalInput(
                                        null,
                                        "test",
                                        "test",
                                        GoalType.PERCENTAGE,
                                        0.4,
                                        null,
                                        1L
                                )
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
            "user has no rights to budget, ",
            "user has no EDIT_GOALS right, EDIT_EXPENSES",
    })
    void shouldReturnAccessDenied(String testCase, String right) {
        User user = userRepository.getUserByEmail("test2@example.com").orElseThrow();
        Budget budget = budgetRepository.findAll().get(0);
        if (right != null) {
            UserRight userRight = UserRight.builder()
                    .user(user)
                    .budgetId(budget.getId())
                    .right(Right.valueOf(right))
                    .build();
            userRightsRepository.saveAndFlush(userRight);
        }

        String budgetId = budgetRepository.findAll().get(0).getId().toString();

        goalRequests.updateGoals(
                        budgetId,
                        List.of(
                                new GoalInput(
                                        1L,
                                        "test",
                                        "test",
                                        GoalType.PERCENTAGE,
                                        null,
                                        0.6,
                                        1L
                                ),
                                new GoalInput(
                                        null,
                                        "test",
                                        "test",
                                        GoalType.PERCENTAGE,
                                        0.4,
                                        null,
                                        1L
                                )
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
