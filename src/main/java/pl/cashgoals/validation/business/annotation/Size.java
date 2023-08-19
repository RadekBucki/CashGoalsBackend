package pl.cashgoals.validation.business.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.cashgoals.validation.business.validator.SizeValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SizeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Size {
    String message() default "{cashgoals.validation.constraints.Size.message}" +
            "{cashgoals.validation.constraints.Size.max.message}" +
            "{cashgoals.validation.constraints.Size.min.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int min() default 0;
    int max() default Integer.MAX_VALUE;
}
