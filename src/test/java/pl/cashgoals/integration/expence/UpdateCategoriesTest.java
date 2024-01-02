package pl.cashgoals.integration.expence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.budget.persistence.model.UserRight;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.expense.business.model.CategoryInput;
import pl.cashgoals.expense.persistence.model.Category;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static graphql.Assert.assertTrue;

class UpdateCategoriesTest extends AbstractIntegrationTest {
    @DisplayName("Should update category")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldUpdateCategory() {
        Budget budget = budgetRepository.findAll().get(0);
        String budgetId = budget.getId().toString();
        budget.setInitializationStep(Step.EXPENSES_CATEGORIES);
        budgetRepository.saveAndFlush(budget);

        Long categoryId = categoryRepository.findAll()
                .stream()
                .filter(category -> category.getBudgetId().toString().equals(budgetId))
                .filter(category -> category.getName().equals("test"))
                .findFirst()
                .orElseThrow()
                .getId();
        expenceRequests.updateCategories(
                        budgetId,
                        List.of(
                                new CategoryInput(
                                        categoryId,
                                        null,
                                        "test",
                                        "test",
                                        false,
                                        List.of()
                                )
                        )
                )
                .errors().verify()
                .path("updateCategories").entityList(Category.class)
                .hasSize(2)
                .satisfies(categories -> {
                    Optional<Category> testCategory = categories.stream()
                            .filter(category -> category.getName().equals("test"))
                            .filter(category -> category.getDescription().equals("test"))
                            .filter(category -> category.getVisible().equals(false))
                            .filter(category -> category.getChildren().size() == 1)
                            .findFirst();
                    assertTrue(testCategory.isPresent());
                    Optional<Category> test2Category = testCategory.get().getChildren().stream()
                            .filter(category -> category.getName().equals("test2"))
                            .filter(category -> category.getDescription().equals("test2"))
                            .filter(category -> category.getVisible().equals(true))
                            .filter(category -> category.getChildren().isEmpty())
                            .findFirst();
                    assertTrue(test2Category.isPresent());
                    Optional<Category> test3Category = categories.stream()
                            .filter(category -> category.getName().equals("unvisible"))
                            .filter(category -> category.getDescription().equals("unvisible"))
                            .filter(category -> category.getVisible().equals(false))
                            .filter(category -> category.getChildren().isEmpty())
                            .findFirst();
                    assertTrue(test3Category.isPresent());
                });

        budget = budgetRepository.findById(budget.getId()).orElseThrow();
        assertTrue(budget.getInitializationStep().equals(Step.GOALS));
    }

    @DisplayName("Should add new category as child of existing category")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldAddNewCategoryAsChildOfExistingCategory() {
        Budget budget = budgetRepository.findAll().get(0);
        String budgetId = budget.getId().toString();
        budget.setInitializationStep(Step.EXPENSES_CATEGORIES);
        budgetRepository.saveAndFlush(budget);

        Long categoryId = categoryRepository.findAll()
                .stream()
                .filter(category -> category.getBudgetId().toString().equals(budgetId))
                .filter(category -> category.getName().equals("test"))
                .findFirst()
                .orElseThrow()
                .getId();
        expenceRequests.updateCategories(
                        budgetId,
                        List.of(
                                new CategoryInput(
                                        null,
                                        categoryId,
                                        "test3",
                                        "test3",
                                        true,
                                        List.of()
                                )
                        )
                )
                .errors().verify()
                .path("updateCategories").entityList(Category.class)
                .hasSize(2)
                .satisfies(categories -> {
                    Optional<Category> testCategory = categories.stream()
                            .filter(category -> category.getName().equals("test"))
                            .filter(category -> category.getDescription().equals("test"))
                            .filter(category -> category.getVisible().equals(true))
                            .filter(category -> category.getChildren().size() == 2)
                            .findFirst();
                    assertTrue(testCategory.isPresent());
                    Optional<Category> test2Category = testCategory.get().getChildren().stream()
                            .filter(category -> category.getName().equals("test2"))
                            .filter(category -> category.getDescription().equals("test2"))
                            .filter(category -> category.getVisible().equals(true))
                            .filter(category -> category.getChildren().isEmpty())
                            .findFirst();
                    assertTrue(test2Category.isPresent());
                    Optional<Category> test3Category = testCategory.get().getChildren().stream()
                            .filter(category -> category.getName().equals("test3"))
                            .filter(category -> category.getDescription().equals("test3"))
                            .filter(category -> category.getVisible().equals(true))
                            .filter(category -> category.getChildren().isEmpty())
                            .findFirst();
                    assertTrue(test3Category.isPresent());
                    Optional<Category> test4Category = categories.stream()
                            .filter(category -> category.getName().equals("unvisible"))
                            .filter(category -> category.getDescription().equals("unvisible"))
                            .filter(category -> category.getVisible().equals(false))
                            .filter(category -> category.getChildren().
                                    isEmpty())
                            .findFirst();
                    assertTrue(test4Category.isPresent());
                });
    }

