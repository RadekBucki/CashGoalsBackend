package pl.cashgoals.integration.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.user.persistence.model.TokenType;
import pl.cashgoals.user.persistence.model.User;
import pl.cashgoals.user.persistence.model.UserToken;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActivateUserTest extends AbstractIntegrationTest {
    @DisplayName("Should activate user")
    @Test
    void shouldActivateUser() {
        User user = userRepository.getUserWithTokensByEmail("inactive@example.com").orElseThrow();
        assertFalse(user.isEnabled());

        String token = user.getTokens().stream()
                .filter(userToken -> userToken.getType().equals(TokenType.ACTIVATION))
                .findFirst()
                .map(UserToken::getToken)
                .orElseThrow();

        userRequests.activateUser(user.getEmail(), token)
                .errors().verify()
                .path("activateUser")
                .entity(Boolean.class)
                .satisfies(Assertions::assertTrue);

        user = userRepository.getUserWithTokensByEmail("inactive@example.com").orElseThrow();

        assertTrue(user.isEnabled());

        Optional<UserToken> tokenAfterActivation = user.getTokens().stream()
                .filter(userToken -> userToken.getType().equals(TokenType.ACTIVATION))
                .filter(userToken -> userToken.getToken().equals(token))
                .findFirst();
        assertTrue(tokenAfterActivation.isEmpty());
    }

    @DisplayName("Should not activate user when token is invalid")
    @Test
    void shouldNotActivateUserWhenTokenIsInvalid() {
        userRequests.activateUser("inactive@example.com", "invalidToken")
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.bad-activation-token")
                                && Objects.equals(responseError.getErrorType(), ErrorType.BAD_REQUEST)
                )
                .verify();
    }

    @DisplayName("Should not activate user when user is already activated")
    @Test
    void shouldNotActivateUserWhenUserIsAlreadyActivated() {
        User user = userRepository.getUserByEmail("test@example.com").orElseThrow();

        userRequests.activateUser(user.getEmail(), "token")
                .errors()
                .expect(responseError ->
                        Objects.equals(responseError.getMessage(), "cashgoals.user.already-activated")
                                && Objects.equals(responseError.getErrorType(), ErrorType.BAD_REQUEST)
                )
                .verify();
    }
}