package pl.cashgoals.unit.user.business.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @DisplayName("Should return true when email is unique")
    @Test
    void shouldEmailBeUnique() {
        when(userRepository.getUserByEmail("example@example.com")).thenReturn(Optional.empty());

        UniqueEmailValidator uniqueEmailValidator = new UniqueEmailValidator(userRepository);
        uniqueEmailValidator.initialize(uniqueEmail);

        boolean result = uniqueEmailValidator.isValid("example@example.com", constraintValidatorContext);

        Assertions.assertTrue(result);
    }

    @DisplayName("Should return false when email is not unique")
    @Test
    void shouldEmailNotBeUnique() {
        when(userRepository.getUserByEmail("example@example.com")).thenReturn(Optional.of(new User()));

        UniqueEmailValidator uniqueEmailValidator = new UniqueEmailValidator(userRepository);

        boolean result = uniqueEmailValidator.isValid("example@example.com", constraintValidatorContext);

        Assertions.assertFalse(result);
    }
}
