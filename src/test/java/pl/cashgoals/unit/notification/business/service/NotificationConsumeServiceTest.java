package pl.cashgoals.unit.notification.business.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cashgoals.notification.business.model.Notification;
import pl.cashgoals.notification.business.service.NotificationConsumeService;
import pl.cashgoals.notification.business.service.source.SendMessageService;
import pl.cashgoals.notification.business.service.source.SendMessageServicesResolver;
import pl.cashgoals.user.business.UserFacade;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumeServiceTest {
    @Mock
    SendMessageServicesResolver sendMessageServicesResolver;
    @Mock
    UserFacade userFacade = mock(UserFacade.class);
    SendMessageService sendMessageService = mock(SendMessageService.class);

    @InjectMocks
    NotificationConsumeService notificationConsumeService;

    @Captor
    ArgumentCaptor<Notification> notificationArgumentCaptor;

    @DisplayName("Should consume notification")
    @Test
    void shouldConsumeNotification() {
        when(userFacade.getUserByUsername("username"))
                .thenReturn(new User());
        when(sendMessageServicesResolver.resolve(any()))
                .thenReturn(List.of(sendMessageService));
        Notification notification = Notification.builder()
                .username("username")
                .build();

        notificationConsumeService.consume(notification);

        verify(sendMessageServicesResolver, times(1))
                .resolve(notificationArgumentCaptor.capture());
        verify(sendMessageService, times(1))
                .send(notificationArgumentCaptor.capture());

        Notification receivedNotification = notificationArgumentCaptor.getValue();
        assertNotNull(receivedNotification.getUser());
    }

    @DisplayName("Should call message service 2 times")
    @Test
    void shouldCallMessageService2Times() {
        when(userFacade.getUserByUsername("username"))
                .thenReturn(new User());
        when(sendMessageServicesResolver.resolve(any()))
                .thenReturn(List.of(sendMessageService, sendMessageService));
        Notification notification = Notification.builder()
                .username("username")
                .build();

        notificationConsumeService.consume(notification);

        verify(sendMessageServicesResolver, times(1))
                .resolve(notificationArgumentCaptor.capture());
        verify(sendMessageService, times(2))
                .send(notificationArgumentCaptor.capture());

        Notification receivedNotification = notificationArgumentCaptor.getValue();
        assertNotNull(receivedNotification.getUser());
    }

}
