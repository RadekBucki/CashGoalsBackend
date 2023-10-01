package pl.cashgoals.integration.user;

import graphql.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.configuration.AbstractIntegrationTest;

import java.util.Objects;

class UpdateUserPasswordTest extends AbstractIntegrationTest {

    @DisplayName("Should update user password")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldUpdateUserPassword() {
        GraphQlTester.Response response = userRequests.updateUserPassword(
                "Test123!",
                "Test1234!"
        );

        response
                .errors().verify()
                .path("updateUserPassword").entity(Boolean.class).satisfies(Assertions::assertTrue);
    }

    @DisplayName("Should return validation error when new password is too simple")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldReturnValidationErrorWhenNewPasswordIsNotTooSimple() {
        GraphQlTester.Response response = userRequests.updateUserPassword(
                "Test123!",
                "test"
        );

        response.errors()
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.ValidationError) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Password.message") &&
                                responseError.getPath().equals("updateUserPassword.newPassword")
                );
    }

    @DisplayName("Should return validation error when old password is incorrect")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"USER"})
    void shouldReturnValidationErrorWhenOldPasswordIsIncorrect() {
        GraphQlTester.Response response = userRequests.updateUserPassword(
                "Test1234!",
                "Test12345!"
        );

        response.errors()
                .expect(responseError ->
                        responseError.getErrorType().equals(org.springframework.graphql.execution.ErrorType.BAD_REQUEST) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.user.bad-password") &&
                                responseError.getPath().equals("updateUserPassword")
                );
    }
}
