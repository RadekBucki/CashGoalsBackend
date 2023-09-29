package pl.cashgoals.user.business.model;

import jakarta.validation.constraints.NotEmpty;
import pl.cashgoals.user.business.annotation.Email;
import pl.cashgoals.user.business.annotation.Password;
import pl.cashgoals.user.business.annotation.UniqueEmail;
import pl.cashgoals.user.persistence.model.Theme;
import pl.cashgoals.validation.business.annotation.Size;
import pl.cashgoals.validation.business.annotation.URL;

public record UserInput(
        @Size(min = 2, max = 100)
        String name,
        @Password
        String password,
        @Email
        @UniqueEmail
        String email,
        @NotEmpty
        Theme theme,
        @URL
        String activationUrl
) {

}
