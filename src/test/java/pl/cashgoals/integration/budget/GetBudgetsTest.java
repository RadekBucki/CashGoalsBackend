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

class GetBudgetsTest extends AbstractIntegrationTest {
    @DisplayName("Should get budget")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldGetBudgets() {
        budgetRequests.getBudgets()
                .errors().verify()
                .path("budgets").entityList(Budget.class).satisfies(budgets -> {
                    assertEquals(1, budgets.size());
                    assertNotNull(budgets.get(0).getId());
                    assertEquals("test", budgets.get(0).getName());
                    assertEquals(Step.INCOMES, budgets.get(0).getInitializationStep());
                });
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        budgetRequests.getBudgets()
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }
}
