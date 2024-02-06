package pl.cashgoals.goal.business.strategies.goal.result;

import org.springframework.stereotype.Component;
import pl.cashgoals.goal.business.model.GoalResult;
import pl.cashgoals.goal.persistence.model.Goal;

@Component
public class AmountStrategy implements GoalResultStrategy {
    @Override
    public GoalResult calculate(Goal goal, Double expensesTotal, Double totalIncome) {
        boolean maxReached;
        if (goal.getMax() != null) {
            maxReached = expensesTotal <= goal.getMax();
        } else {
            maxReached = true;
        }

        boolean minReached;
        if (goal.getMin() != null) {
            minReached = expensesTotal >= goal.getMin();
        } else {
            minReached = true;
        }

        return new GoalResult(
                goal,
                expensesTotal,
                maxReached && minReached
        );
    }
}
