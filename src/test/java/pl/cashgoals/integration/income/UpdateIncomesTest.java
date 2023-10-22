package pl.cashgoals.integration.income;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.budget.persistence.model.UserRights;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.income.persistence.model.Frequency;
import pl.cashgoals.income.persistence.model.Income;
import pl.cashgoals.income.persistence.model.Period;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateIncomesTest extends AbstractIntegrationTest {
    @DisplayName("Should update incomes")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldUpdateIncomes() {
        Budget budget = budgetRepository.findAll().get(0);
        budget.setInitializationStep(Step.INCOMES);
        budgetRepository.save(budget);
        String budgetId = budgetRepository.findAll().get(0).getId();

        Long incomeId = incomeRepository.findAll()
                .stream()
                .filter(income -> income.getBudget().getId().equals(budgetId))
                .findFirst()
                .orElseThrow()
                .getId();
        incomeRequests.updateIncomes(
                        budgetId,
                        List.of(
                                Income.builder()
                                        .id(incomeId)
                                        .name("test")
                                        .description("test")
                                        .amount(1000.0)
                                        .frequency(
                                                Frequency.builder()
                                                        .period(Period.MONTH)
                                                        .value(1)
                                                        .build()
                                        )
                                        .build()
                        )
                )
                .errors().verify()
                .path("updateIncomes")
                .entityList(Income.class)
                .hasSize(1)
                .satisfies(incomes -> {
                    Income income = incomes.get(0);
                    assertEquals("test", income.getName());
                    assertEquals("test", income.getDescription());
                    assertEquals(1000.0, income.getAmount());
                    assertEquals(Period.MONTH, income.getFrequency().getPeriod());
                    assertEquals(1, income.getFrequency().getValue());
                });

        budget = budgetRepository.findById(budgetId).orElseThrow();
        assertEquals(Step.EXPENSES_CATEGORIES, budget.getInitializationStep());
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        checkUnauthorizedResponse();
    }

    @DisplayName("Should return access denied when")
    @WithMockUser(username = "test2@example.com", authorities = {"USER"})
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "user has no rights to budget",
            "user has no EDIT_INCOMES right, EDIT_INCOMES",
    })
    void shouldReturnAccessDenied(String testCase, String right) {
        User user = userRepository.getUserByEmail("test2@example.com").orElseThrow();
        Budget budget = budgetRepository.findAll().get(0);
        UserRights userRights = UserRights.builder()
                .user(user)
                .budget(budget)
                .right(Right.valueOf(right))
                .build();
        userRightsRepository.saveAndFlush(userRights);
        checkUnauthorizedResponse();
    }

    private void checkUnauthorizedResponse() {
        String budgetId = budgetRepository.findAll().get(0).getId();
        incomeRequests.updateIncomes(
                        budgetId,
                        List.of(
                                Income.builder()
                                        .name("test")
                                        .description("test")
                                        .amount(1000.0)
                                        .frequency(
                                                Frequency.builder()
                                                        .period(Period.MONTH)
                                                        .value(1)
                                                        .build()
                                        )
                                        .build()
                        )
                )
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }
}
