package pl.cashgoals.integration.income;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.income.persistence.model.Income;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetIncomesTest extends AbstractIntegrationTest {
    @DisplayName("Should get incomes")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldGetIncomes() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        incomeRequests.getIncomes(budgetId)
                .errors().verify()
                .path("incomes")
                .entityList(Income.class)
                .hasSize(1)
                .satisfies(incomes -> {
                    Income income = incomes.get(0);
                    assertEquals("test", income.getName());
                    assertEquals("test", income.getDescription());
                });
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        incomeRequests.getIncomes(budgetId)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }
}
