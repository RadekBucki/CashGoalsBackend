package pl.cashgoals.budget.communication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import pl.cashgoals.budget.business.model.UserRightsInput;
import pl.cashgoals.budget.business.model.UserRightsOutput;
import pl.cashgoals.budget.business.service.BudgetService;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;
import pl.cashgoals.validation.business.annotation.Size;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

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

    @QueryMapping
    @FullyAuthenticated
    public Budget budget(@Argument UUID id) {
        return budgetService.getBudget(id);
    }

    @QueryMapping
    @FullyAuthenticated
    public List<Budget> budgets(Principal principal) {
        return budgetService.getBudgets(principal);
    }

    @SchemaMapping(typeName = "Budget" , field = "rights")
    public List<Right> id(Budget budget, Principal principal) {
        return budgetService.getGetCurrentUserRightsFromBudget(budget, principal);
    }

    @QueryMapping
    @FullyAuthenticated
    public List<UserRightsOutput> usersRights(@Argument UUID budgetId) {
        return budgetService.getUserRights(budgetId);
    }

    @MutationMapping
    @FullyAuthenticated
    public List<UserRightsOutput> updateUsersRights(
            @Argument UUID budgetId,
            @Argument List<UserRightsInput> usersRights
    ) {
        return budgetService.updateUserRights(budgetId, usersRights);
    }
}
