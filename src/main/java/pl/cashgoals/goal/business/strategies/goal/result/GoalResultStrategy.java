package pl.cashgoals.goal.business.strategies.goal.result;


import pl.cashgoals.goal.business.model.GoalResult;
import pl.cashgoals.goal.persistence.model.Goal;

public interface GoalResultStrategy {
    GoalResult calculate(Goal goal, Double expensesTotal, Double totalIncome);
}
