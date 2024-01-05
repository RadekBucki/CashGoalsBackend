package pl.cashgoals.integration.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.goal.persistence.model.Goal;
import pl.cashgoals.goal.persistence.model.GoalType;

import java.util.Objects;
import java.util.Optional;

import static graphql.Assert.assertTrue;

class GetGoalsTest extends AbstractIntegrationTest {
    @DisplayName("Should return all goals")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldReturnAllGoals() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        goalRequests.getGoals(budgetId)
                .errors().verify()
                .path("goals")
                .entityList(Goal.class)
                .hasSize(1)
                .satisfies(goals -> {
                    Optional<Goal> goal = goals.stream()
                            .filter(g -> g.getName().equals("test"))
                            .filter(g -> g.getDescription().equals("test"))
                            .filter(g -> g.getType().equals(GoalType.PERCENTAGE))
                            .filter(g -> g.getMax().equals(50.0))
                            .filter(g -> g.getCategory().getName().equals("test"))
                            .findFirst();
                    assertTrue(goal.isPresent());
                });
    }

    @DisplayName("Should return access denied when user is not authorized")
    @Test
    void shouldReturnAccessDeniedWhenUserIsNotAuthorized() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        goalRequests.getGoals(budgetId)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }
}
