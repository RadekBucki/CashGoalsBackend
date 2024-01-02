package pl.cashgoals.income.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.cashgoals.income.persistence.model.IncomeItem;

import java.util.List;
import java.util.UUID;

public interface IncomeItemRepository extends JpaRepository<IncomeItem, Long> {
    @Query(
            "SELECT i FROM IncomeItem i " +
                    "WHERE i.income.budgetId = ?1 " +
                    "AND EXTRACT(MONTH FROM i.date) = ?2 " +
                    "AND EXTRACT(YEAR FROM i.date) = ?3"
    )
    List<IncomeItem> findAllByBudgetIdAndMonthAndYear(UUID budgetId, Integer month, Integer year);
}
