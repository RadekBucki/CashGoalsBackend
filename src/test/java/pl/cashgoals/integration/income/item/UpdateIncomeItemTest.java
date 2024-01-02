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
import pl.cashgoals.income.business.model.IncomeItemInput;
import pl.cashgoals.income.persistence.model.IncomeItem;
import pl.cashgoals.user.persistence.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateIncomeItemTest extends AbstractIntegrationTest {
    @DisplayName("Should update income item")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldUpdateIncomeItem() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        IncomeItem incomeItem = incomeItemRepository.findAll().get(0);
        incomeItem.setName("test name changed");
        incomeRequests.updateIncomeItem(
                        budgetId,
                        IncomeItemInput.builder()
                                .id(incomeItem.getId())
                                .incomeId(incomeItem.getIncome().getId())
                                .name(incomeItem.getName())
                                .description(incomeItem.getDescription())
                                .amount(incomeItem.getAmount())
                                .date(incomeItem.getDate())
                                .build()
                )
                .errors().verify()
                .path("updateIncomeItem")
                .entity(IncomeItem.class)
                .satisfies(incomeItem1 -> {
                    assertEquals("test name changed", incomeItem1.getName());
                    assertEquals("test", incomeItem1.getDescription());
                    assertEquals(100, incomeItem1.getAmount());
                    assertEquals(LocalDate.of(2023, 12, 31), incomeItem1.getDate());
                });
    }

    @DisplayName("Should create income item")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldCreateIncomeItem() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        Long incomeId = incomeRepository.findAll().get(0).getId();
        IncomeItemInput incomeItem = IncomeItemInput.builder()
                .incomeId(incomeId)
                .name("test123")
                .description("test123")
                .amount(100.0)
                .date(LocalDate.of(2024, 1, 1))
                .build();
        incomeRequests.updateIncomeItem(budgetId, incomeItem)
                .errors().verify()
                .path("updateIncomeItem")
                .entity(IncomeItem.class)
                .satisfies(incomeItem1 -> {
                    assertEquals("test123", incomeItem1.getName());
                    assertEquals("test123", incomeItem1.getDescription());
                    assertEquals(100, incomeItem1.getAmount());
                    assertEquals(LocalDate.of(2024, 1, 1), incomeItem1.getDate());
                    assertEquals(incomeId, incomeItem1.getIncome().getId());
                });
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        IncomeItem incomeItem = incomeItemRepository.findAll().get(0);
        incomeItem.setName("test name changed");
        incomeRequests.updateIncomeItem(
                        budgetId,
                        IncomeItemInput.builder()
                                .id(incomeItem.getId())
                                .incomeId(incomeItem.getIncome().getId())
                                .name(incomeItem.getName())
                                .description(incomeItem.getDescription())
                                .amount(incomeItem.getAmount())
                                .date(incomeItem.getDate())
                                .build()
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
            "user has no EDIT_INCOME_ITEMS right to budget, EDIT_CATEGORIES",
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
        IncomeItem incomeItem = incomeItemRepository.findAll().get(0);
        incomeItem.setName("test name changed");
        incomeRequests.updateIncomeItem(
                        budgetId,
                        IncomeItemInput.builder()
                                .id(incomeItem.getId())
                                .incomeId(incomeItem.getIncome().getId())
                                .name(incomeItem.getName())
                                .description(incomeItem.getDescription())
                                .amount(incomeItem.getAmount())
                                .date(incomeItem.getDate())
                                .build()
                )
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }
}
