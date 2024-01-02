package pl.cashgoals.expense.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.BudgetFacade;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.expense.business.model.ExpenseInput;
import pl.cashgoals.expense.persistence.model.Expense;
import pl.cashgoals.expense.persistence.repository.CategoryRepository;
import pl.cashgoals.expense.persistence.repository.ExpenseRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetFacade budgetFacade;

    public List<Expense> getExpenses(UUID budgetId, Integer year, Integer month) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.VIEW);
        return expenseRepository.findAllByBudgetIdAndYearAndMonth(budgetId, year, month);
    }
    public Expense updateExpense(UUID budgetId, ExpenseInput expenseInput) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_EXPENSES);
        Expense expense = Expense.builder()
                .id(expenseInput.id())
                .description(expenseInput.description())
                .date(expenseInput.date())
                .amount(expenseInput.amount())
                .category(categoryRepository.getReferenceById(expenseInput.categoryId()))
                .build();
        expenseRepository.save(expense);
        return expenseRepository.findById(expense.getId()).orElseThrow();
    }

    public Boolean deleteExpense(UUID budgetId, Long expenseId) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_EXPENSES);
        expenseRepository.deleteById(expenseId);
        return true;
    }
}
