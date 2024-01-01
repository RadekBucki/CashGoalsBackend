package pl.cashgoals.income.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.BudgetFacade;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.income.persistence.model.Income;
import pl.cashgoals.income.persistence.model.IncomeItem;
import pl.cashgoals.income.persistence.repository.IncomeItemRepository;
import pl.cashgoals.income.persistence.repository.IncomeRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncomeItemService {
    private final IncomeRepository incomeRepository;
    private final IncomeItemRepository incomeItemRepository;
    private final BudgetFacade budgetFacade;

    public List<Income> getIncomeItems(UUID budgetId, Integer month, Integer year) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.VIEW);
        return incomeRepository.findAllByBudgetIdAndMonthAndYear(budgetId, month, year);
    }

    public IncomeItem updateIncomeItem(UUID budgetId, IncomeItem incomeItem) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_INCOME_ITEMS);
        incomeItem.setBudgetId(budgetId);
        return incomeItemRepository.save(incomeItem);
    }

    public Boolean deleteIncomeItem(UUID budgetId, Long incomeItemId) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_INCOME_ITEMS);
        incomeItemRepository.deleteById(incomeItemId);
        return true;
    }
}
