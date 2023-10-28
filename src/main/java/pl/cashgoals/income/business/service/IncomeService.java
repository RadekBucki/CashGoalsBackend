package pl.cashgoals.income.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.BudgetFacade;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.income.persistence.model.Income;
import pl.cashgoals.income.persistence.repository.IncomeRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final BudgetFacade budgetFacade;
    public List<Income> getIncomes(UUID budgetId) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.VIEW);
        return incomeRepository.findAllByBudgetId(budgetId);
    }

    public List<Income> updateIncomes(UUID budgetId, List<Income> incomes) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_INCOMES);
        incomes.forEach(income -> income.setBudgetId(budgetId));
        incomeRepository.saveAll(incomes);
        budgetFacade.updateBudgetInitializationStep(budgetId, Step.EXPENSES_CATEGORIES);
        return incomeRepository.findAllByBudgetId(budgetId);
    }
}
