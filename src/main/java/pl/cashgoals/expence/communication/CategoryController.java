package pl.cashgoals.expence.communication;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pl.cashgoals.expence.business.service.CategoryService;
import pl.cashgoals.expence.business.model.CategoryInput;
import pl.cashgoals.expence.persistence.model.Category;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @QueryMapping
    @FullyAuthenticated
    public List<Category> categories(@Argument UUID budgetId) {
        return categoryService.getCategories(budgetId);
    }
    @QueryMapping
    @FullyAuthenticated
    public List<Category> visibleCategories(@Argument UUID budgetId) {
        return categoryService.getVisibleCategories(budgetId);
    }

    @MutationMapping
    @FullyAuthenticated
    public List<Category> updateCategories(@Argument UUID budgetId, @Argument List<CategoryInput> categories) {
        return categoryService.updateCategories(budgetId, categories);
    }

    @MutationMapping
    @FullyAuthenticated
    public Boolean deleteCategories(@Argument UUID budgetId, @Argument List<Long> categoryIds) {
        return categoryService.deleteCategories(budgetId, categoryIds);
    }

    @SchemaMapping(typeName = "Category", field = "parentId")
    public Long parentId(Category category) {
        if (category.getParent() == null) {
            return null;
        }
        return category.getParent().getId();
    }
}
