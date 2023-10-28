package pl.cashgoals.goal.business.model;

import pl.cashgoals.goal.persistence.model.GoalType;

public record GoalInput(
        Long id,
        String name,
        String description,
        GoalType type,
        Double value,
        Long category
) {
}
