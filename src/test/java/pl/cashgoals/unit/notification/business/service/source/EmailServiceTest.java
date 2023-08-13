package pl.cashgoals.unit.notification.business.service.source;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.business.model.Template;
import pl.cashgoals.notification.business.service.source.EmailService;
import pl.cashgoals.user.persistence.model.User;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    JavaMailSender javaMailSender;
    @Mock
    SpringTemplateEngine templateEngine;
    @InjectMocks
    EmailService emailService;

    Notification notification = Notification.builder()
            .locale(Locale.ROOT)
            .template(Template.ACTIVATION)
            .user(
                    User.builder()
                            .email("email")
                            .build()
            )
            .variables(Map.of())
            .build();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "sender", "sender");
    }

    @DisplayName("Sending email should successfully send the email")
    @Test
    void shouldSendEmailSuccessfully() {
        when(javaMailSender.createMimeMessage())
                .thenReturn(mock(MimeMessage.class));
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("Email body");

        emailService.send(notification);

        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @DisplayName("Sending email with template engine exception should log error and throw exception")
    @Test
    void shouldLogErrorAndThrowExceptionOnTemplateEngineException() {
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenThrow(new RuntimeException("Template engine exception"));

        assertThrows(AmqpException.class, () -> emailService.send(notification));
    }
}
