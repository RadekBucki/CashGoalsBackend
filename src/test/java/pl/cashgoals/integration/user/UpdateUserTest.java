package pl.cashgoals.integration.user;

import graphql.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.user.persistence.model.User;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateUserTest extends AbstractIntegrationTest {

    @DisplayName("Should update user")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"SCOPE_USER"})
    void shouldUpdateUser() {
        GraphQlTester.Response response = userRequests.updateUser(
                "test1",
                "Test123!",
                "test1@example.com"
        );

        response
                .errors().verify()
                .path("updateUser").entity(User.class).satisfies(user -> {
                    assertEquals("test1", user.getName());
                    assertEquals("test1@example.com", user.getEmail());
                });
    }

    @DisplayName("Should return validation errors")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"SCOPE_USER"})
    void shouldReturnValidationErrors() {
        GraphQlTester.Response response = userRequests.updateUser(
                "t",
                "test",
                "bad email"
        );

        response.errors()
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.ValidationError) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Size.message") &&
                                responseError.getPath().equals("updateUser.input.name")
                )
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.ValidationError) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Email.message") &&
                                responseError.getPath().equals("updateUser.input.email")
                )
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.ValidationError) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Password.message") &&
                                responseError.getPath().equals("updateUser.input.password")
                );
    }

    @DisplayName("Should return error when user with given email already exists")
    @Test
    @WithMockUser(username = "test@example.com", authorities = {"SCOPE_USER"})
    void shouldReturnErrorWhenUserWithGivenEmailAlreadyExists() {
        GraphQlTester.Response response = userRequests.updateUser(
                "test1",
                "Test123!",
                "inactive@example.com"
        );

        response.errors().expect(responseError ->
                responseError.getErrorType().equals(ErrorType.ValidationError) &&
                        Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.EmailExist.message") &&
                        responseError.getPath().equals("updateUser.input.email")
        );
    }
    
    @DisplayName("Should return error when user is not logged in")
    @Test
    void shouldReturnErrorWhenUserIsNotLoggedIn() {
        GraphQlTester.Response response = userRequests.updateUser(
                "test1",
                "Test123!",
                "test1@example.com"
        );

        response.errors().expect(responseError ->
                responseError.getErrorType().equals(org.springframework.graphql.execution.ErrorType.UNAUTHORIZED) &&
                        Objects.equals(responseError.getMessage(), "cashgoals.user.unauthorized")
        );
    }
}
