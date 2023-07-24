package pl.cashgoals.user.business.model;

import pl.cashgoals.user.persistence.model.AppUser;

public record LoginOutput(String accessToken, String refreshToken, AppUser user) {
}
