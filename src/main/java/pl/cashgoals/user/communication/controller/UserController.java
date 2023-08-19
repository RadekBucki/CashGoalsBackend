package pl.cashgoals.user.communication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;
import pl.cashgoals.user.business.annotation.Password;
import pl.cashgoals.user.business.model.LoginOutput;
import pl.cashgoals.user.business.model.UserInput;
import pl.cashgoals.user.business.service.UserService;
import pl.cashgoals.user.persistence.model.User;
import pl.cashgoals.validation.business.annotation.URL;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @MutationMapping
    public LoginOutput login(@Argument String username, @Argument String password) {
        return userService.login(username, password);
    }

    @MutationMapping
    public User createUser(@Valid @Argument UserInput input) {
        return userService.createUser(input);
    }

    @MutationMapping
    @FullyAuthenticated
    public User updateUser(@Valid @Argument UserInput input, Principal principal) {
        return userService.updateUser(input, principal);
    }

    @MutationMapping
    @FullyAuthenticated
    public LoginOutput refreshToken(@Argument String token, Principal principal) {
        return userService.refreshToken(token, principal);
    }

    @QueryMapping
    @FullyAuthenticated
    public User user(Principal principal) {
        return userService.getUserByUsername(principal.getName());
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
