package pl.cashgoals.user.business.service;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import pl.cashgoals.user.persistence.model.User;

import java.text.ParseException;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private static final int HOURS_TO_SECONDS_MULTIPLIER = 60 * 60;
    private static final int DAYS_TO_SECONDS_MULTIPLIER = 60 * 60 * 24;
    private static final int RANDOM_CODE_LENGTH = 10;
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String EMAIL = "email";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${spring.security.jwt.access-expiration-in-hours}")
    private long accessExpirationTime;

    @Value("${spring.security.jwt.refresh-expiration-in-days}")
    private long refreshExpirationTime;

    public String generateAccessToken(User user) {
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer(issuer)
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(accessExpirationTime * HOURS_TO_SECONDS_MULTIPLIER))
                        .subject(user.getEmail())
                        .claim("scope",
                                user.getAuthorities()
                                        .stream()
                                        .map(Object::toString)
                                        .toList()
                        )
                        .build()
        )).getTokenValue();
    }

    public String generateRefreshToken(User user, String accessToken) {
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer(issuer)
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(refreshExpirationTime * DAYS_TO_SECONDS_MULTIPLIER))
                        .claim(EMAIL, user.getEmail())
                        .claim(ACCESS_TOKEN, accessToken)
                        .build()
        )).getTokenValue();
    }

    public boolean verifyRefreshToken(String refreshToken, String accessToken) {
        try {
            Jwt refreshTokenJwt = jwtDecoder.decode(refreshToken);
            String accessTokenClaim = refreshTokenJwt.getClaimAsString(ACCESS_TOKEN);
            if (accessTokenClaim == null) {
                return false;
            }
            JWT accessTokenJwt = JWTParser.parse(accessTokenClaim);
            return Objects.equals(
                    refreshTokenJwt.getClaimAsString(EMAIL),
                    accessTokenJwt.getJWTClaimsSet().getSubject()
            )
                    && Objects.equals(accessTokenClaim, accessToken)
                    && Objects.requireNonNull(refreshTokenJwt.getExpiresAt()).isAfter(Instant.now());
        } catch (JwtException | ParseException e) {
            log.debug("Invalid access token: {0}", e);
            return false;
        }
    }

    public String generateRandomCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, RANDOM_CODE_LENGTH)
                .toUpperCase(Locale.ENGLISH);
    }
}
