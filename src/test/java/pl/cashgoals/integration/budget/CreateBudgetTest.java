package pl.cashgoals.integration.budget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.configuration.AbstractIntegrationTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateBudgetTest extends AbstractIntegrationTest {
    @DisplayName("Should create budget")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldCreateBudget() {
        budgetRequests.createBudget("test_budget")
                .errors().verify()
                .path("budget").entity(Budget.class).satisfies(budget -> {
                    assertNotNull(budget.getId());
                    assertEquals("test_budget", budget.getName());
                    assertEquals(Step.INCOMES, budget.getInitializationStep());
                });
    }

    @DisplayName("Should return access denied")
    @Test
    void shouldReturnAccessDenied() {
        budgetRequests.createBudget("test_budget")
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }

    @DisplayName("Should return validation error")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldReturnValidationError() {
        budgetRequests.createBudget("")
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Size.min.message")
                                && responseError.getErrorType().equals(graphql.ErrorType.ValidationError)
                )
                .verify();
    }
}
