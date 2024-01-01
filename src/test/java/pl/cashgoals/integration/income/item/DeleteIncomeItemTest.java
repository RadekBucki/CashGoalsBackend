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

class DeleteIncomeItemTest extends AbstractIntegrationTest {
    @DisplayName("Should delete income item")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldDeleteIncomeItem() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        IncomeItem incomeItem = incomeItemRepository.findAll().get(0);
        incomeRequests.deleteIncomeItem(budgetId, incomeItem.getId())
                .errors().verify()
                .path("deleteIncomeItem")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        IncomeItem incomeItem = incomeItemRepository.findAll().get(0);
        incomeRequests.deleteIncomeItem(budgetId, incomeItem.getId())
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
            "user has no EDIT_INCOME_ITEMS right to budget, EDIT_CATEGORIES",
    })
    void shouldReturnAccess(String name, String right) {
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
        IncomeItem incomeItem = incomeItemRepository.findAll().get(0);
        incomeRequests.deleteIncomeItem(budgetId, incomeItem.getId())
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }
}
