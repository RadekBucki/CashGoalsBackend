package pl.cashgoals.income.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.cashgoals.income.persistence.model.Income;

import java.util.List;
import java.util.UUID;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    @Query("SELECT i FROM Income i WHERE i.budgetId = :budgetId")
    List<Income> findAllByBudgetId(UUID budgetId);
}
