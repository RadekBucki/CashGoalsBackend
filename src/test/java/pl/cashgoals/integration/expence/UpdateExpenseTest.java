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
import pl.cashgoals.expense.business.model.ExpenseInput;
import pl.cashgoals.expense.persistence.model.Expense;
import pl.cashgoals.user.persistence.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateExpenseTest extends AbstractIntegrationTest {
    @DisplayName("Should update expense")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldUpdateIncomeItem() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        Expense expense = expenseRepository.findAll().get(0);
        expense.setDescription("test description changed");
        expenseRequests.updateExpense(
                        budgetId,
                        new ExpenseInput(
                                expense.getId(),
                                expense.getDescription(),
                                expense.getAmount(),
                                expense.getDate(),
                                expense.getCategory().getId()
                        )
                )
                .errors().verify()
                .path("updateExpense")
                .entity(Expense.class)
                .satisfies(expense1 -> {
                    assertEquals("test description changed", expense1.getDescription());
                    assertEquals(100, expense1.getAmount());
                    assertEquals(LocalDate.of(2023, 12, 31), expense1.getDate());
                });
    }

    @DisplayName("Should create income item")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldCreateIncomeItem() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        Long categoryId = categoryRepository.findAll().get(0).getId();
        ExpenseInput expense = new ExpenseInput(
                null,
                "test123",
                100.0,
                LocalDate.of(2024, 1, 1),
                categoryId
        );
        expenseRequests.updateExpense(
                        budgetId,
                        expense
                )
                .errors().verify()
                .path("updateExpense")
                .entity(Expense.class)
                .satisfies(expense1 -> {
                    assertEquals("test123", expense1.getDescription());
                    assertEquals(100, expense1.getAmount());
                    assertEquals(LocalDate.of(2024, 1, 1), expense1.getDate());
                });
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        Expense expense = expenseRepository.findAll().get(0);
        expense.setDescription("test description changed");
        expenseRequests.updateExpense(
                        budgetId,
                        new ExpenseInput(
                                expense.getId(),
                                expense.getDescription(),
                                expense.getAmount(),
                                expense.getDate(),
                                expense.getCategory().getId()
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
            "user has no EDIT_EXPENSE right to budget, EDIT_CATEGORIES",
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

        Expense expense = expenseRepository.findAll().get(0);
        expense.setDescription("test description changed");
        expenseRequests.updateExpense(
                        budget.getId().toString(),
                        new ExpenseInput(
                                expense.getId(),
                                expense.getDescription(),
                                expense.getAmount(),
                                expense.getDate(),
                                expense.getCategory().getId()
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
