package pl.cashgoals.income.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.income.business.service.IncomeItemService;
import pl.cashgoals.income.persistence.model.IncomeItem;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncomeFacade {
    private final IncomeItemService incomeItemService;

    public List<IncomeItem> getIncomeItems(UUID budgetId, Integer month, Integer year) {
        return incomeItemService.findIncomeItems(budgetId, month, year);
    }
}
