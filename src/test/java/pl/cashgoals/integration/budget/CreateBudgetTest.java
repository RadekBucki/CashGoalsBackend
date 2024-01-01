package pl.cashgoals.integration.budget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.configuration.AbstractIntegrationTest;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateBudgetTest extends AbstractIntegrationTest {
    @DisplayName("Should create budget")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldCreateBudget() {
        budgetRequests.createBudget("test_budget")
                .errors().verify()
                .path("createBudget").entity(Budget.class).satisfies(budget -> {
                    assertNotNull(budget.getId());
                    assertEquals("test_budget", budget.getName());
                    assertEquals(Step.INCOMES, budget.getInitializationStep());
                })
                .path("createBudget.rights")
                .entityList(Right.class)
                .containsExactly(Right.values());
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

    public static Stream<Arguments> validationTestCasesDataProvider() {
        return Stream.of(
                Arguments.of("Should return validation error when name is empty", ""),
                Arguments.of("Should return validation error when name is too long", "a".repeat(101))
        );
    }

    @DisplayName("Should return validation error")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @MethodSource("validationTestCasesDataProvider")
    @ParameterizedTest(name = "{0}")
    void shouldReturnValidationError(String testCase, String name) {
        budgetRequests.createBudget(name)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Size.message")
                                && responseError.getErrorType().equals(graphql.ErrorType.ValidationError)
                );
    }
}
