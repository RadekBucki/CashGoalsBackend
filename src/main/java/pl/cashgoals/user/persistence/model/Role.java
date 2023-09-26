package pl.cashgoals.user.persistence.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Locale;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }

    public static Role fromString(String role) {
        return Role.valueOf(role.toUpperCase(Locale.ROOT));
    }
}
