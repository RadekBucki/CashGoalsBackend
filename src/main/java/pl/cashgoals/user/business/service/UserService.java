package pl.cashgoals.user.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.cashgoals.user.business.exception.UserNotFound;
import pl.cashgoals.user.business.model.LoginOutput;
import pl.cashgoals.user.business.model.UserInput;
import pl.cashgoals.user.persistence.model.User;
import pl.cashgoals.user.persistence.repository.UserRepository;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.getUserByUsername(username)
                .orElseThrow(UserNotFound::new);
    }

    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username)
                .orElseThrow(UserNotFound::new);
    }

    public User createUser(UserInput input) {
        return userRepository.saveAndFlush(
                User.builder()
                        .username(input.username())
                        .email(input.email())
                        .firstname(input.firstname())
                        .lastname(input.lastname())
                        .password(passwordEncoder.encode(input.password()))
                        .enabled(false)
                        .build()
        );
    }

    public LoginOutput login(String username, String password) {
        User user = getUserByUsername(username);

        if (
                !passwordEncoder.matches(password, user.getPassword())
                        || Boolean.TRUE.equals(!user.getEnabled())
        ) {
            throw new UserNotFound();
        }

        String accessToken = tokenService.generateAccessToken(user);

        return new LoginOutput(
                accessToken,
                tokenService.generateRefreshToken(user, accessToken),
                user
        );
    }

    public User updateUser(UserInput input, Principal principal) {
        User user = getUserByUsername(principal.getName());

        user.setUsername(input.username());
        user.setEmail(input.email());
        user.setFirstname(input.firstname());
        user.setLastname(input.lastname());
        user.setPassword(passwordEncoder.encode(input.password()));

        return userRepository.saveAndFlush(user);
    }

    public LoginOutput refreshToken(String token, Principal principal) {
        if (!tokenService.verifyRefreshToken(token, ((JwtAuthenticationToken) principal).getToken().getTokenValue())) {
            throw new UserNotFound();
        }
        User user = getUserByUsername(principal.getName());

        String accessToken = tokenService.generateAccessToken(user);

        return new LoginOutput(
                accessToken,
                tokenService.generateRefreshToken(user, accessToken),
                user
        );
    }
}
