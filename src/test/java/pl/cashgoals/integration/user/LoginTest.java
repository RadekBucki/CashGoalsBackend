package pl.cashgoals.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import pl.cashgoals.configuration.AbstractIntegrationTest;
import pl.cashgoals.user.business.model.LoginOutput;

import java.time.Instant;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoginTest extends AbstractIntegrationTest {
    @Autowired
    private JwtDecoder jwtDecoder;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${spring.security.jwt.access-expiration-in-hours}")
    private long accessExpirationTime;

    @Value("${spring.security.jwt.refresh-expiration-in-days}")
    private long refreshExpirationTime;

    @DisplayName("Should login")
    @Test
    void shouldLogin() {
        GraphQlTester.Response response = userRequests.login("test", "Test123!");

        response
                .errors().verify()
                .path("login").entity(LoginOutput.class).satisfies(loginOutput -> {
                    assertEquals("test", loginOutput.user().getUsername());
                    assertEquals("test@example.com", loginOutput.user().getEmail());

                    Jwt accessToken = jwtDecoder.decode(loginOutput.accessToken());
                    assertEquals(issuer, accessToken.getClaim(JwtClaimNames.ISS));
                    assertEquals("test", accessToken.getSubject());
                    assertNotNull(accessToken.getIssuedAt());
                    assertEquals(accessToken.getIssuedAt().toEpochMilli(), Instant.now().toEpochMilli(), 3000);
                    assertNotNull(accessToken.getExpiresAt());
                    assertEquals(
                            accessToken.getExpiresAt().toEpochMilli(),
                            Instant.now().plusSeconds(accessExpirationTime * 60 * 60).toEpochMilli(),
                            3000
                    );

                    Jwt refreshToken = jwtDecoder.decode(loginOutput.refreshToken());
                    assertEquals(issuer, refreshToken.getClaim(JwtClaimNames.ISS));
                    assertEquals("test", refreshToken.getClaimAsString("username"));
                    assertEquals(loginOutput.accessToken(), refreshToken.getClaimAsString("accessToken"));
                    assertNotNull(refreshToken.getIssuedAt());
                    assertEquals(refreshToken.getIssuedAt().toEpochMilli(), Instant.now().toEpochMilli(), 3000);
                    assertNotNull(refreshToken.getExpiresAt());
                    assertEquals(
                            refreshToken.getExpiresAt().toEpochMilli(),
                            Instant.now().plusSeconds(refreshExpirationTime * 60 * 60 * 24).toEpochMilli(),
                            3000
                    );
                });
    }

    @DisplayName("Should return error when password is incorrect")
    @Test
    void shouldReturnErrorWhenPasswordIsIncorrect() {
        GraphQlTester.Response response = userRequests.login("test", "incorrect");

        response.errors()
                .expect(responseError -> responseError.getErrorType().equals(ErrorType.NOT_FOUND) &&
                        Objects.requireNonNull(responseError.getMessage()).equals("cashgoals.user.not-found")
                )
                .verify();
    }

    @DisplayName("Should return error when user does not exist")
    @Test
    void shouldReturnErrorWhenUserDoesNotExist() {
        GraphQlTester.Response response = userRequests.login("not-exist", "incorrect");

        response.errors()
                .expect(responseError -> responseError.getErrorType().equals(ErrorType.NOT_FOUND) &&
                        Objects.requireNonNull(responseError.getMessage()).equals("cashgoals.user.not-found")
                )
                .verify();
    }

    @DisplayName("Should return error when user is not active")
    @Test
    void shouldReturnErrorWhenUserIsNotActive() {
        GraphQlTester.Response response = userRequests.login("inactive", "Test123!");

        response.errors()
                .expect(responseError -> responseError.getErrorType().equals(ErrorType.NOT_FOUND) &&
                        Objects.requireNonNull(responseError.getMessage()).equals("cashgoals.user.not-found")
                )
                .verify();
    }
}
