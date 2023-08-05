package pl.cashgoals.integration.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.user.business.model.LoginOutput;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class RefreshTokenTest extends AbstractIntegrationTest {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${spring.security.jwt.refresh-expiration-in-days}")
    private long refreshExpirationTime;

    LoginOutput loginOutput;

    @BeforeEach
    void setUp() {
        super.setup();
        loginOutput = userRequests.login("test", "Test123!")
                .path("login").entity(LoginOutput.class)
                .get();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new JwtAuthenticationToken(
                        Jwt.withTokenValue(loginOutput.accessToken())
                                .subject(loginOutput.user().getUsername())
                                .header("Authorization", "Bearer " + loginOutput.accessToken())
                                .claim("scope", "USER")
                                .build(),
                        List.of((GrantedAuthority) () -> "SCOPE_USER")
                )
        );
        SecurityContextHolder.setContext(context);
    }

    @DisplayName("Should refresh token")
    @Test
    void shouldRefreshToken() {
        userRequests.refreshToken(loginOutput.refreshToken())
                .errors().verify()
                .path("refreshToken").entity(LoginOutput.class).satisfies(loginOutput -> {
                    assertEquals("test", loginOutput.user().getUsername());
                    assertNotNull(loginOutput.accessToken());
                    assertNotNull(loginOutput.refreshToken());
                });
    }

    @DisplayName("Should not refresh token when token is invalid")
    @Test
    void shouldNotRefreshTokenWhenTokenIsInvalid() {
        userRequests.refreshToken("invalidToken")
                .errors()
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.BAD_REQUEST) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.user.bad-refresh-token")
                )
                .verify();
    }

    @DisplayName("Should not refresh token when token is expired")
    @Test
    void shouldNotRefreshTokenWhenTokenIsExpired() {
        Instant instant = Instant.now(Clock.fixed(
                Instant.now().plusSeconds(refreshExpirationTime * 24 * 60 * 60 + 1),
                Clock.systemDefaultZone().getZone()
        ));

        try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class)) {
            mockedStatic.when(Instant::now).thenReturn(instant);
            assertEquals(Instant.now(), instant);

            userRequests.refreshToken(loginOutput.refreshToken())
                    .errors()
                    .expect(responseError ->
                            responseError.getErrorType().equals(ErrorType.BAD_REQUEST) &&
                                    Objects.equals(responseError.getMessage(), "cashgoals.user.bad-refresh-token")
                    )
                    .verify();
        }
    }

    @DisplayName("Should not refresh token when username claim is missing in refresh token")
    @Test
    void shouldNotRefreshTokenWhenUsernameClaimIsMissingInRefreshToken() {
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer(issuer)
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(refreshExpirationTime * 24 * 60 * 60))
                        .claim("accessToken", loginOutput.accessToken())
                        .build()
        )).getTokenValue();
        userRequests.refreshToken(refreshToken)
                .errors()
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.BAD_REQUEST) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.user.bad-refresh-token")
                )
                .verify();
    }

    @DisplayName("Should not refresh token when accessToken claim is missing in refresh token")
    @Test
    void shouldNotRefreshTokenWhenAccessTokenClaimIsMissingInRefreshToken() {
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer(issuer)
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(refreshExpirationTime * 24 * 60 * 60))
                        .claim("username", loginOutput.user().getUsername())
                        .build()
        )).getTokenValue();
        userRequests.refreshToken(refreshToken)
                .errors()
                .expect(responseError ->
                        responseError.getErrorType().equals(ErrorType.BAD_REQUEST) &&
                                Objects.equals(responseError.getMessage(), "cashgoals.user.bad-refresh-token")
                )
                .verify();
    }
}
