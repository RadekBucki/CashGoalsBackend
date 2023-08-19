package pl.cashgoals.user.business.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.cashgoals.user.business.validator.UniqueUsernameValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String value() default "";
    String message() default "{cashgoals.validation.constraints.UsernameExist.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
