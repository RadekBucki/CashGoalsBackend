package pl.cashgoals.user.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.cashgoals.user.persistence.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);

    @Query("SELECT u FROM user_entity u LEFT JOIN FETCH u.tokens WHERE u.email = :email")
    Optional<User> getUserWithTokensByEmail(String email);
}
