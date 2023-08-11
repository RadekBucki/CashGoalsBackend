package pl.cashgoals.integration.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.cashgoals.configuration.AbstractIntegrationTest;

import javax.mail.internet.MimeMessage;

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

        greenMail.waitForIncomingEmail(1);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        greenMail.reset();
    }

    @DisplayName("Should not sent reset password email when user does not exist")
    @Test
    void shouldNotResetPasswordWhenUserDoesNotExist() {
        userRequests.requestPasswordReset("notexistingemail@example.com")
                .errors().verify()
                .path("requestPasswordReset")
                .entity(String.class)
                .satisfies(response -> assertEquals("cashgoals.user.password-reset-requested", response));

        greenMail.waitForIncomingEmail(0);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(0, receivedMessages.length);
        greenMail.reset();
    }

    @DisplayName("Should not sent reset password email when user is inactive")
    @Test
    void shouldNotResetPasswordWhenUserIsInactive() {
        userRequests.requestPasswordReset("nactive@example.com")
                .errors().verify()
                .path("requestPasswordReset")
                .entity(String.class)
                .satisfies(response -> assertEquals("cashgoals.user.password-reset-requested-inactive", response));

        greenMail.waitForIncomingEmail(0);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(0, receivedMessages.length);
        greenMail.reset();
    }

    @DisplayName("Should return validation errors when email is invalid")
    @Test
    void shouldReturnValidationErrorsWhenEmailIsInvalid() {
        userRequests.requestPasswordReset("invalidemail")
                .errors().verify()
                .path("requestPasswordReset")
                .entity(String.class)
                .satisfies(response -> assertEquals("cashgoals.validation.constraints.Email.message", response));

        greenMail.waitForIncomingEmail(0);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(0, receivedMessages.length);
        greenMail.reset();
    }

}
