package pl.cashgoals.income.persistence.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.cashgoals.income.persistence.model.Income;

import java.util.List;
import java.util.UUID;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    @Query("SELECT i FROM Income i WHERE i.budgetId = :budgetId")
    List<Income> findAllByBudgetId(UUID budgetId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Income i WHERE i.budgetId = :budgetId AND i.id IN :incomeIds")
    void deleteIncomes(UUID budgetId, List<Long> incomeIds);

    @Query(
            "SELECT DISTINCT i FROM Income i JOIN FETCH i.incomeItems ii " +
                    "WHERE i.budgetId = :budgetId " +
                    "AND EXTRACT(MONTH FROM ii.date) = :month " +
                    "AND EXTRACT(YEAR FROM ii.date) = :year"
    )
    List<Income> findAllByBudgetIdAndMonthAndYear(UUID budgetId, Integer month, Integer year);
}
