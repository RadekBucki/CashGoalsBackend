package pl.cashgoals.unit.user.business.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.cashgoals.user.business.annotation.UniqueUsername;
import pl.cashgoals.user.business.validator.UniqueUsernameValidator;
import pl.cashgoals.user.persistence.model.User;
import pl.cashgoals.user.persistence.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UniqueUsernameValidatorTest {
    UniqueUsername uniqueUsername = mock(UniqueUsername.class);
    ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);
    UserRepository userRepository = mock(UserRepository.class);
    User user = new User();
    UniqueUsernameValidator uniqueUsernameValidator = new UniqueUsernameValidator(userRepository);
    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        user.setUsername("example");
        uniqueUsernameValidator.initialize(uniqueUsername);
    }

    @DisplayName("Should return true when username email is unique")
    @Test
    void shouldEmailBeUnique() {
        when(userRepository.getUserByUsername("username")).thenReturn(Optional.empty());

        boolean result = uniqueUsernameValidator.isValid("username", constraintValidatorContext);
        Assertions.assertTrue(result);
    }

    @DisplayName("Should return false when username email is not unique")
    @Test
    void shouldEmailNotBeUnique() {
        when(userRepository.getUserByUsername("username")).thenReturn(Optional.of(user));

        boolean result = uniqueUsernameValidator.isValid("username", constraintValidatorContext);
        Assertions.assertFalse(result);
    }

    @DisplayName("Should return true when username is equal to logged user username")
    @Test
    void shouldUsernameBeEqualLoggedUserUsername() {
        when(userRepository.getUserByUsername("username")).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("username");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        boolean result = uniqueUsernameValidator.isValid("username", constraintValidatorContext);
        Assertions.assertTrue(result);
    }
}
