package pl.cashgoals.expense.persistence.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.cashgoals.expense.persistence.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.budgetId = ?1 AND c.parent IS NULL")
    List<Category> findRootCategoriesByBudgetId(UUID budgetId);

    @Query("SELECT c FROM Category c WHERE c.budgetId = ?1 AND c.parent IS NULL AND c.visible = TRUE")
    List<Category> findVisibleRootCategoriesByBudgetId(UUID budgetId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Category c WHERE c.budgetId = ?1 AND c.id IN ?2")
    void deleteCategories(UUID budgetId, List<Long> categoryIds);
}
