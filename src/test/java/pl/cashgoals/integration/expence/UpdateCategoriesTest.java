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
import pl.cashgoals.expence.persistence.model.Category;
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
                .filter(category -> category.getBudgetId().equals(budgetId))
                .filter(category -> category.getName().equals("test"))
                .findFirst()
                .orElseThrow()
                .getId();
        expenceRequests.updateCategories(
                        budgetId,
                        List.of(
                                Category.builder()
                                        .id(categoryId)
                                        .name("test")
                                        .description("test")
                                        .visible(false)
                                        .build()
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
                            .filter(category -> category.getChildren().size() == 1)
                            .findFirst();
                    assertTrue(testCategory.isPresent());
                    Optional<Category> test2Category = testCategory.get().getChildren().stream()
                            .filter(category -> category.getName().equals("test2"))
                            .filter(category -> category.getDescription().equals("test2"))
                            .filter(category -> category.getVisible().equals(false))
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

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        Long categoryId = categoryRepository.findAll()
                .stream()
                .filter(category -> category.getBudgetId().equals(budgetId))
                .filter(category -> category.getName().equals("test"))
                .findFirst()
                .orElseThrow()
                .getId();
        expenceRequests.updateCategories(
                        budgetId,
                        List.of(
                                Category.builder()
                                        .id(categoryId)
                                        .name("test")
                                        .description("test")
                                        .visible(false)
                                        .build()
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
            "user has no rights to budget",
            "user has no EDIT_EXPENSES right, EDIT_EXPENSES",
    })
    void shouldReturnAccessDenied(String testCase, String right) {
        User user = userRepository.getUserByEmail("test2@example.com").orElseThrow();
        Budget budget = budgetRepository.findAll().get(0);
        UserRight userRight = UserRight.builder()
                .user(user)
                .budget(budget)
                .right(Right.valueOf(right))
                .build();
        userRightsRepository.saveAndFlush(userRight);

        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        Long categoryId = categoryRepository.findAll()
                .stream()
                .filter(category -> category.getBudgetId().equals(budgetId))
                .filter(category -> category.getName().equals("test"))
                .findFirst()
                .orElseThrow()
                .getId();
        expenceRequests.updateCategories(
                        budgetId,
                        List.of(
                                Category.builder()
                                        .id(categoryId)
                                        .name("test")
                                        .description("test")
                                        .visible(false)
                                        .build()
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
