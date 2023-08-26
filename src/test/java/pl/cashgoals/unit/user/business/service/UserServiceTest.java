package pl.cashgoals.unit.user.business.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import pl.cashgoals.notification.business.NotificationFacade;
import pl.cashgoals.user.business.exception.BadRefreshTokenException;
import pl.cashgoals.user.business.exception.UserNotFoundException;
import pl.cashgoals.user.business.model.LoginOutput;
import pl.cashgoals.user.business.model.UserInput;
import pl.cashgoals.user.business.service.TokenService;
import pl.cashgoals.user.business.service.UserService;
import pl.cashgoals.user.persistence.model.TokenType;
import pl.cashgoals.user.persistence.model.User;
import pl.cashgoals.user.persistence.model.UserToken;
import pl.cashgoals.user.persistence.repository.UserRepository;
import pl.cashgoals.utils.graphql.business.exception.GraphQLBadRequestException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    TokenService tokenService;
    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    @SuppressWarnings("unused")
    NotificationFacade notificationFacade;

    @InjectMocks
    UserService userService;

    @DisplayName("Load user by email")
    @Nested
    class LoadUserByEmailTest {
        @DisplayName("Should load user by email")
        @Test
        void shouldLoadUserByEmail() {
            when(userRepository.getUserByEmail(anyString()))
                    .thenReturn(Optional.of(
                            User.builder()
                                    .email("test@example.com")
                                    .build()
                    ));

            UserDetails username = userService.loadUserByUsername("test@example.com");

            assertEquals("test@example.com", username.getUsername());
        }

        @DisplayName("Should throw exception when user not found")
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.getUserByEmail(anyString()))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.loadUserByUsername("test@example.com")
            );
        }
    }

    @DisplayName("Get user by email")
    @Nested
    class GetUserByEmailTest {
        @DisplayName("Should get user by email")
        @Test
        void shouldGetUserByEmail() {
            when(userRepository.getUserByEmail(anyString()))
                    .thenReturn(Optional.of(
                            User.builder()
                                    .email("test@example.com")
                                    .build()
                    ));

            User username = userService.getUserByEmail("test@example.com");

            assertEquals("test@example.com", username.getEmail());
        }

        @DisplayName("Should throw exception when user not found")
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.getUserByEmail(anyString()))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.getUserByEmail("test@example.com")
            );
        }
    }

    @DisplayName("Should create user")
    @Test
    void shouldCreateUser() {
        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");
        when(tokenService.generateRandomCode())
                .thenReturn("code");

        User username = userService.createUser(new UserInput(
                "name",
                "Qwerty123!",
                "example@example.com",
                "http://some-url.com"
        ));

        assertEquals("name", username.getName());
    }

    @DisplayName("Login user")
    @Nested
    class LoginUserTest {
        @DisplayName("Should login user")
        @Test
        void shouldLoginUser() {
            User user = User.builder()
                    .name("name")
                    .password("password")
                    .enabled(true)
                    .build();
            when(userRepository.getUserByEmail(anyString()))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString()))
                    .thenReturn(true);
            when(tokenService.generateAccessToken(user))
                    .thenReturn("access");
            when(tokenService.generateRefreshToken(user, "access"))
                    .thenReturn("refresh");

            LoginOutput loginOutput = userService.login("name", "password");

            assertEquals("name", loginOutput.user().getName());
            assertEquals("access", loginOutput.accessToken());
            assertEquals("refresh", loginOutput.refreshToken());

        }

        @DisplayName("Should throw exception when user not found")
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.getUserByEmail(anyString()))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.login("name", "password")
            );
        }

        @DisplayName("Should throw exception when password is incorrect")
        @Test
        void shouldThrowExceptionWhenPasswordIsIncorrect() {
            User user = User.builder()
                    .name("name")
                    .password("password")
                    .enabled(true)
                    .build();
            when(userRepository.getUserByEmail(anyString()))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString()))
                    .thenReturn(false);

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.login("name", "password")
            );
        }

        @DisplayName("Should throw exception when user is not enabled")
        @Test
        void shouldThrowExceptionWhenUserIsNotEnabled() {
            User user = User.builder()
                    .name("name")
                    .password("password")
                    .enabled(false)
                    .build();
            when(userRepository.getUserByEmail(anyString()))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString()))
                    .thenReturn(true);

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.login("name", "password")
            );
        }
    }

    @DisplayName("Update user")
    @Nested
    class UpdateUserTest {
        Principal principal = mock(Principal.class);

        @DisplayName("Should update user")
        @Test
        void shouldUpdateUser() {
            UserInput userInput = new UserInput(
                    "username2",
                    "password2",
                    "email2",
                    "activationUrl"
            );

            User user = User.builder()
                    .name("name")
                    .password("password")
                    .name("test@example.com")
                    .enabled(true)
                    .build();

            when(userRepository.getUserByEmail(anyString()))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.encode(anyString()))
                    .thenReturn("encoded");
            when(principal.getName())
                    .thenReturn("name");
            when(userRepository.saveAndFlush(user))
                    .thenReturn(user);

            User updatedUser = userService.updateUser(userInput, principal);

            assertEquals("username2", updatedUser.getName());
            assertEquals("encoded", updatedUser.getPassword());
            assertEquals("email2", updatedUser.getEmail());
        }

        @DisplayName("Should throw exception when user not found")
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            UserInput userInput = new UserInput(
                    "username2",
                    "password2",
                    "email2",
                    "activationUrl"
            );
            when(userRepository.getUserByEmail(anyString()))
                    .thenReturn(Optional.empty());
            when(principal.getName())
                    .thenReturn("name");

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.updateUser(userInput, principal)
            );

        }
    }

    @DisplayName("Refresh token")
    @Nested
    class RefreshTokenTest {
        JwtAuthenticationToken principal = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);

        @DisplayName("Should refresh token")
        @Test
        void shouldRefreshToken() {
            User user = new User();
            when(jwt.getTokenValue())
                    .thenReturn("token");
            when(principal.getToken())
                    .thenReturn(jwt);
            when(principal.getName())
                    .thenReturn("name");
            when(tokenService.verifyRefreshToken("refreshToken", "token"))
                    .thenReturn(true);
            when(userRepository.getUserByEmail("name"))
                    .thenReturn(Optional.of(user));
            when(tokenService.generateAccessToken(user))
                    .thenReturn("access");
            when(tokenService.generateRefreshToken(user, "access"))
                    .thenReturn("refresh");

            LoginOutput loginOutput = userService.refreshToken("refreshToken", principal);

            assertEquals("access", loginOutput.accessToken());
            assertEquals("refresh", loginOutput.refreshToken());
        }

        @DisplayName("Should throw exception when refresh token is incorrect")
        @Test
        void shouldThrowExceptionWhenRefreshTokenIsIncorrect() {
            when(jwt.getTokenValue())
                    .thenReturn("token");
            when(principal.getToken())
                    .thenReturn(jwt);
            when(principal.getName())
                    .thenReturn("name");
            when(tokenService.verifyRefreshToken("refreshToken", "token"))
                    .thenReturn(false);

            assertThrows(
                    BadRefreshTokenException.class,
                    () -> userService.refreshToken("refreshToken", principal)
            );
        }

        @DisplayName("Should throw exception when user not found")
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(jwt.getTokenValue())
                    .thenReturn("token");
            when(principal.getToken())
                    .thenReturn(jwt);
            when(principal.getName())
                    .thenReturn("name");
            when(principal.getName())
                    .thenReturn("name");
            when(tokenService.verifyRefreshToken("refreshToken", "token"))
                    .thenReturn(true);
            when(userRepository.getUserByEmail("name"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.refreshToken("refreshToken", principal)
            );
        }
    }

    @DisplayName("Activate user")
    @Nested
    class ActivateUserTest {
        @DisplayName("Should activate user")
        @Test
        void shouldActivateUser() {
            User user = User.builder()
                    .name("name")
                    .password("password")
                    .name("test@example.com")
                    .enabled(false)
                    .tokens(new ArrayList<>(List.of(
                            UserToken.builder()
                                    .token("token")
                                    .type(TokenType.ACTIVATION)
                                    .build()
                    )))
                    .build();

            when(userRepository.getUserWithTokensByEmail(anyString()))
                    .thenReturn(Optional.of(user));

            Boolean result = userService.activateUser("token", "test@example.com");

            assertTrue(result);
            assertTrue(user.isEnabled());
        }

        @DisplayName("Should throw exception when user not found")
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.getUserWithTokensByEmail(anyString()))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.activateUser("token", "test@example.com")
            );
        }

        @DisplayName("Should throw exception when token is incorrect")
        @Test
        void shouldThrowExceptionWhenTokenIsIncorrect() {
            User user = User.builder()
                    .name("name")
                    .password("password")
                    .name("test@example.com")
                    .enabled(false)
                    .tokens(new ArrayList<>(List.of(
                            UserToken.builder()
                                    .token("token")
                                    .type(TokenType.ACTIVATION)
                                    .build()
                    )))
                    .build();

            when(userRepository.getUserWithTokensByEmail(anyString()))
                    .thenReturn(Optional.of(user));

            assertThrows(
                    GraphQLBadRequestException.class,
                    () -> userService.activateUser("token2", "test@example.com")
            );
        }

        @DisplayName("Should throw exception when user is already enabled")
        @Test
        void shouldThrowExceptionWhenUserIsAlreadyEnabled() {
            User user = User.builder()
                    .name("name")
                    .password("password")
                    .name("test@example.com")
                    .enabled(true)
                    .tokens(new ArrayList<>(List.of(
                            UserToken.builder()
                                    .token("token")
                                    .type(TokenType.ACTIVATION)
                                    .build()
                    )))
                    .build();

            when(userRepository.getUserWithTokensByEmail(anyString()))
                    .thenReturn(Optional.of(user));

            assertThrows(
                    GraphQLBadRequestException.class,
                    () -> userService.activateUser("token", "test@example.com")
            );
        }
    }

    @DisplayName("Request password reset")
    @Nested
    class RequestPasswordResetTest {
        @DisplayName("Should request password reset")
        @Test
        void shouldRequestPasswordReset() {
            User user = User.builder()
                    .name("name")
                    .password("password")
                    .name("test@example.com")
                    .enabled(true)
                    .build();

            when(userRepository.getActiveUserByEmail(anyString()))
                    .thenReturn(Optional.of(user));
            when(tokenService.generateRandomCode())
                    .thenReturn("code");
            when(userRepository.saveAndFlush(user))
                    .thenReturn(user);

            Boolean result = userService.requestPasswordReset("test@example.com", "resetUrl");

            assertTrue(result);
            assertEquals("code", user.getTokens().get(0).getToken());
        }

        @DisplayName("Should throw exception when user not found")
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.getActiveUserByEmail(anyString()))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.requestPasswordReset("test@example.com", "resetUrl")
            );
        }
    }

    @DisplayName("Reset password")
    @Nested
    class ResetPasswordTest {
        @DisplayName("Should reset password")
        @Test
        void shouldResetPassword() {
            User user = User.builder()
                    .name("name")
                    .password("password")
                    .name("test@example.com")
                    .enabled(true)
                    .tokens(new ArrayList<>(List.of(
                            UserToken.builder()
                                    .token("token")
                                    .type(TokenType.RESET_PASSWORD)
                                    .build()
                    )))
                    .build();

            when(userRepository.getActiveUserByEmail(anyString()))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.encode(anyString()))
                    .thenReturn("encoded");
            when(userRepository.saveAndFlush(user))
                    .thenReturn(user);

            Boolean result = userService.resetPassword("test@example.com", "token", "password");

            assertTrue(result);
            assertEquals("encoded", user.getPassword());
        }

        @DisplayName("Should throw exception when user not found")
        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.getActiveUserByEmail(anyString()))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UserNotFoundException.class,
                    () -> userService.resetPassword("test@example.com", "token", "password")
            );
        }

        @DisplayName("Should throw exception when token is incorrect")
        @Test
        void shouldThrowExceptionWhenTokenIsIncorrect() {
            User user = User.builder()
                    .name("name")
                    .password("password")
                    .name("test@example.com")
                    .enabled(true)
                    .tokens(new ArrayList<>(List.of(
                            UserToken.builder()
                                    .token("token")
                                    .type(TokenType.RESET_PASSWORD)
                                    .build()
                    )))
                    .build();

            when(userRepository.getActiveUserByEmail(anyString()))
                    .thenReturn(Optional.of(user));

            assertThrows(
                    GraphQLBadRequestException.class,
                    () -> userService.resetPassword("test@example.com", "token2", "password")
            );
        }
    }
}
