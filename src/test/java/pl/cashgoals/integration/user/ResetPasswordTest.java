package pl.cashgoals.integration.user;

import graphql.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.user.persistence.model.TokenType;
import pl.cashgoals.user.persistence.model.User;
import pl.cashgoals.user.persistence.model.UserToken;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ResetPasswordTest extends AbstractIntegrationTest {
    @DisplayName("Should reset password")
    @Test
    void shouldResetPassword() {
        User user = userRepository.getUserWithTokensByEmail("test@example.com")
                .orElseThrow();
        user.getTokens()
                .add(
                        UserToken.builder()
                                .token("token")
                                .type(TokenType.RESET_PASSWORD)
                                .user(user)
                                .build()
                );
        userRepository.save(user);

        userRequests.resetPassword("test@example.com", "token", "Test123@")
                .errors().verify()
                .path("resetPassword")
                .entity(Boolean.class)
                .satisfies(Assertions::assertTrue);

        user = userRepository.getUserWithTokensByEmail("test@example.com")
                .orElseThrow();

        assertTrue(passwordEncoder.matches("Test123@", user.getPassword()));
        assertTrue(
                user.getTokens()
                        .stream()
                        .noneMatch(userToken -> userToken.getType() == TokenType.RESET_PASSWORD)
        );
    }

    @DisplayName("Should not reset password when token is invalid")
    @Test
    void shouldNotResetPasswordWhenTokenIsInvalid() {
        userRequests.resetPassword("test@example.com", "token", "Test123@")
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getErrorType(), org.springframework.graphql.execution.ErrorType.BAD_REQUEST)
                                && Objects.equals(responseError.getMessage(), "cashgoals.user.bad-reset-password-token")
                );

        User user = userRepository.getUserByEmail("test@example.com")
                .orElseThrow();

        assertFalse(passwordEncoder.matches("Test123@", user.getPassword()));
    }

    @DisplayName("Should return validation errors when password is invalid")
    @Test
    void shouldReturnValidationErrorsWhenEmailAndPasswordAreInvalid() {
        userRequests.resetPassword("test@example.com", "token", "tosimplepassword")
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getErrorType(), ErrorType.ValidationError)
                                && Objects.equals(responseError.getMessage(), "cashgoals.validation.constraints.Password.message")
                );
    }
}
