package pl.cashgoals.user.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.cashgoals.user.persistence.model.UserToken;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
}
