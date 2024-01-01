package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;
import pl.cashgoals.expence.business.model.CategoryInput;

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

    public GraphQlTester.Response updateCategories(String budgetId, List<CategoryInput> categories) {
        return graphQlTester.documentName("expence/updateCategories")
                .variable("budgetId", budgetId)
                .variable(
                        "categories",
                        categories.stream()
                                .map(this::mapCategoryInputToMap)
                                .toList()
                )
                .execute();
    }

    public GraphQlTester.Response deleteCategories(String budgetId, List<Long> categoryIds) {
        return graphQlTester.documentName("expence/deleteCategories")
                .variable("budgetId", budgetId)
                .variable("categoryIds", categoryIds)
                .execute();
    }

    private Map<String, Object> mapCategoryInputToMap(CategoryInput categoryInput) {
        Map<String, Object> categoryMap = new HashMap<>(Map.of(
                "name", categoryInput.name(),
                "visible", categoryInput.visible(),
                "children", categoryInput.children()
                        .stream()
                        .map(this::mapCategoryInputToMap)
                        .toList()
        ));
        if (categoryInput.id() != null) {
            categoryMap.put("id", categoryInput.id());
        }
        if (categoryInput.description() != null) {
            categoryMap.put("description", categoryInput.description());
        }
        if (categoryInput.parentId() != null) {
            categoryMap.put("parentId", categoryInput.parentId());
        }
        return categoryMap;
    }
}
