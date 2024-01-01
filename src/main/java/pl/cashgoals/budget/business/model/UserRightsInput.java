package pl.cashgoals.budget.business.model;

import pl.cashgoals.budget.persistence.model.Right;

import java.util.List;

public record UserRightsInput(String email, List<Right> rights) {
}
