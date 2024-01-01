package pl.cashgoals.budget.business.model;

import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;

public record UserRightsOutput(User user, List<Right> rights) {
}
