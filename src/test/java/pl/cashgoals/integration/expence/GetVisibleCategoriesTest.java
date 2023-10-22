package pl.cashgoals.integration.expence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.expence.persistence.model.Category;

import java.util.Objects;
import java.util.Optional;

import static graphql.Assert.assertTrue;

class GetVisibleCategoriesTest extends AbstractIntegrationTest {
    @DisplayName("Should return all visible categories")
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    @Test
    void shouldReturnAllVisibleCategories() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        expenceRequests.getVisibleCategories(budgetId)
                .errors().verify()
                .path("categories").entityList(Category.class)
                .hasSize(1)
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
                            .filter(category -> category.getVisible().equals(true))
                            .filter(category -> category.getChildren().isEmpty())
                            .findFirst();
                    assertTrue(test2Category.isPresent());
                });
    }

    @DisplayName("Should return access denied when authorization missed")
    @Test
    void shouldReturnAccessDeniedWhenAuthorizationMissed() {
        String budgetId = budgetRepository.findAll().get(0).getId().toString();
        expenceRequests.getVisibleCategories(budgetId)
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }
}
