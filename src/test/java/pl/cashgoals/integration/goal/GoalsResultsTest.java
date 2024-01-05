package pl.cashgoals.integration.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.UserRight;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.expense.persistence.model.Expense;
import pl.cashgoals.goal.business.model.GoalResult;
import pl.cashgoals.user.persistence.model.User;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GoalsResultsTest extends AbstractIntegrationTest {
    @DisplayName("Should return not reached goal result")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldReturnNotReachedGoalResult() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        goalRequests.getGoalResults(budgetId, 2024, 1)
                .errors()
                .verify()
                .path("goalResults")
                .entityList(GoalResult.class)
                .hasSize(1)
                .satisfies(goalResults -> {
                    GoalResult goalResult = goalResults.get(0);
                    assertEquals("test", goalResult.goal().getName());
                    assertFalse(goalResult.reached());
                    assertEquals(100, goalResult.actual());
                });
    }

    @DisplayName("Should return reached goal result")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldReturnReachedGoalResult() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();

        Expense expense = expenseRepository.findAllByBudgetIdAndYearAndMonth(
                        UUID.fromString(budgetId),
                        2024,
                        1
                )
                .get(0);
        expense.setAmount(50.0);
        expenseRepository.saveAndFlush(expense);

        goalRequests.getGoalResults(budgetId, 2024, 1)
                .errors()
                .verify()
                .path("goalResults")
                .entityList(GoalResult.class)
                .hasSize(1)
                .satisfies(goalResults -> {
                    GoalResult goalResult = goalResults.get(0);
                    assertEquals("test", goalResult.goal().getName());
                    assertTrue(goalResult.reached());
                    assertEquals(50, goalResult.actual());
                });
    }


    @DisplayName("Should return access denied when user is not authorized")
    @Test
    void shouldReturnAccessDeniedWhenUserIsNotAuthorized() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        goalRequests.getGoalResults(budgetId, 2024, 1)
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
            "user has no VIEW right to budget, EDIT_CATEGORIES",
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

        String budgetId = budget.getId().toString();
        goalRequests.getGoalResults(budgetId, 2024, 1)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }
}
