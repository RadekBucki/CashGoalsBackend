package pl.cashgoals.goal.business.model;

import pl.cashgoals.goal.persistence.model.Goal;

public record GoalResult(
        Goal goal,
        Double actual,
        Boolean minReached,
        Boolean maxReached
) {
}
