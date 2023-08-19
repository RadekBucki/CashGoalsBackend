package pl.cashgoals.unit.user.business.service;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.util.ReflectionTestUtils;
import pl.cashgoals.user.business.service.TokenService;
import pl.cashgoals.user.persistence.model.User;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    JwtEncoder jtwEncoder;
    JwtDecoder jwtDecoder;
    TokenService tokenService;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();

        JWK jwk = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("cashgoals")
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));

        jtwEncoder = new NimbusJwtEncoder(jwkSource);
        jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey).build();

        tokenService = new TokenService(jtwEncoder, jwtDecoder);

        ReflectionTestUtils.setField(tokenService, "issuer", "issuer");
        ReflectionTestUtils.setField(tokenService, "accessExpirationTime", 1);
        ReflectionTestUtils.setField(tokenService, "refreshExpirationTime", 1);
    }

    @DisplayName("Should generate access token")
    @Test
    void shouldGenerateAccessToken() {
        String token = tokenService.generateAccessToken(
                User.builder()
                        .username("username")
                        .build()
        );

        Jwt decodedToken = jwtDecoder.decode(token);

        assertEquals("username", decodedToken.getSubject());
        assertEquals("issuer", decodedToken.getClaim(JwtClaimNames.ISS));
        assertEquals(
                Instant.now().plusSeconds(3600).toEpochMilli(),
                Objects.requireNonNull(decodedToken.getExpiresAt()).toEpochMilli(),
                3000
        );
    }

    @DisplayName("Should generate refresh token")
    @Test
    void shouldGenerateRefreshToken() {
        String token = tokenService.generateRefreshToken(
                User.builder()
                        .username("username")
                        .build(),
                "accessToken"
        );

        Jwt decodedToken = jwtDecoder.decode(token);

        assertEquals("username", decodedToken.getClaimAsString("username"));
        assertEquals("accessToken", decodedToken.getClaimAsString("accessToken"));
        assertEquals("issuer", decodedToken.getClaim(JwtClaimNames.ISS));
        assertEquals(
                Instant.now().plusSeconds(86400).toEpochMilli(),
                Objects.requireNonNull(decodedToken.getExpiresAt()).toEpochMilli(),
                3000
        );
    }

    @DisplayName("Verify refresh token")
    @Nested
    class VerifyRefreshTokenTest {
        @DisplayName("Should return true when token is valid")
        @Test
        void shouldReturnTrueWhenTokenIsValid() {
            String accessToken = tokenService.generateAccessToken(
                    User.builder()
                            .username("username")
                            .build()
            );
            String refreshToken = tokenService.generateRefreshToken(
                    User.builder()
                            .username("username")
                            .build(),
                    accessToken
            );

            assertTrue(tokenService.verifyRefreshToken(refreshToken, accessToken));
        }

        @DisplayName("Should return false when access token is invalid")
        @Test
        void shouldReturnFalseWhenTokenIsInvalid() {
            String accessToken = tokenService.generateAccessToken(
                    User.builder()
                            .username("username")
                            .build()
            );
            String refreshToken = tokenService.generateRefreshToken(
                    User.builder()
                            .username("username")
                            .build(),
                    accessToken
            );

            assertFalse(tokenService.verifyRefreshToken(refreshToken, accessToken + "invalid"));
        }

        @DisplayName("Should return false when refresh token is invalid")
        @Test
        void shouldReturnFalseWhenRefreshTokenIsInvalid() {
            assertFalse(tokenService.verifyRefreshToken("invalid", "invalid"));
        }

        @DisplayName("Should return false when refresh token is expired")
        @Test
        void shouldReturnFalseWhenRefreshTokenIsExpired() {
            String accessToken = tokenService.generateAccessToken(
                    User.builder()
                            .username("username")
                            .build()
            );
            String refreshToken = tokenService.generateRefreshToken(
                    User.builder()
                            .username("username")
                            .build(),
                    accessToken
            );

            Instant instant = Instant.now(Clock.fixed(
                    Instant.now().plusSeconds(86400 * 2),
                    Clock.systemDefaultZone().getZone()
            ));
            try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class)) {
                mockedInstant.when(Instant::now).thenReturn(instant);
                assertEquals(Instant.now(), instant);

                assertFalse(tokenService.verifyRefreshToken(refreshToken, accessToken));
            }
        }

        @DisplayName("Should return false when username in refresh token is different than access token subject")
        @Test
        void shouldReturnFalseWhenUsernameInRefreshTokenIsDifferentThanAccessTokenSubject() {
            String accessToken = tokenService.generateAccessToken(
                    User.builder()
                            .username("username")
                            .build()
            );
            String refreshToken = tokenService.generateRefreshToken(
                    User.builder()
                            .username("username2")
                            .build(),
                    accessToken
            );

            assertFalse(tokenService.verifyRefreshToken(refreshToken, accessToken));
        }

        @DisplayName("Should return false when refresh token has different access token")
        @Test
        void shouldReturnFalseWhenRefreshTokenHasDifferentAccessToken() {
            String accessToken = tokenService.generateAccessToken(
                    User.builder()
                            .username("username")
                            .build()
            );
            String refreshToken = tokenService.generateRefreshToken(
                    User.builder()
                            .username("username")
                            .build(),
                    accessToken + "invalid"
            );

            assertFalse(tokenService.verifyRefreshToken(refreshToken, accessToken));
        }
    }

    @DisplayName("Generate random code")
    @Test
    void shouldGenerateRandomCode() {
        String code = tokenService.generateRandomCode();

        assertEquals(10, code.length());
    }

    @DisplayName("Random code is unique")
    @Test
    void shouldRandomCodeIsUnique() {
        String code1 = tokenService.generateRandomCode();
        String code2 = tokenService.generateRandomCode();

        assertNotEquals(code1, code2);
    }
}
