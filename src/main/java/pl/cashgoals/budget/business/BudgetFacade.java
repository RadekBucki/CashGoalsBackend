package pl.cashgoals.budget.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.service.BudgetService;
import pl.cashgoals.budget.business.service.RightValidationService;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetFacade {
    private final RightValidationService rightValidationService;
    private final BudgetService budgetService;

    public void verifyCurrentUserRight(UUID budgetId, Right right) {
        rightValidationService.verifyUserRight(budgetId, right);
    }

    public void updateBudgetInitializationStep(UUID budgetId, Step step) {
        budgetService.updateBudgetInitializationStep(budgetId, step);
    }
}
