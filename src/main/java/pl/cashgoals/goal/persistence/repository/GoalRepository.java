package pl.cashgoals.goal.persistence.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.cashgoals.goal.persistence.model.Goal;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    @Query("SELECT g FROM Goal g WHERE g.budgetId = :budgetId")
    @EntityGraph(attributePaths = {"category"})
    List<Goal> findAllByBudgetId(UUID budgetId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Goal g WHERE g.budgetId = :budgetId AND g.id IN :goalIds")
    void deleteGoals(UUID budgetId, List<Long> goalIds);
}
