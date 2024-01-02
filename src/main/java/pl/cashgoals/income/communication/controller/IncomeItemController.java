package pl.cashgoals.income.communication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.cashgoals.income.business.model.IncomeItemInput;
import pl.cashgoals.income.business.service.IncomeItemService;
import pl.cashgoals.income.persistence.model.IncomeItem;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class IncomeItemController {
    private final IncomeItemService incomeItemService;

    @QueryMapping
    @FullyAuthenticated
    public List<IncomeItem> incomeItems(@Argument UUID budgetId, @Argument Integer month, @Argument Integer year) {
        return incomeItemService.getIncomeItems(budgetId, month, year);
    }

    @MutationMapping
    @FullyAuthenticated
    public IncomeItem updateIncomeItem(@Argument UUID budgetId, @Argument IncomeItemInput incomeItem) {
        return incomeItemService.updateIncomeItem(budgetId, incomeItem);
    }

    @MutationMapping
    @FullyAuthenticated
    public Boolean deleteIncomeItem(@Argument UUID budgetId, @Argument Long incomeItemId) {
        return incomeItemService.deleteIncomeItem(budgetId, incomeItemId);
    }
}
