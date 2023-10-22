package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;
import pl.cashgoals.budget.persistence.model.UserRight;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public GraphQlTester.Response updateUserRights(String budgetId, List<UserRight> userRights) {

        return graphQlTester.documentName("budget/updateUserRights")
                .variable("budgetId", budgetId)
                .variable(
                        "usersRights",
                        userRights.stream()
                                .collect(Collectors.groupingBy(
                                        userRight -> userRight.getUser().getEmail(),
                                        Collectors.mapping(userRight -> userRight.getRight().toString(), Collectors.toList())
                                ))
                                .entrySet()
                                .stream()
                                .map(entry -> Map.of(
                                        "email", entry.getKey(),
                                        "rights", entry.getValue()
                                ))
                                .collect(Collectors.toList())
                )
                .execute();
    }
}
