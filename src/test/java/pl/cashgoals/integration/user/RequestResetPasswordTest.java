package pl.cashgoals.integration.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.cashgoals.configuration.AbstractIntegrationTest;

import java.util.Objects;

class RequestResetPasswordTest extends AbstractIntegrationTest {
    @DisplayName("Should sent reset password email")
    @Test
    void shouldResetPassword() {
        userRequests.requestPasswordReset("test@example.com", "http://some-web.com/reset")
                .errors().verify()
                .path("requestPasswordReset")
                .entity(Boolean.class)
                .satisfies(Assertions::assertTrue);
    }

    @ParameterizedTest(name = "Test case: {index} - Email: {0}, Expected Error: {2}")
    @CsvSource({
            "notexistingemail@example.com, http://some-web.com/reset, cashgoals.user.not-found, NOT_FOUND",
            "inactive@example.com, http://some-web.com/reset, cashgoals.user.not-found, NOT_FOUND",
            "test@example.com, not-url, cashgoals.validation.constraints.URL.message, ValidationError"
    })
    void shouldReturnError(String email, String resetUrl, String expectedErrorMessage, String errorType) {
        userRequests.requestPasswordReset(email, resetUrl)
                .errors()
                .expect(responseError ->
                        Objects.equals(expectedErrorMessage, responseError.getMessage())
                                && Objects.equals(errorType, responseError.getErrorType().toString())
                );
    }

}
