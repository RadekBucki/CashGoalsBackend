package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;
import pl.cashgoals.income.business.model.IncomeItemInput;
import pl.cashgoals.income.persistence.model.Income;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeRequests {
    private final GraphQlTester graphQlTester;

    public IncomeRequests(GraphQlTester graphQlTester) {
        this.graphQlTester = graphQlTester;
    }

    public GraphQlTester.Response getIncomes(String budgetId) {
        return graphQlTester.documentName("income/getIncomes")
                .variable("budgetId", budgetId)
                .execute();
    }

    public GraphQlTester.Response updateIncomes(String budgetId, List<Income> incomes) {
        return graphQlTester.documentName("income/updateIncomes")
                .variable("budgetId", budgetId)
                .variable(
                        "incomes",
                        incomes.stream()
                                .map(income -> {
                                    Map<String, Object> incomeMap = new HashMap<>(Map.of(
                                            "name", income.getName()
                                    ));
                                    if (income.getId() != null) {
                                        incomeMap.put("id", income.getId());
                                    }
                                    if (income.getDescription() != null) {
                                        incomeMap.put("description", income.getDescription());
                                    }
                                    return incomeMap;
                                })
                                .toList()
                )
                .execute();
    }

    public GraphQlTester.Response deleteIncomes(String budgetId, List<Long> incomeIds) {
        return graphQlTester.documentName("income/deleteIncomes")
                .variable("budgetId", budgetId)
                .variable("incomeIds", incomeIds)
                .execute();
    }

    public GraphQlTester.Response getIncomeItems(String budgetId, int month, int year) {
        return graphQlTester.documentName("income/item/getIncomeItems")
                .variable("budgetId", budgetId)
                .variable("month", month)
                .variable("year", year)
                .execute();
    }

    public GraphQlTester.Response updateIncomeItem(String budgetId, IncomeItemInput incomeItem) {
        Map<String, Object> incomeItemMap = new HashMap<>(Map.of(
                "description", incomeItem.description(),
                "amount", incomeItem.amount(),
                "date", incomeItem.date()
        ));
        if (incomeItem.id() != null) {
            incomeItemMap.put("id", incomeItem.id());
        }
        if (incomeItem.incomeId() != null) {
            incomeItemMap.put("incomeId", incomeItem.incomeId());
        }
        return graphQlTester.documentName("income/item/updateIncomeItem")
                .variable("budgetId", budgetId)
                .variable("incomeItem", incomeItemMap)
                .execute();
    }

    public GraphQlTester.Response deleteIncomeItem(String budgetId, Long incomeItemId) {
        return graphQlTester.documentName("income/item/deleteIncomeItem")
                .variable("budgetId", budgetId)
                .variable("incomeItemId", incomeItemId)
                .execute();
    }
}
