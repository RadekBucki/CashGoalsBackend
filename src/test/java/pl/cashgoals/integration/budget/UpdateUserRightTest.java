package pl.cashgoals.integration.budget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.budget.business.model.UserRightsInput;
import pl.cashgoals.budget.business.model.UserRightsOutput;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.budget.persistence.model.UserRight;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static graphql.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateUserRightTest extends AbstractIntegrationTest {
    @DisplayName("Should update user rights")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldUpdateUserRights() {
        Budget budget = budgetRepository.findAll().get(0);
        budget.setInitializationStep(Step.USERS_AND_RIGHTS);
        budgetRepository.saveAndFlush(budget);
        String budgetId = budget.getId().toString();
        budgetRequests.updateUsersRights(
                        budgetId,
                        List.of(new UserRightsInput("test2@example.com", List.of(Right.EDIT_EXPENSES)))
                )
                .errors().verify()
                .path("updateUsersRights").entityList(UserRightsOutput.class).satisfies(userRights -> {
                    Optional<UserRightsOutput> userRightsOptional = userRights.stream()
                            .filter(userRights1 -> userRights1.user().getEmail().equals("test2@example.com"))
                            .filter(userRights1 -> userRights1.rights().equals(List.of(Right.EDIT_EXPENSES)))
                            .findFirst();
                    assertTrue(userRightsOptional.isPresent());
                });
        budget = budgetRepository.findById(budget.getId()).orElseThrow();
        assertEquals(Step.FINISHED, budget.getInitializationStep());
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        budgetRequests.updateUsersRights(
                        budgetId,
                        List.of(new UserRightsInput("test2@example.com", List.of(Right.EDIT_EXPENSES)))
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
            "user has no EDIT_USERS_AND_RIGHTS right, EDIT_EXPENSES",
    })
    void shouldReturnAccessDenied(String testCase, String right) {
        User user = userRepository.getUserByEmail("test2@example.com").orElseThrow();
        Budget budget = budgetRepository.findAll().get(0);
        if (right != null) {
            UserRight userRight = UserRight.builder()
                    .user(user)
                    .budget(budget)
                    .right(Right.valueOf(right))
                    .build();
            userRightsRepository.saveAndFlush(userRight);
        }

        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        budgetRequests.updateUsersRights(
                        budgetId,
                        List.of(new UserRightsInput("test2@example.com", List.of(Right.EDIT_EXPENSES)))
                )
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }
}
