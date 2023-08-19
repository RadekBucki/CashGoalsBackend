package pl.cashgoals.unit.notification.business.service.source;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.business.service.source.EmailService;
import pl.cashgoals.notification.business.service.source.SendMessageService;
import pl.cashgoals.notification.business.service.source.SendMessageServicesResolver;
import pl.cashgoals.notification.business.service.source.Source;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SendMessageServicesResolverTest {
    @Mock
    EmailService emailService;

    @InjectMocks
    SendMessageServicesResolver sendMessageServicesResolver;

    @DisplayName("Should return specified service")
    @Test
    void shouldReturnSpecifiedService() {
        List<SendMessageService> resolvedServices = sendMessageServicesResolver.resolve(
                Notification.builder()
                        .source(List.of(Source.EMAIL))
                        .build()
        );

        assertEquals(1, resolvedServices.size());
        assertEquals(emailService, resolvedServices.get(0));
    }

    @DisplayName("Should return multiple services")
    @Test
    void shouldReturnMultipleServices() {
        List<SendMessageService> resolvedServices = sendMessageServicesResolver.resolve(
                Notification.builder()
                        .source(List.of(Source.EMAIL, Source.EMAIL))
                        .build()
        );

        assertEquals(2, resolvedServices.size());
        assertEquals(emailService, resolvedServices.get(0));
        assertEquals(emailService, resolvedServices.get(1));
    }

    @DisplayName("Should return default service when source is empty")
    @Test
    void shouldReturnDefaultServiceWhenSourceIsEmpty() {
        List<SendMessageService> resolvedServices = sendMessageServicesResolver.resolve(
                Notification.builder()
                        .source(List.of())
                        .build()
        );

        assertEquals(1, resolvedServices.size());
        assertEquals(emailService, resolvedServices.get(0));
    }
}
