package pl.cashgoals.user.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.user.business.model.LoginOutput;
import pl.cashgoals.user.business.model.UserInput;
import pl.cashgoals.user.business.service.UserService;
import pl.cashgoals.user.persistence.model.AppUser;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;

    public AppUser createUser(UserInput input) {
        return userService.createUser(input);
    }

    public LoginOutput login(String username, String password) {
        return userService.login(username, password);
    }

    public AppUser updateUser(UserInput input, Principal principal) {
        return userService.updateUser(input, principal);
    }
}
