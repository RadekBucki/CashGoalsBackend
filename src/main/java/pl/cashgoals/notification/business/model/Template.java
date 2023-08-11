package pl.cashgoals.notification.business.model;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum Template implements Serializable {
    ACTIVATION("activation"),
    RESET_PASSWORD("reset-password");

    private final String name;

    Template(String template) {
        this.name = template;
    }
}
