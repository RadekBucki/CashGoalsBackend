package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;
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
                                            "name", income.getName(),
                                            "amount", income.getAmount()
                                    ));
                                    if (income.getId() != null) {
                                        incomeMap.put("id", income.getId());
                                    }
                                    if (income.getDescription() != null) {
                                        incomeMap.put("description", income.getDescription());
                                    }
                                    if (income.getFrequency() != null) {
                                        incomeMap.put(
                                                "frequency",
                                                Map.of(
                                                        "period", income.getFrequency().getPeriod(),
                                                        "value", income.getFrequency().getValue()
                                                )
                                        );
                                    }
                                    return incomeMap;
                                })
                                .toList()
                )
                .execute();
    }
}
