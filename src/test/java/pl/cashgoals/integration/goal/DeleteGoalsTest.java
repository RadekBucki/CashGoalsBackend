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
import pl.cashgoals.goal.persistence.model.Goal;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;

class DeleteGoalsTest extends AbstractIntegrationTest {
    @DisplayName("Should delete goal")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldDeleteGoal() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        Goal goal = goalRepository.findAll()
                .stream()
                .filter(g -> g.getBudgetId().toString().equals(budgetId))
                .findFirst()
                .orElseThrow();
        goalRequests.deleteGoals(
                        budgetId,
                        List.of(goal.getId())
                )
                .errors()
                .verify()
                .path("deleteGoals")
                .entity(Boolean.class)
                .isEqualTo(true);

        List<Goal> goals = goalRepository.findAll();
        assertFalse(goals.contains(goal));
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        Long goalId = goalRepository.findAll()
                .stream()
                .filter(g -> g.getBudgetId().toString().equals(budgetId))
                .findFirst()
                .orElseThrow()
                .getId();
        goalRequests.deleteGoals(
                        budgetId,
                        List.of(goalId)
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
            "user has no EDIT_EXPENSES right, EDIT_EXPENSES",
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
        Long goalId = goalRepository.findAll()
                .stream()
                .filter(g -> g.getBudgetId().toString().equals(budgetId))
                .findFirst()
                .orElseThrow()
                .getId();

        goalRequests.deleteGoals(
                        budgetId,
                        List.of(goalId)
                )
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }
}
