package pl.cashgoals.user.communication.authentication;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class NotValidAuthenticationToken extends AnonymousAuthenticationToken {
    private final String credentials;

    public NotValidAuthenticationToken(String principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super("NOT_VALID", principal, authorities);
        this.setAuthenticated(false);
        this.credentials = token;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}
