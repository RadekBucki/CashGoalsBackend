package pl.cashgoals.user.communication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pl.cashgoals.user.business.UserFacade;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;
import pl.cashgoals.user.business.model.LoginOutput;
import pl.cashgoals.user.business.model.UserInput;
import pl.cashgoals.user.persistence.model.AppUser;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserFacade userService;

    @MutationMapping
    public LoginOutput login(@Argument String username, @Argument String password) {
        return userService.login(username, password);
    }

    @MutationMapping
    public AppUser createUser(@Argument UserInput input) {
        return userService.createUser(input);
    }

    @MutationMapping
    @FullyAuthenticated
    public AppUser updateUser(@Argument UserInput input, Principal principal) {
        return userService.updateUser(input, principal);
    }
}
