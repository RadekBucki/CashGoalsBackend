package pl.cashgoals.integration.budget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.UserRights;
import pl.cashgoals.configuration.AbstractIntegrationTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetUserRightsTest extends AbstractIntegrationTest {
    @DisplayName("Should return user rights")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldReturnUserRights() {
        String budgetId = budgetRepository.findAll().get(0).getId();
        budgetRequests.getUserRights(budgetId)
                .errors().verify()
                .path("userRights").entityList(UserRights.class).satisfies(userRights -> {
                    assertEquals(1, userRights.size());
                    assertEquals("test", userRights.get(0).getUser().getName());
                    assertEquals("test@example.com", userRights.get(0).getUser().getEmail());
                    assertEquals(Right.OWNER, userRights.get(0).getRight());
                });
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId();
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
        String budgetId = budgetRepository.findAll().get(0).getId();
        budgetRequests.getUserRights(budgetId)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }
}
