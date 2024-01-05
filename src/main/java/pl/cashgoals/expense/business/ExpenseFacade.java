package pl.cashgoals.expense.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.expense.business.service.ExpenseService;
import pl.cashgoals.expense.persistence.model.Expense;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseFacade {
    private final ExpenseService expenseService;

    public List<Expense> getExpenses(UUID budgetId, Integer month, Integer year) {
        return expenseService.findExpenses(budgetId, year, month);
    }
}
