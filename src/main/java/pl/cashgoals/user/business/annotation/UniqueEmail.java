package pl.cashgoals.user.business.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.cashgoals.user.business.validator.UniqueEmailValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String value() default "";
    String message() default "{cashgoals.validation.constraints.EmailExist.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
