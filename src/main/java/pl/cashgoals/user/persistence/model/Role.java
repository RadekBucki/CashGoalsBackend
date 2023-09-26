package pl.cashgoals.user.persistence.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }

    static public Role fromString(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
