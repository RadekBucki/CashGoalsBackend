package pl.cashgoals.user.business.model;

import jakarta.validation.constraints.NotNull;
import pl.cashgoals.user.business.annotation.Email;
import pl.cashgoals.user.business.annotation.Password;
import pl.cashgoals.user.business.annotation.UniqueEmail;
import pl.cashgoals.user.business.annotation.UniqueUsername;
import pl.cashgoals.validation.business.annotation.Size;

public record UserInput(
        @NotNull
        @Size(min = 2, max = 100)
        @UniqueUsername
        String username,
        @NotNull
        @Password
        String password,
        @NotNull
        @Email
        @UniqueEmail
        String email,
        String activationUrl
) {

}
