package pl.cashgoals.expence.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.BudgetFacade;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
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

    public List<Category> updateCategories(UUID budgetId, List<Category> categories) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_CATEGORIES);
        categories.forEach(category -> category.setBudgetId(budgetId));
        categoryRepository.saveAllAndFlush(categories);
        budgetFacade.updateBudgetInitializationStep(budgetId, Step.GOALS);
        return getCategories(budgetId);
    }

    public Boolean deleteCategories(UUID budgetId, List<Long> categoryIds) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_CATEGORIES);
        categoryRepository.deleteCategories(budgetId, categoryIds);
        return true;
    }
}
