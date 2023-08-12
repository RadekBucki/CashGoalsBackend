package pl.cashgoals.integration.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.cashgoals.configuration.AbstractIntegrationTest;

import javax.mail.internet.MimeMessage;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestResetPasswordTest extends AbstractIntegrationTest {
    @DisplayName("Should sent reset password email")
    @Test
    void shouldResetPassword() {
        userRequests.requestPasswordReset("test@example.com")
                .errors().verify()
                .path("requestPasswordReset")
                .entity(Boolean.class)
                .satisfies(Assertions::assertTrue);
    }

    @ParameterizedTest(name = "Test case: {index} - Email: {0}, Expected Error: {1}")
    @CsvSource({
            "notexistingemail@example.com, cashgoals.user.not-found, NOT_FOUND",
            "nactive@example.com, cashgoals.user.not-found, NOT_FOUND"
    })
    void shouldReturnError(String email, String expectedErrorMessage, String errorType) {
        userRequests.requestPasswordReset(email)
                .errors()
                .expect(responseError ->
                        Objects.equals(expectedErrorMessage, responseError.getMessage())
                                && Objects.equals(errorType, responseError.getErrorType().toString())
                )
                .verify();

        greenMail.waitForIncomingEmail(0);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(0, receivedMessages.length);
        greenMail.reset();
    }

}
