package pl.cashgoals.budget.communication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import pl.cashgoals.budget.business.service.BudgetService;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;
import pl.cashgoals.validation.business.annotation.Size;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @MutationMapping
    @FullyAuthenticated
    @Validated
    public Budget createBudget(@Argument @Size(min = 1, max = 100) String name, Principal principal) {
        return budgetService.createBudget(name, principal);
    }

    @SchemaMapping(typeName = "Budget" , field = "rights")
    public List<Right> id(Budget budget, Principal principal) {
        return budgetService.getGetCurrentUserRightsFromBudget(budget, principal);
    }
}
