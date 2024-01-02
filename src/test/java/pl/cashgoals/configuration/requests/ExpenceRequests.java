package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;
import pl.cashgoals.expense.business.model.CategoryInput;
import pl.cashgoals.expense.business.model.ExpenseInput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenceRequests {
    private final GraphQlTester graphQlTester;

    public ExpenceRequests(GraphQlTester graphQlTester) {
        this.graphQlTester = graphQlTester;
    }

    public GraphQlTester.Response getCategories(String budgetId) {
        return graphQlTester.documentName("expence/category/getCategories")
                .variable("budgetId", budgetId)
                .execute();
    }

    public GraphQlTester.Response getVisibleCategories(String budgetId) {
        return graphQlTester.documentName("expence/category/getVisibleCategories")
                .variable("budgetId", budgetId)
                .execute();
    }

    public GraphQlTester.Response updateCategories(String budgetId, List<CategoryInput> categories) {
        return graphQlTester.documentName("expence/category/updateCategories")
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
        return graphQlTester.documentName("expence/category/deleteCategories")
                .variable("budgetId", budgetId)
                .variable("categoryIds", categoryIds)
                .execute();
    }

    public GraphQlTester.Response getExpenses(String budgetId) {
        return graphQlTester.documentName("expence/expense/getExpenses")
                .variable("budgetId", budgetId)
                .execute();
    }

    public GraphQlTester.Response updateExpense(String budgetId, ExpenseInput expense) {
        Map<String, Object> expenseMap = new HashMap<>(Map.of(
                "amount", expense.amount(),
                "date", expense.date(),
                "categoryId", expense.categoryId()
        ));
        if (expense.id() != null) {
            expenseMap.put("id", expense.id());
        }
        if (expense.description() != null) {
            expenseMap.put("description", expense.description());
        }
        return graphQlTester.documentName("expence/expense/updateExpense")
                .variable("budgetId", budgetId)
                .variable("expense", expenseMap)
                .execute();
    }

    public GraphQlTester.Response deleteExpense(String budgetId, Long expenseId) {
        return graphQlTester.documentName("expence/expense/deleteExpense")
                .variable("budgetId", budgetId)
                .variable("expenseId", expenseId)
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
