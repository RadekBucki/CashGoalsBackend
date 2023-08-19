package pl.cashgoals.unit.validation.business.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.cashgoals.validation.business.annotation.Size;
import pl.cashgoals.validation.business.validator.SizeValidator;

import static graphql.Assert.assertFalse;
import static graphql.Assert.assertTrue;
import static org.mockito.Mockito.*;


class SizeValidatorTest {
    Size sizeAnnotation = mock(Size.class);
    ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);
    ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder =
            mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

    @DisplayName("Should return true when size is between min and max")
    @ParameterizedTest
    @CsvSource({
            "1, 10, test1",
            "5, 15, test2",
            "0, 5, test3",
            "0, 5, null"
    })
    void shouldSizeBeValid(int min, int max, String value) {
        when(sizeAnnotation.min()).thenReturn(min);
        when(sizeAnnotation.max()).thenReturn(max);

        SizeValidator sizeValidator = new SizeValidator();
        sizeValidator.initialize(sizeAnnotation);

        boolean result = sizeValidator.isValid(value, constraintValidatorContext);

        assertTrue(result);
    }

    @DisplayName("Should be Size message when size is not between min and max")
    @ParameterizedTest
    @CsvSource({
            "5, 10, test, {cashgoals.validation.constraints.Size.message}",
            "1, 3, test, {cashgoals.validation.constraints.Size.message}",
            "0, 2, test, {cashgoals.validation.constraints.Size.max.message}",
            "5, " + Integer.MAX_VALUE + ", test, {cashgoals.validation.constraints.Size.min.message}"
    })
    void shouldBeSizeMessageWhenSizeNotInMinAndMax(int min, int max, String value, String message) {
        when(sizeAnnotation.min()).thenReturn(min);
        when(sizeAnnotation.max()).thenReturn(max);

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(message))
                .thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addConstraintViolation())
                .thenReturn(constraintValidatorContext);

        SizeValidator sizeValidator = new SizeValidator();
        sizeValidator.initialize(sizeAnnotation);

        boolean result = sizeValidator.isValid(value, constraintValidatorContext);

        assertFalse(result);
    }
}