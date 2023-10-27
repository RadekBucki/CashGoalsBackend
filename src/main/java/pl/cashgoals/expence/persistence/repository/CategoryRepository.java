package pl.cashgoals.expence.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.cashgoals.expence.persistence.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.budgetId = ?1 AND c.parent IS NULL")
    List<Category> findRootCategoriesByBudgetId(UUID budgetId);

    @Query("SELECT c FROM Category c WHERE c.budgetId = ?1 AND c.parent IS NULL AND c.visible = TRUE")
    List<Category> findVisibleRootCategoriesByBudgetId(UUID budgetId);
}
