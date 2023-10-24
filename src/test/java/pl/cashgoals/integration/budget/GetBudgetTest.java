package pl.cashgoals.integration.budget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.configuration.AbstractIntegrationTest;

import java.util.Objects;

import static graphql.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GetBudgetTest extends AbstractIntegrationTest {
    @DisplayName("Should get budget")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldGetBudget() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        budgetRequests.getBudget(budgetId)
                .errors().verify()
                .path("budget").entity(Budget.class).satisfies(budget -> {
                    assertNotNull(budget.getId());
                    assertEquals("test", budget.getName());
                    assertEquals(Step.INCOMES, budget.getInitializationStep());
                });
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        budgetRequests.getBudget(budgetId)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }

    @DisplayName("Should return access denied when user has no rights to budget")
    @Test
    @WithMockUser(username = "test2@example.com", authorities = {"USER"})
    void shouldReturnAccessDeniedWhenUserHasNoRightsToBudget() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        budgetRequests.getBudget(budgetId)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }
}
