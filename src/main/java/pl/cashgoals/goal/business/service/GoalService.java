package pl.cashgoals.goal.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.BudgetFacade;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.expence.persistence.model.Category;
import pl.cashgoals.goal.business.model.GoalInput;
import pl.cashgoals.goal.persistence.model.Goal;
import pl.cashgoals.goal.persistence.repository.GoalRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final BudgetFacade budgetFacade;

    public List<Goal> getGoals(UUID budgetId) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.VIEW);
        return goalRepository.findAllByBudgetId(budgetId);
    }

    public List<Goal> updateGoals(UUID budgetId, List<GoalInput> goals) {
        budgetFacade.verifyCurrentUserRight(budgetId, Right.EDIT_GOALS);
        goalRepository.saveAll(
                goals.stream()
                        .map(
                                goalInput -> Goal.builder()
                                        .id(goalInput.id())
                                        .name(goalInput.name())
                                        .description(goalInput.description())
                                        .type(goalInput.type())
                                        .value(goalInput.value())
                                        .category(Category.builder().id(goalInput.category()).build())
                                        .budgetId(budgetId)
                                        .build()
                        )
                        .toList()
        );
        budgetFacade.updateBudgetInitializationStep(budgetId, Step.USERS_AND_RIGHTS);
        return goalRepository.findAllByBudgetId(budgetId);
    }
}
