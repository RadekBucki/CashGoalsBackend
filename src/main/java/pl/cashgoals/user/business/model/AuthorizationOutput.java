package pl.cashgoals.user.business.model;

import pl.cashgoals.user.persistence.model.User;

public record AuthorizationOutput(String accessToken, String refreshToken, User user) {
}
