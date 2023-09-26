package pl.cashgoals.user.communication.filter;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.cashgoals.user.communication.authentication.NotValidAuthenticationToken;
import pl.cashgoals.user.persistence.model.Role;

import java.io.IOException;
import java.text.ParseException;

public class BearerTokenAuthenticationFilter extends BasicAuthenticationFilter {

    private final BearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
    private final JwtDecoder jwtDecoder;

    public BearerTokenAuthenticationFilter(AuthenticationManager authenticationManager, JwtDecoder jwtDecoder) {
        super(authenticationManager);
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        String token = bearerTokenResolver.resolve(request);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication;
        try {
            Jwt jwt = jwtDecoder.decode(token);
            authentication = new UsernamePasswordAuthenticationToken(
                    jwt.getSubject(),
                    token,
                    jwt.getClaimAsStringList("scope")
                            .stream()
                            .map(Role::fromString)
                            .toList()
            );
        } catch (JwtException e) {
            logger.debug("Failed to validate JWT token", e);
            try {
                JWT jwt = JWTParser.parse(token);
                authentication = new NotValidAuthenticationToken(
                        jwt.getJWTClaimsSet().getSubject(),
                        token,
                        jwt.getJWTClaimsSet()
                                .getStringListClaim("scope")
                                .stream()
                                .map(Role::fromString)
                                .toList()
                );
            } catch (ParseException ex) {
                chain.doFilter(request, response);
                return;
            }
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
