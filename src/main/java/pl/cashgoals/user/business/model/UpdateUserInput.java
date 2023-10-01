package pl.cashgoals.user.business.model;

import jakarta.validation.constraints.NotNull;
import pl.cashgoals.user.business.annotation.Email;
import pl.cashgoals.user.business.annotation.UniqueEmail;
import pl.cashgoals.user.persistence.model.Theme;
import pl.cashgoals.validation.business.annotation.Size;

import java.util.Locale;

public record UpdateUserInput(
        @Size(min = 2, max = 100)
        String name,
        String password,
        @Email
        @UniqueEmail
        String email,
        @NotNull
        Theme theme,
        @NotNull
        Locale locale
) {

}
