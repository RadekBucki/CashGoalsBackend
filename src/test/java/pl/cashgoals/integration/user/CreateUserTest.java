package pl.cashgoals.integration.user;

import graphql.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.user.persistence.model.User;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateUserTest extends AbstractIntegrationTest {

    @DisplayName("Should create user")
    @Test
    void shouldCreateUser() {
        GraphQlTester.Response response = userRequests.createUser(
                "test1",
                "Test123!",
                "test1@example.com",
                "Test",
                "Test"
        );

        response
                .errors().verify()
                .path("createUser").entity(User.class).satisfies(user -> {
                    assertEquals("test1", user.getUsername());
                    assertEquals("Test", user.getFirstname());
                    assertEquals("Test", user.getLastname());
                    assertEquals("test1@example.com", user.getEmail());
                });
    }

    @DisplayName("Should return validation errors")
    @Test
    void shouldReturnValidationErrors() {
        GraphQlTester.Response response = userRequests.createUser(
                "t",
                "test",
                "bad email",
                "T",
                "T"
        );

        response.errors()
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.ValidationError) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Size.message") &&
                                responseError.getPath().equals("createUser.input.username")
                )
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.ValidationError) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Size.message") &&
                                responseError.getPath().equals("createUser.input.firstname")
                )
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.ValidationError) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Size.message") &&
                                responseError.getPath().equals("createUser.input.lastname")
                )
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.ValidationError) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Email.message") &&
                                responseError.getPath().equals("createUser.input.email")
                )
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.ValidationError) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Password.message") &&
                                responseError.getPath().equals("createUser.input.password")
                );
    }

    @DisplayName("Should return error when user with given username already exists")
    @Test
    void shouldReturnErrorWhenUserWithGivenUsernameAlreadyExists() {
        GraphQlTester.Response response = userRequests.createUser(
                "test",
                "Test123!",
                "test1@example.com",
                "Test",
                "Test"
        );

        response.errors().expect(responseError ->
                responseError.getErrorType().equals(ErrorType.ValidationError) &&
                        Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.UsernameExist.message") &&
                        responseError.getPath().equals("createUser.input.username")
        );
    }

    @DisplayName("Should return error when user with given email already exists")
    @Test
    void shouldReturnErrorWhenUserWithGivenEmailAlreadyExists() {
        GraphQlTester.Response response = userRequests.createUser(
                "test1",
                "Test123!",
                "test@example.com",
                "Test",
                "Test"
        );

        response.errors().expect(responseError ->
                responseError.getErrorType().equals(ErrorType.ValidationError) &&
                        Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.EmailExist.message") &&
                        responseError.getPath().equals("createUser.input.email")
        );
    }
}
