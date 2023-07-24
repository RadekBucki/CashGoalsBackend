package pl.cashgoals.user.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import pl.cashgoals.user.persistence.model.AppUser;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenService {
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

    public String generateAccessToken(AppUser appUser) {
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer(issuer)
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(accessExpirationTime * 60 * 60))
                        .subject(appUser.getUsername())
                        .claim("scope",
                                appUser.getAuthorities()
                                        .stream()
                                        .map(Object::toString)
                                        .toList()
                        )
                        .build()
        )).getTokenValue();
    }

    public String generateRefreshToken(AppUser appUser, String accessToken) {
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer(issuer)
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(refreshExpirationTime * 60 * 60 * 24))
                        .claim(USERNAME, appUser.getUsername())
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
