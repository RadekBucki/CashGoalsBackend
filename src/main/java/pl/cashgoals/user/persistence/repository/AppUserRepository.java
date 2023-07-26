package pl.cashgoals.user.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.cashgoals.user.persistence.model.AppUser;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> getUserByUsername(String username);
    Optional<AppUser> getUserByEmail(String email);
}
