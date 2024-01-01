package pl.cashgoals.budget.persistence.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;

import java.util.List;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    @Query("SELECT b FROM Budget b LEFT JOIN b.userRights ur WHERE ur.right = :right AND ur.user.email = :email")
    @EntityGraph(attributePaths = {"userRights", "userRights.user"})
    List<Budget> findAllByUserEmailAndRight(String email, Right right);
}
