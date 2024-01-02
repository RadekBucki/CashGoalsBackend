package pl.cashgoals.expense.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.BudgetFacade;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.expense.business.model.CategoryInput;
import pl.cashgoals.expense.persistence.model.Category;
import pl.cashgoals.expense.persistence.repository.CategoryRepository;

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

    public List<Category> updateCategories(UUID budgetId, List<CategoryInput> categoryInputs) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_CATEGORIES);
        List<Category> categories = categoryInputs.stream()
                .map(categoryInput -> mapCategoryInputToCategory(categoryInput, budgetId))
                .toList();
        categoryRepository.saveAllAndFlush(categories);
        budgetFacade.updateBudgetInitializationStep(budgetId, Step.GOALS);
        return getCategories(budgetId);
    }

    public Boolean deleteCategories(UUID budgetId, List<Long> categoryIds) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_CATEGORIES);
        categoryRepository.deleteCategories(budgetId, categoryIds);
        return true;
    }

    private Category mapCategoryInputToCategory(CategoryInput categoryInput, UUID budgetId) {
        Category category = Category.builder()
                .id(categoryInput.id())
                .name(categoryInput.name())
                .description(categoryInput.description())
                .children(
                        categoryInput.children()
                                .stream()
                                .map(child -> mapCategoryInputToCategory(child, budgetId))
                                .toList()
                )
                .visible(categoryInput.visible())
                .budgetId(budgetId)
                .build();

        if (categoryInput.parentId() != null) {
            category.setParent(categoryRepository.getReferenceById(categoryInput.parentId()));
        }
        category.getChildren().forEach(child -> child.setParent(category));

        return category;
    }
}
