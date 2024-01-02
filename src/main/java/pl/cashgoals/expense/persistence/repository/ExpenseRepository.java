package pl.cashgoals.expense.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.cashgoals.expense.persistence.model.Expense;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query(
            "SELECT e FROM Expense e " +
                    "WHERE e.category.budgetId = :budgetId " +
                    "AND EXTRACT(MONTH FROM e.date) = :month " +
                    "AND EXTRACT(YEAR FROM e.date) = :year"
    )
    List<Expense> findAllByBudgetIdAndYearAndMonth(UUID budgetId, Integer year, Integer month);
}
