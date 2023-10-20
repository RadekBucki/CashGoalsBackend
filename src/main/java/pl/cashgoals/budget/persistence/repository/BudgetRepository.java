package pl.cashgoals.budget.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cashgoals.budget.persistence.model.Budget;

public interface BudgetRepository extends JpaRepository<Budget, String> {
}
