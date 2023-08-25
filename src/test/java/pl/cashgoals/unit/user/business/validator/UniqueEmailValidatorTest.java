package pl.cashgoals.unit.user.business.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.cashgoals.user.business.annotation.UniqueEmail;
import pl.cashgoals.user.business.validator.UniqueEmailValidator;
import pl.cashgoals.user.persistence.model.User;
import pl.cashgoals.user.persistence.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UniqueEmailValidatorTest {
    UniqueEmail uniqueEmail = mock(UniqueEmail.class);
    ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);
    UserRepository userRepository = mock(UserRepository.class);
    User user = new User();
    UniqueEmailValidator uniqueEmailValidator = new UniqueEmailValidator(userRepository);
    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        user.setName("example");
        uniqueEmailValidator.initialize(uniqueEmail);
    }

    @DisplayName("Should return true when email is unique")
    @Test
    void shouldEmailBeUnique() {
        when(userRepository.getUserByEmail("example@example.com")).thenReturn(Optional.empty());

        boolean result = uniqueEmailValidator.isValid("example@example.com", constraintValidatorContext);
        Assertions.assertTrue(result);
    }

    @DisplayName("Should return false when email is not unique")
    @Test
    void shouldEmailNotBeUnique() {
        when(userRepository.getUserByEmail("example@example.com")).thenReturn(Optional.of(user));

        boolean result = uniqueEmailValidator.isValid("example@example.com", constraintValidatorContext);
        Assertions.assertFalse(result);
    }

    @DisplayName("Should return true when email is equal to logged user email")
    @Test
    void shouldEmailBeEqualLoggedUserEmail() {
        when(userRepository.getUserByEmail("example@example.com")).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("example");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        boolean result = uniqueEmailValidator.isValid("example@example.com", constraintValidatorContext);
        Assertions.assertTrue(result);
    }

}
