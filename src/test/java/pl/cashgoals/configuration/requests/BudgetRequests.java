package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;
import pl.cashgoals.budget.business.model.UserRightsInput;

import java.util.List;
import java.util.Map;

public class BudgetRequests {
    private final GraphQlTester graphQlTester;

    public BudgetRequests(GraphQlTester graphQlTester) {
        this.graphQlTester = graphQlTester;
    }

    public GraphQlTester.Response createBudget(String name) {
        return graphQlTester.documentName("budget/createBudget")
                .variable("name", name)
                .execute();
    }

    public GraphQlTester.Response getBudget(String id) {
        return graphQlTester.documentName("budget/getBudget")
                .variable("id", id)
                .execute();
    }

    public GraphQlTester.Response getBudgets() {
        return graphQlTester.documentName("budget/getBudgets")
                .execute();
    }

    public GraphQlTester.Response getUserRights(String budgetId) {
        return graphQlTester.documentName("budget/getUserRights")
                .variable("budgetId", budgetId)
                .execute();
    }

    public GraphQlTester.Response updateUsersRights(String budgetId, List<UserRightsInput> userRights) {
        return graphQlTester.documentName("budget/updateUsersRights")
                .variable("budgetId", budgetId)
                .variable(
                        "usersRights",
                        userRights
                                .stream()
                                .map(userRight -> Map.of(
                                        "email", userRight.email(),
                                        "rights", userRight.rights()
                                ))
                                .toList()
                )
                .execute();
    }
}
