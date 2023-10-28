package pl.cashgoals.goal.communication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.cashgoals.goal.business.model.GoalInput;
import pl.cashgoals.goal.business.service.GoalService;
import pl.cashgoals.goal.persistence.model.Goal;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    @QueryMapping
    @FullyAuthenticated
    public List<Goal> goals(@Argument UUID budgetId) {
        return goalService.getGoals(budgetId);
    }

    @MutationMapping
    @FullyAuthenticated
    public List<Goal> updateGoals(@Argument UUID budgetId, @Argument List<GoalInput> goals) {
        return goalService.updateGoals(budgetId, goals);
    }
}
