package pl.cashgoals.user.business.model;

import pl.cashgoals.user.persistence.model.User;

public record LoginOutput(String accessToken, String refreshToken, User user) {
}
