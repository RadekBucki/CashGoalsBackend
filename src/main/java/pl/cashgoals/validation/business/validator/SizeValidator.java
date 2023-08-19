package pl.cashgoals.validation.business.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.cashgoals.validation.business.annotation.Size;

public class SizeValidator implements ConstraintValidator<Size, String> {
    private int min;
    private int max;

    @Override
    public void initialize(Size constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || (value.length() >= min && value.length() <= max)) {
            return true;
        }

        String message;
        if (min == 0) {
            message = "{cashgoals.validation.constraints.Size.max.message}";
        } else if (max == Integer.MAX_VALUE) {
            message = "{cashgoals.validation.constraints.Size.min.message}";
        } else {
            message = "{cashgoals.validation.constraints.Size.message}";
        }

        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
