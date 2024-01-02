package pl.cashgoals.integration.expence;

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
import pl.cashgoals.user.persistence.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetExpensesTest extends AbstractIntegrationTest {
    @DisplayName("Should return all expenses")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldReturnAllExpenses() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        expenseRequests.getExpenses(budgetId, 1, 2024)
                .errors().verify()
                .path("expenses").entityList(Expense.class)
                .hasSize(1)
                .satisfies(expenses -> {
                    Expense expense = expenses.get(0);
                    assertEquals("test2", expense.getDescription());
                    assertEquals(100, expense.getAmount());
                    assertEquals(LocalDate.of(2024, 1, 1), expense.getDate());
                    assertEquals("test2", expense.getCategory().getName());
                });
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        expenseRequests.getExpenses(budgetId, 1, 2024)
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

        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        expenseRequests.getExpenses(budgetId, 1, 2024)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }

}
