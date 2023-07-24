package pl.cashgoals.user.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import pl.cashgoals.user.business.model.TokenType;
import pl.cashgoals.user.persistence.model.AppUser;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${spring.security.jwt.access-expiration}")
    private long accessExpirationTime;

    @Value("${spring.security.jwt.refresh-expiration}")
    private long refreshExpirationTime;

    public String generateToken(AppUser appUser, TokenType tokenType) {
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer(issuer)
                        .issuedAt(Instant.now())
                        .expiresAt(
                                Instant.now()
                                        .plusSeconds(getExpirationTime(tokenType))
                        )
                        .subject(appUser.getUsername())
                        .audience(
                                appUser.getAuthorities()
                                        .stream()
                                        .map(Object::toString)
                                        .toList()
                        )
                        .build()
        )).getTokenValue();
    }

    private Long getExpirationTime(TokenType tokenType) {
        return tokenType == TokenType.ACCESS_TOKEN ? accessExpirationTime : refreshExpirationTime;
    }
}
