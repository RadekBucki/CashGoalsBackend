package pl.cashgoals.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.user.persistence.model.Theme;
import pl.cashgoals.user.persistence.model.User;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetUserTest extends AbstractIntegrationTest {
    @DisplayName("Should get user")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldGetUser() {
        userRequests.getUser()
                .errors().verify()
                .path("user").entity(User.class).satisfies(user -> {
                    assertEquals("test", user.getName());
                    assertEquals("test@example.com", user.getEmail());
                    assertEquals(Theme.DARK, user.getTheme());
                    assertEquals("en", user.getLocale().getLanguage());
                });
    }

    @DisplayName("Should return access denied")
    @Test
    void shouldReturnAccessDenied() {
        userRequests.getUser()
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
                                && responseError.getErrorType().equals(ErrorType.UNAUTHORIZED)
                )
                .verify();
    }
}
