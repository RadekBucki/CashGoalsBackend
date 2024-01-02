package pl.cashgoals.expense.communication;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.cashgoals.expense.business.model.ExpenseInput;
import pl.cashgoals.expense.business.service.ExpenseService;
import pl.cashgoals.expense.persistence.model.Expense;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @QueryMapping
    @FullyAuthenticated
    public List<Expense> expenses(@Argument UUID budgetId, @Argument Integer year, @Argument Integer month) {
        return expenseService.getExpenses(budgetId, year, month);
    }

    @MutationMapping
    @FullyAuthenticated
    public Expense updateExpense(@Argument UUID budgetId, @Argument ExpenseInput expense) {
        return expenseService.updateExpense(budgetId, expense);
    }

    @MutationMapping
    @FullyAuthenticated
    public Boolean deleteExpense(@Argument UUID budgetId, @Argument Long expenseId) {
        return expenseService.deleteExpense(budgetId, expenseId);
    }
}
