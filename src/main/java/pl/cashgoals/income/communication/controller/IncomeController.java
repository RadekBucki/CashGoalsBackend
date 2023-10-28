package pl.cashgoals.income.communication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.cashgoals.income.business.service.IncomeService;
import pl.cashgoals.income.persistence.model.Income;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class IncomeController {
    private final IncomeService incomeService;

    @QueryMapping
    @FullyAuthenticated
    public List<Income> incomes(@Argument UUID budgetId) {
        return incomeService.getIncomes(budgetId);
    }

    @MutationMapping
    @FullyAuthenticated
    public List<Income> updateIncomes(@Argument UUID budgetId, @Argument List<Income> incomes) {
        return incomeService.updateIncomes(budgetId, incomes);
    }
}
