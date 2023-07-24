package pl.cashgoals.user.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.cashgoals.user.business.exception.AuthenticationException;
import pl.cashgoals.user.business.model.LoginOutput;
import pl.cashgoals.user.business.model.TokenType;
import pl.cashgoals.user.business.model.UserInput;
import pl.cashgoals.user.persistence.model.AppUser;
import pl.cashgoals.user.persistence.repository.AppUserRepository;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.getUserByUsername(username)
                .orElseThrow(AuthenticationException::new);
    }

    public AppUser createUser(UserInput input) {
        return appUserRepository.saveAndFlush(
                AppUser.builder()
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
        AppUser appUser = appUserRepository.getUserByUsername(username)
                .orElseThrow(AuthenticationException::new);

        if (
                !passwordEncoder.matches(password, appUser.getPassword())
                        || Boolean.TRUE.equals(!appUser.getEnabled())
        ) {
            throw new AuthenticationException();
        }

        return new LoginOutput(
                tokenService.generateToken(appUser, TokenType.ACCESS_TOKEN),
                tokenService.generateToken(appUser, TokenType.REFRESH_TOKEN),
                appUser
        );
    }

    public AppUser updateUser(UserInput input, Principal principal) {
        AppUser appUser = appUserRepository.getUserByUsername(principal.getName())
                .orElseThrow(AuthenticationException::new);

        appUser.setUsername(input.username());
        appUser.setEmail(input.email());
        appUser.setFirstname(input.firstname());
        appUser.setLastname(input.lastname());
        appUser.setPassword(passwordEncoder.encode(input.password()));

        return appUserRepository.saveAndFlush(appUser);
    }
}
