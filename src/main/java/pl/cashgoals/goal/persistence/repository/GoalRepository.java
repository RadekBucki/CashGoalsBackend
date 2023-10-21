package pl.cashgoals.goal.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cashgoals.goal.persistence.model.Goal;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
