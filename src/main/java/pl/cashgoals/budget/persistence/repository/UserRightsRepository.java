package pl.cashgoals.budget.persistence.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.UserRight;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;
import java.util.UUID;

public interface UserRightsRepository extends JpaRepository<UserRight, Long> {
    @Query("SELECT ur.right FROM UserRight ur WHERE ur.budget.id = :budgetId AND ur.user.email = :email")
    List<Right> getRights(UUID budgetId, String email);
    @Modifying
    @Transactional
    @Query("DELETE FROM UserRight ur WHERE ur.budget = :budget AND ur.user = :user")
    void deleteRightsByBudgetAndUser(Budget budget, User user);

    @Modifying
    @Transactional
    default List<UserRight> setUserRightsToBudget(Budget budget, User user, List<Right> rights) {
        deleteRightsByBudgetAndUser(budget, user);
        return this.saveAllAndFlush(
                rights.stream()
                        .map(right -> UserRight.builder()
                                .budget(budget)
                                .user(user)
                                .right(right)
                                .build())
                        .toList()
        );
    }
    @Query(
            "SELECT COUNT(ur) = 1 " +
            "FROM UserRight ur " +
            "WHERE ur.budget.id = :budgetId " +
            "AND ur.user.email = :email " +
            "AND ur.right = :right"
    )
    Boolean hasUserRight(UUID budgetId, String email, Right right);
}
