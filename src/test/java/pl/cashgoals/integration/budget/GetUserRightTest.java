package pl.cashgoals.integration.budget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.budget.business.model.UserRightsOutput;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.configuration.AbstractIntegrationTest;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetUserRightTest extends AbstractIntegrationTest {
    @DisplayName("Should return user rights")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldReturnUserRights() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        budgetRequests.getUserRights(budgetId)
                .errors().verify()
                .path("usersRights").entityList(UserRightsOutput.class).satisfies(userRights -> {
                    assertEquals(1, userRights.size());
                    assertEquals("test", userRights.get(0).user().getName());
                    assertEquals("test@example.com", userRights.get(0).user().getEmail());
                    assertEquals(List.of(Right.values()), userRights.get(0).rights());
                });
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        budgetRequests.getUserRights(budgetId)
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
        budgetRequests.getUserRights(budgetId)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }
}
