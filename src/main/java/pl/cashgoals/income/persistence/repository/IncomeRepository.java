package pl.cashgoals.income.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cashgoals.income.persistence.model.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {
}
