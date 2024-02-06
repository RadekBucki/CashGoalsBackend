package pl.cashgoals.goal.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.expense.business.ExpenseFacade;
import pl.cashgoals.expense.persistence.model.Category;
import pl.cashgoals.expense.persistence.model.Expense;
import pl.cashgoals.goal.business.model.GoalResult;
import pl.cashgoals.goal.business.strategies.goal.result.GoalResultStrategyResolver;
import pl.cashgoals.goal.persistence.model.Goal;
import pl.cashgoals.income.business.IncomeFacade;
import pl.cashgoals.income.persistence.model.IncomeItem;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalResultsService {
    private final IncomeFacade incomeFacade;
    private final ExpenseFacade expenseFacade;
    private final GoalService goalService;
    private final GoalResultStrategyResolver goalResultStrategyResolver;

    public List<GoalResult> getGoalResults(UUID budgetId, Integer year, Integer month) {
        List<Goal> goals = goalService.getGoals(budgetId);
        List<Expense> expenses = expenseFacade.getExpenses(budgetId, month, year);
        List<IncomeItem> incomes = incomeFacade.getIncomeItems(budgetId, month, year);

        Double totalIncome = incomes.stream()
                .mapToDouble(IncomeItem::getAmount)
                .sum();

        return goals.stream()
                .map(goal -> {
                    Double goalCategoryExpensesTotal = expenses.stream()
                            .filter(expense -> hasExpenseCategory(goal.getCategory(), expense))
                            .mapToDouble(Expense::getAmount)
                            .sum();

                    return goalResultStrategyResolver
                            .resolve(goal.getType())
                            .calculate(goal, goalCategoryExpensesTotal, totalIncome);
                })
                .toList();
    }

    private static boolean hasExpenseCategory(Category category, Expense expense) {
        boolean hasCategory = false;
        Category expenseCategory = expense.getCategory();
        while (expenseCategory != null && !hasCategory) {
            hasCategory = category.getId().equals(expenseCategory.getId());
            expenseCategory = expenseCategory.getParent();
        }
        return hasCategory;
    }
}
