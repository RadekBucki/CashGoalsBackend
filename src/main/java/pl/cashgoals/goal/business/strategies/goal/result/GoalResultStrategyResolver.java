package pl.cashgoals.goal.business.strategies.goal.result;

import org.springframework.stereotype.Component;
import pl.cashgoals.goal.persistence.model.GoalType;

import java.util.Map;

@Component
public class GoalResultStrategyResolver {
    private final Map<GoalType, GoalResultStrategy> strategies;

    public GoalResultStrategyResolver(AmountStrategy amountStrategy, PercentageStrategy percentageStrategy) {
        this.strategies = Map.of(
                GoalType.AMOUNT, amountStrategy,
                GoalType.PERCENTAGE, percentageStrategy
        );
    }

    public GoalResultStrategy resolve(GoalType goalType) {
        return strategies.get(goalType);
    }
}
