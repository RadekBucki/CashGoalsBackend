package pl.cashgoals.income.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cashgoals.income.persistence.model.IncomeItem;

public interface IncomeItemRepository extends JpaRepository<IncomeItem, Long> {
}
