package pl.cashgoals.user.business.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import pl.cashgoals.user.business.annotation.UniqueUsername;
import pl.cashgoals.user.persistence.repository.AppUserRepository;

@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    private final AppUserRepository appUserRepository;

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return appUserRepository.getUserByUsername(value).isEmpty();
    }
}
