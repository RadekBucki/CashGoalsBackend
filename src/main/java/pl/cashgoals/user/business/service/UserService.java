package pl.cashgoals.user.business.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.cashgoals.notification.business.NotificationFacade;
import pl.cashgoals.notification.business.model.Template;
import pl.cashgoals.notification.business.service.source.Source;
import pl.cashgoals.user.business.exception.BadRefreshTokenException;
import pl.cashgoals.user.business.exception.UserNotFoundException;
import pl.cashgoals.user.business.model.AuthorizationOutput;
import pl.cashgoals.user.business.model.CreateUserInput;
import pl.cashgoals.user.business.model.UpdateUserInput;
import pl.cashgoals.user.persistence.model.TokenType;
import pl.cashgoals.user.persistence.model.User;
import pl.cashgoals.user.persistence.model.UserToken;
import pl.cashgoals.user.persistence.repository.UserRepository;
import pl.cashgoals.utils.graphql.business.exception.GraphQLBadRequestException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final NotificationFacade notificationFacade;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return getUserByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public User createUser(CreateUserInput input) {
        User user = User.builder()
                .name(input.name())
                .email(input.email())
                .password(passwordEncoder.encode(input.password()))
                .theme(input.theme())
                .locale(LocaleContextHolder.getLocale())
                .enabled(false)
                .build();

        String code = tokenService.generateRandomCode();

        UserToken token = UserToken.builder()
                .user(user)
                .token(code)
                .type(TokenType.ACTIVATION)
                .build();

        user.getTokens().add(token);
        userRepository.saveAndFlush(user);

        notificationFacade.sendNotification(
                Template.ACTIVATION,
                user,
                Map.of(
                        "url", input.activationUrl() + "?user=" + user.getEmail() + "&code=" + code,
                        "code", code
                ),
                List.of(Source.EMAIL)
        );
        return user;
    }

    public AuthorizationOutput login(String email, String password) {
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword()) || Boolean.TRUE.equals(!user.getEnabled())) {
            throw new UserNotFoundException();
        }

        String accessToken = tokenService.generateAccessToken(user);

        return new AuthorizationOutput(
                accessToken,
                tokenService.generateRefreshToken(user, accessToken),
                user
        );
    }

    public User updateUser(UpdateUserInput input, Principal principal) {
        User user = getUserByEmail(principal.getName());
        if (!passwordEncoder.matches(input.password(), user.getPassword())) {
            throw new GraphQLBadRequestException("cashgoals.user.bad-password");
        }

        user.setName(input.name());
        user.setEmail(input.email());
        user.setTheme(input.theme());
        user.setLocale(input.locale());

        return userRepository.saveAndFlush(user);
    }

    public Boolean updateUserPassword(String oldPassword, String newPassword, Principal principal) {
        return true;
    }

    public AuthorizationOutput refreshToken(String token, Authentication authentication) {
        if (!tokenService.verifyRefreshToken(token, authentication.getCredentials().toString())) {
            throw new BadRefreshTokenException();
        }
        User user = getUserByEmail(authentication.getName());

        String accessToken = tokenService.generateAccessToken(user);

        return new AuthorizationOutput(
                accessToken,
                tokenService.generateRefreshToken(user, accessToken),
                user
        );
    }

    public Boolean activateUser(String token, String email) {
        User user = userRepository.getUserWithTokensByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (user.isEnabled()) {
            throw new GraphQLBadRequestException("cashgoals.user.already-activated");
        }

        if (
                user.getTokens()
                        .stream()
                        .filter(userToken -> userToken.getType() == TokenType.ACTIVATION)
                        .filter(userToken -> userToken.getToken().equals(token))
                        .findFirst()
                        .isEmpty()
        ) {
            throw new GraphQLBadRequestException("cashgoals.user.bad-activation-token");
        }

        user.setEnabled(true);
        user.getTokens().removeIf(userToken -> userToken.getType() == TokenType.ACTIVATION);
        userRepository.saveAndFlush(user);

        return true;
    }

    public Boolean requestPasswordReset(String email, String resetUrl) {
        User user = userRepository.getActiveUserByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        String code = tokenService.generateRandomCode();

        UserToken userToken = UserToken.builder()
                .user(user)
                .token(code)
                .type(TokenType.RESET_PASSWORD)
                .build();

        user.getTokens().add(userToken);
        userRepository.saveAndFlush(user);

        notificationFacade.sendNotification(
                Template.RESET_PASSWORD,
                user,
                Map.of(
                        "url", resetUrl + "?user=" + user.getEmail() + "&code=" + code,
                        "code", code
                ),
                List.of(Source.EMAIL)
        );

        return true;
    }

    public Boolean resetPassword(String email, String token, String newPassword) {
        User user = userRepository.getActiveUserByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (
                user.getTokens()
                        .stream()
                        .filter(userToken -> userToken.getType() == TokenType.RESET_PASSWORD)
                        .filter(userToken -> userToken.getToken().equals(token))
                        .findFirst()
                        .isEmpty()
        ) {
            throw new GraphQLBadRequestException("cashgoals.user.bad-reset-password-token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.getTokens().removeIf(userToken -> userToken.getType() == TokenType.RESET_PASSWORD);
        userRepository.saveAndFlush(user);

        return true;
    }
}
