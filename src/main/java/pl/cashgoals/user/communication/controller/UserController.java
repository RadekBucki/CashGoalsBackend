package pl.cashgoals.user.communication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;
import pl.cashgoals.user.business.annotation.Password;
import pl.cashgoals.user.business.model.AuthorizationOutput;
import pl.cashgoals.user.business.model.CreateUserInput;
import pl.cashgoals.user.business.model.UpdateUserInput;
import pl.cashgoals.user.business.service.UserService;
import pl.cashgoals.user.persistence.model.User;
import pl.cashgoals.validation.business.annotation.URL;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @MutationMapping
    public AuthorizationOutput login(@Argument String email, @Argument String password) {
        return userService.login(email, password);
    }

    @MutationMapping
    public User createUser(@Valid @Argument CreateUserInput input) {
        return userService.createUser(input);
    }

    @MutationMapping
    @FullyAuthenticated
    public User updateUser(@Valid @Argument UpdateUserInput input, Principal principal) {
        return userService.updateUser(input, principal);
    }

    @MutationMapping
    @FullyAuthenticated
    @Validated
    public Boolean updateUserPassword(
            @Argument String oldPassword,
            @Argument @Password String newPassword,
            Principal principal
    ) {
        return userService.updateUserPassword(oldPassword, newPassword, principal);
    }

    @MutationMapping
    public AuthorizationOutput refreshToken(@Argument String token, Authentication authentication) {
        return userService.refreshToken(token, authentication);
    }

    @QueryMapping
    @FullyAuthenticated
    public User user(Principal principal) {
        return userService.getUserByEmail(principal.getName());
    }

    @MutationMapping
    public Boolean activateUser(@Argument String token, @Argument String email) {
        return userService.activateUser(token, email);
    }

    @MutationMapping
    @Validated
    public Boolean requestPasswordReset(@Argument String email, @Argument @URL String resetUrl) {
        return userService.requestPasswordReset(email, resetUrl);
    }

    @MutationMapping
    @Validated
    public Boolean resetPassword(
            @Argument String email,
            @Argument String token,
            @Argument @Password String password
    ) {
        return userService.resetPassword(email, token, password);
    }
}
