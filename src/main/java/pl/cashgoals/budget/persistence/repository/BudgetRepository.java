package pl.cashgoals.budget.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cashgoals.budget.persistence.model.Budget;

import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {
}
