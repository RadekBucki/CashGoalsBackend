package pl.cashgoals.notification.business.model;

import lombok.Getter;

@Getter
public enum Template {
    ACTIVATION("activation"),
    RESET_PASSWORD("reset-password");

    private final String name;

    Template(String template) {
        this.name = template;
    }
}
