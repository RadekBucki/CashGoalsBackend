package pl.cashgoals.user.communication.authentication;

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
public class NotValidAuthenticationToken extends AnonymousAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String credentials;

    public NotValidAuthenticationToken(
            String principal,
            String token,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super("NOT_VALID", principal, authorities);
        this.credentials = token;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}
