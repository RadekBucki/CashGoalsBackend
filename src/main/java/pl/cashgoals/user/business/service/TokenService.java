package pl.cashgoals.user.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import pl.cashgoals.user.persistence.model.User;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final int HOURS_TO_SECONDS_MULTIPLIER = 60 * 60;
    private static final int DAYS_TO_SECONDS_MULTIPLIER = 60 * 60 * 24;
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String USERNAME = "username";

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
                        .subject(user.getUsername())
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
                        .claim(USERNAME, user.getUsername())
                        .claim(ACCESS_TOKEN, accessToken)
                        .build()
        )).getTokenValue();
    }

    public boolean verifyRefreshToken(String refreshToken, String accessToken) {
        Jwt refreshTokenJwt = jwtDecoder.decode(refreshToken);
        if (Objects.requireNonNull(refreshTokenJwt.getExpiresAt()).isBefore(Instant.now())) {
            return false;
        }
        Jwt accessTokenJwt = jwtDecoder.decode(refreshTokenJwt.getClaimAsString(ACCESS_TOKEN));
        return refreshTokenJwt.getClaimAsString(USERNAME).equals(accessTokenJwt.getSubject())
                && refreshTokenJwt.getClaimAsString(ACCESS_TOKEN).equals(accessToken);
    }
}