    @DisplayName("Should add new category as child of new category")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldAddNewCategoryAsChildOfNewCategory() {
        Budget budget = budgetRepository.findAll().get(0);
        String budgetId = budget.getId().toString();
        budget.setInitializationStep(Step.EXPENSES_CATEGORIES);
        budgetRepository.saveAndFlush(budget);

        Long categoryId = categoryRepository.findAll()
                .stream()
                .filter(category -> category.getBudgetId().toString().equals(budgetId))
                .filter(category -> category.getName().equals("test"))
                .findFirst()
                .orElseThrow()
                .getId();
        expenceRequests.updateCategories(
                        budgetId,
                        List.of(
                                new CategoryInput(
                                        null,
                                        categoryId,
                                        "test3",
                                        "test3",
                                        true,
                                        List.of(
                                                new CategoryInput(
                                                        null,
                                                        null,
                                                        "test4",
                                                        "test4",
                                                        true,
                                                        List.of()
                                                )
                                        )
                                )
                        )
                )
                .errors().verify()
                .path("updateCategories").entityList(Category.class)
                .hasSize(2)
                .satisfies(categories -> {
                    Optional<Category> testCategory = categories.stream()
                            .filter(category -> category.getName().equals("test"))
                            .filter(category -> category.getDescription().equals("test"))
                            .filter(category -> category.getVisible().equals(true))
                            .filter(category -> category.getChildren().size() == 2)
                            .findFirst();
                    assertTrue(testCategory.isPresent());
                    Optional<Category> test2Category = testCategory.get().getChildren().stream()
                            .filter(category -> category.getName().equals("test2"))
                            .filter(category -> category.getDescription().equals("test2"))
                            .filter(category -> category.getVisible().equals(true))
                            .filter(category -> category.getChildren().isEmpty())
                            .findFirst();
                    assertTrue(test2Category.isPresent());
                    Optional<Category> test3Category = testCategory.get().getChildren().stream()
                            .filter(category -> category.getName().equals("test3"))
                            .filter(category -> category.getDescription().equals("test3"))
                            .filter(category -> category.getVisible().equals(true))
                            .filter(category -> category.getChildren().size() == 1)
                            .findFirst();
                    assertTrue(test3Category.isPresent());
                    Optional<Category> test4Category = test3Category.get().getChildren().stream()
                            .filter(category -> category.getName().equals("test4"))
                            .filter(category -> category.getDescription().equals("test4"))
                            .filter(category -> category.getVisible().equals(true))
                            .filter(category -> category.getChildren().isEmpty())
                            .findFirst();
                    assertTrue(test4Category.isPresent());
                });
    }


    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        Long categoryId = categoryRepository.findAll()
                .stream()
                .filter(category -> category.getBudgetId().toString().equals(budgetId))
                .filter(category -> category.getName().equals("test"))
                .findFirst()
                .orElseThrow()
                .getId();
        expenceRequests.updateCategories(
                        budgetId,
                        List.of(
                                new CategoryInput(
                                        categoryId,
                                        null,
                                        "test",
                                        "test",
                                        false,
                                        List.of()
                                )
                        )
                )
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }

    @DisplayName("Should return access denied when")
    @WithMockUser(username = "test2@example.com", authorities = {"USER"})
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "user has no rights to budget, ",
            "user has no EDIT_CATEGORIES right to budget, EDIT_EXPENSES",
    })
    void shouldReturnAccessDenied(String testCase, String right) {
        User user = userRepository.getUserByEmail("test2@example.com").orElseThrow();
        Budget budget = budgetRepository.findAll().get(0);
        if (right != null) {
            UserRight userRight = UserRight.builder()
                    .user(user)
                    .budgetId(budget.getId())
                    .right(Right.valueOf(right))
                    .build();
            userRightsRepository.saveAndFlush(userRight);
        }

        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        Long categoryId = categoryRepository.findAll()
                .stream()
                .filter(category -> category.getBudgetId().toString().equals(budgetId))
                .filter(category -> category.getName().equals("test"))
                .findFirst()
                .orElseThrow()
                .getId();
        expenceRequests.updateCategories(
                        budgetId,
                        List.of(
                                new CategoryInput(
                                        categoryId,
                                        null,
                                        "test",
                                        "test",
                                        false,
                                        List.of()
                                )
                        )
                )
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.budget.not-found")
                                && responseError.getErrorType().equals(ErrorType.NOT_FOUND)
                )
                .verify();
    }
}
