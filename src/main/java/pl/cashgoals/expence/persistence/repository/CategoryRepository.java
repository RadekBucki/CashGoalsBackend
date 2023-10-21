package pl.cashgoals.expence.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cashgoals.expence.persistence.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
