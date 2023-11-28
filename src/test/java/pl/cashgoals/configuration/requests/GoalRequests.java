package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;
import pl.cashgoals.goal.business.model.GoalInput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalRequests {
    private final GraphQlTester graphQlTester;

    public GoalRequests(GraphQlTester graphQlTester) {
        this.graphQlTester = graphQlTester;
    }

    public GraphQlTester.Response getGoals(String budgetId) {
        return graphQlTester.documentName("goal/getGoals")
                .operationName("getGoals")
                .variable("budgetId", budgetId)
                .execute();
    }

    public GraphQlTester.Response updateGoals(String budgetId, List<GoalInput> goals) {
        return graphQlTester.documentName("goal/updateGoals")
                .operationName("updateGoals")
                .variable("budgetId", budgetId)
                .variable(
                        "goals",
                        goals.stream()
                                .map(goal -> {
                                    Map<String, Object> goalMap = new HashMap<>(Map.of(
                                            "name", goal.name(),
                                            "type", goal.type().name(),
                                            "value", goal.value(),
                                            "category", goal.category()
                                    ));
                                    if (goal.id() != null) {
                                        goalMap.put("id", goal.id());
                                    }
                                    if (goal.description() != null) {
                                        goalMap.put("description", goal.description());
                                    }
                                    return goalMap;
                                })
                                .toList()
                )
                .execute();
    }

    public GraphQlTester.Response deleteGoals(String budgetId, List<Long> goalIds) {
        return graphQlTester.documentName("goal/deleteGoals")
                .operationName("deleteGoals")
                .variable("budgetId", budgetId)
                .variable("goalIds", goalIds)
                .execute();
    }
}
