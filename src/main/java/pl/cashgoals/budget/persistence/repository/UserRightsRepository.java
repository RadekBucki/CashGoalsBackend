package pl.cashgoals.budget.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cashgoals.budget.persistence.model.UserRights;

public interface UserRightsRepository extends JpaRepository<UserRights, Long> {
}
