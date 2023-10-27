package pl.cashgoals.expence.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.BudgetFacade;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.expence.persistence.model.Category;
import pl.cashgoals.expence.persistence.repository.CategoryRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BudgetFacade budgetFacade;

    public List<Category> getCategories(UUID budgetId) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.VIEW);
        return categoryRepository.findRootCategoriesByBudgetId(budgetId);
    }

    public List<Category> getVisibleCategories(UUID budgetId) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.VIEW);
        return categoryRepository.findVisibleRootCategoriesByBudgetId(budgetId);
    }
}
