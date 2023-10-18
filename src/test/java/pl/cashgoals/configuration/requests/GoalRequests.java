package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;
import pl.cashgoals.goal.persistence.model.Goal;

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

    public GraphQlTester.Response updateGoals(String budgetId, List<Goal> goals) {
        return graphQlTester.documentName("goal/updateGoals")
                .operationName("updateGoals")
                .variable("budgetId", budgetId)
                .variable(
                        "goals",
                        goals.stream()
                                .map(goal -> {
                                    Map<String, Object> goalMap = new HashMap<>(Map.of(
                                            "name", goal.getName(),
                                            "type", goal.getType().name(),
                                            "value", goal.getValue(),
                                            "category", goal.getCategory().getId()
                                    ));
                                    if (goal.getId() != null) {
                                        goalMap.put("id", goal.getId());
                                    }
                                    if (goal.getDescription() != null) {
                                        goalMap.put("description", goal.getDescription());
                                    }
                                    return goalMap;
                                })
                                .toList()
                )
                .execute();
    }
}
