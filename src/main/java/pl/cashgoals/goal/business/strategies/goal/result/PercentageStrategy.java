package pl.cashgoals.goal.business.strategies.goal.result;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.cashgoals.goal.business.model.GoalResult;
import pl.cashgoals.goal.persistence.model.Goal;

@Component
@RequiredArgsConstructor
public class PercentageStrategy implements GoalResultStrategy {
    private static final int PERCENTAGE_MULTIPLIER = 100;

    private final AmountStrategy amountStrategy;

    @Override
    public GoalResult calculate(Goal goal, Double expensesTotal, Double totalIncome) {
        double actualPercentage;
        if (totalIncome != 0) {
            actualPercentage = expensesTotal / totalIncome * PERCENTAGE_MULTIPLIER;
        } else {
            actualPercentage = 0.0;
        }

        return amountStrategy.calculate(goal, (double) Math.round(actualPercentage), totalIncome);
    }
}
