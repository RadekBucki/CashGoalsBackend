package pl.cashgoals.expense.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cashgoals.expense.persistence.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
