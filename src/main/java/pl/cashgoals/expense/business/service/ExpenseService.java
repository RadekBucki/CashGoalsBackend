package pl.cashgoals.expense.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.BudgetFacade;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.expense.business.model.ExpenseInput;
import pl.cashgoals.expense.persistence.model.Category;
import pl.cashgoals.expense.persistence.model.Expense;
import pl.cashgoals.expense.persistence.repository.CategoryRepository;
import pl.cashgoals.expense.persistence.repository.ExpenseRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetFacade budgetFacade;

    public List<Expense> findExpenses(UUID budgetId, Integer year, Integer month) {
        return expenseRepository.findAllByBudgetIdAndYearAndMonth(budgetId, year, month);
    }
    public List<Expense> getExpenses(UUID budgetId, Integer year, Integer month) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.VIEW);
        return findExpenses(budgetId, year, month);
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

    public String getCategories(Expense expense) {
        List<Category> categories = new ArrayList<>();
        Category currentCategory = expense.getCategory();

        while (currentCategory != null) {
            categories.add(currentCategory);
            currentCategory = currentCategory.getParent();
        }

        Collections.reverse(categories);
        return categories.stream()
                .map(Category::getName)
                .reduce((a, b) -> a + " > " + b)
                .orElse("");
    }
}
