package pl.cashgoals.goal.communication.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.cashgoals.goal.business.model.GoalResult;
import pl.cashgoals.goal.business.service.GoalResultsService;
import pl.cashgoals.user.business.annotation.FullyAuthenticated;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class GoalResultsController {
    private final GoalResultsService goalResultsService;

    @QueryMapping
    @FullyAuthenticated
    public List<GoalResult> goalResults(@Argument UUID budgetId, @Argument Integer year, @Argument Integer month) {
        return goalResultsService.getGoalResults(budgetId, year, month);
    }
}
