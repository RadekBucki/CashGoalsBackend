package pl.cashgoals.income.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.BudgetFacade;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.income.business.model.IncomeItemInput;
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

    public List<IncomeItem> getIncomeItems(UUID budgetId, Integer month, Integer year) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.VIEW);
        return incomeItemRepository.findAllByBudgetIdAndMonthAndYear(budgetId, month, year);
    }

    public IncomeItem updateIncomeItem(UUID budgetId, IncomeItemInput incomeItemInput) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_INCOME_ITEMS);
        IncomeItem incomeItem = IncomeItem.builder()
                .id(incomeItemInput.id())
                .income(incomeRepository.getReferenceById(incomeItemInput.incomeId()))
                .name(incomeItemInput.name())
                .description(incomeItemInput.description())
                .amount(incomeItemInput.amount())
                .date(incomeItemInput.date())
                .build();
        incomeItemRepository.save(incomeItem);
        return incomeItemRepository.findById(incomeItem.getId()).orElseThrow();
    }

    public Boolean deleteIncomeItem(UUID budgetId, Long incomeItemId) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_INCOME_ITEMS);
        incomeItemRepository.deleteById(incomeItemId);
        return true;
    }
}
