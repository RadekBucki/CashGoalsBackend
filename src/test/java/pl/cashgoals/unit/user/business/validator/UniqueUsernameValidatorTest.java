package pl.cashgoals.unit.user.business.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @DisplayName("Should return true when username email is unique")
    @Test
    void shouldEmailBeUnique() {
        when(userRepository.getUserByUsername("username")).thenReturn(Optional.empty());

        UniqueUsernameValidator uniqueUsernameValidator = new UniqueUsernameValidator(userRepository);
        uniqueUsernameValidator.initialize(uniqueUsername);

        boolean result = uniqueUsernameValidator.isValid("username", constraintValidatorContext);

        Assertions.assertTrue(result);
    }

    @DisplayName("Should return false when username email is not unique")
    @Test
    void shouldEmailNotBeUnique() {
        when(userRepository.getUserByUsername("username")).thenReturn(Optional.of(new User()));

        UniqueUsernameValidator uniqueUsernameValidator = new UniqueUsernameValidator(userRepository);

        boolean result = uniqueUsernameValidator.isValid("username", constraintValidatorContext);

        Assertions.assertFalse(result);
    }
}
