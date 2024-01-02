package pl.cashgoals.integration.income.item;

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
import pl.cashgoals.income.persistence.model.IncomeItem;
import pl.cashgoals.user.persistence.model.User;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetIncomeItemsTest extends AbstractIntegrationTest {
    @DisplayName("Should get income items")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldGetIncomeItems() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        incomeRequests.getIncomeItems(budgetId, 12, 2023)
                .errors().verify()
                .path("incomeItems")
                .entityList(IncomeItem.class)
                .hasSize(1)
                .satisfies(incomesItems -> {
                    assertEquals(1, incomesItems.size());
                    IncomeItem incomeItem = incomesItems.get(0);
                    assertEquals("test", incomeItem.getDescription());
                    assertEquals(100, incomeItem.getAmount());
                });
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        incomeRequests.getIncomeItems(budgetId, 12, 2023)
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
        incomeRequests.getIncomeItems(budgetId, 12, 2023)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }
}
