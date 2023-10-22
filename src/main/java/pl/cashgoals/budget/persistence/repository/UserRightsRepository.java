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

public interface UserRightsRepository extends JpaRepository<UserRight, Long> {
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
}
