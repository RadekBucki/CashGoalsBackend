package pl.cashgoals.user.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.user.business.service.UserService;
import pl.cashgoals.user.persistence.model.User;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;

    public User getUserByUsername(String username) {
        return userService.getUserByUsername(username);
    }
}
