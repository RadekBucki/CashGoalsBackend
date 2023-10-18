package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;
import pl.cashgoals.expence.persistence.model.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenceRequests {
    private final GraphQlTester graphQlTester;

    public ExpenceRequests(GraphQlTester graphQlTester) {
        this.graphQlTester = graphQlTester;
    }

    public GraphQlTester.Response getCategories(String budgetId) {
        return graphQlTester.documentName("expence/getCategories")
                .variable("budgetId", budgetId)
                .execute();
    }

    public GraphQlTester.Response getVisibleCategories(String budgetId) {
        return graphQlTester.documentName("expence/getVisibleCategories")
                .variable("budgetId", budgetId)
                .execute();
    }

    public GraphQlTester.Response updateCategories(String budgetId, List<Category> categories) {
        return graphQlTester.documentName("expence/updateCategories")
                .variable("budgetId", budgetId)
                .variable(
                        "categories",
                        categories.stream()
                                .map(category -> {
                                    Map<String, Object> categoryMap = new HashMap<>(Map.of(
                                            "name", category.getName(),
                                            "description", category.getDescription(),
                                            "visible", category.getVisible()
                                    ));
                                    if (category.getId() != null) {
                                        categoryMap.put("id", category.getId());
                                    }
                                    if (category.getParent() != null) {
                                        categoryMap.put("parent", category.getParent().getId());
                                    }
                                    return categoryMap;
                                })
                                .toList()
                )
                .execute();
    }
}
