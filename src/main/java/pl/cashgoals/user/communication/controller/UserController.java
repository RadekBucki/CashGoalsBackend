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
    private final UserFacade userFacade;

    @MutationMapping
    public LoginOutput login(@Argument String username, @Argument String password) {
        return userFacade.login(username, password);
    }

    @MutationMapping
    public AppUser createUser(@Argument UserInput input) {
        return userFacade.createUser(input);
    }

    @MutationMapping
    @FullyAuthenticated
    public AppUser updateUser(@Argument UserInput input, Principal principal) {
        return userFacade.updateUser(input, principal);
    }

    @MutationMapping
    @FullyAuthenticated
    public LoginOutput refreshToken(@Argument String token, Principal principal) {
        return userFacade.refreshToken(token, principal);
    }
}
