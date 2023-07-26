package pl.cashgoals.user.business.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import pl.cashgoals.user.business.annotation.UniqueEmail;
import pl.cashgoals.user.persistence.repository.AppUserRepository;

@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final AppUserRepository appUserRepository;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return appUserRepository.getUserByEmail(value).isEmpty();
    }
}
