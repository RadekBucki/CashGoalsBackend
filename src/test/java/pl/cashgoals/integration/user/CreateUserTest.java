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
                "test1@example.com"
        );

        response
                .errors().verify()
                .path("createUser").entity(User.class).satisfies(user -> {
                    assertEquals("test1", user.getName());
                    assertEquals("test1@example.com", user.getEmail());
                });
    }

    @DisplayName("Should return validation errors")
    @Test
    void shouldReturnValidationErrors() {
        GraphQlTester.Response response = userRequests.createUser(
                "t",
                "test",
                "bad email"
        );

        response.errors()
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.ValidationError) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Size.message") &&
                                responseError.getPath().equals("createUser.input.name")
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

    @DisplayName("Should return error when user with given email already exists")
    @Test
    void shouldReturnErrorWhenUserWithGivenEmailAlreadyExists() {
        GraphQlTester.Response response = userRequests.createUser(
                "test1",
                "Test123!",
                "test@example.com"
        );

        response.errors().expect(responseError ->
                responseError.getErrorType().equals(ErrorType.ValidationError) &&
                        Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.EmailExist.message") &&
                        responseError.getPath().equals("createUser.input.email")
        );
    }
}
