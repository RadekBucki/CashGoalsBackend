package pl.cashgoals.budget.business.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.exception.BudgetNotFoundException;
import pl.cashgoals.budget.business.model.UserRightsInput;
import pl.cashgoals.budget.business.model.UserRightsOutput;
import pl.cashgoals.budget.persistence.model.Budget;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.model.Step;
import pl.cashgoals.budget.persistence.model.UserRight;
import pl.cashgoals.budget.persistence.repository.BudgetRepository;
import pl.cashgoals.budget.persistence.repository.UserRightsRepository;
import pl.cashgoals.user.business.UserFacade;
import pl.cashgoals.user.persistence.model.User;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final UserFacade userFacade;
    private final BudgetRepository budgetRepository;
    private final UserRightsRepository userRightsRepository;
    private final RightValidationService rightValidationService;

    @Transactional
    public Budget createBudget(String name, Principal principal) {
        User user = userFacade.getUserByEmail(principal.getName());

        Budget budget = Budget.builder()
                .name(name)
                .initializationStep(Step.INCOMES)
                .build();
        budgetRepository.save(budget);

        List<UserRight> userRights = userRightsRepository.setUserRightsToBudget(
                budget,
                user,
                List.of(Right.values())
        );
        budget.setUserRights(userRights);

        return budget;
    }

    public List<Right> getGetCurrentUserRightsFromBudget(Budget budget, Principal principal) {
        return userRightsRepository.getRights(budget.getId(), principal.getName());
    }

    public Budget getBudget(UUID id) {
        rightValidationService.verifyUserRight(id, Right.VIEW);
        return budgetRepository.findById(id).orElseThrow(BudgetNotFoundException::new);
    }

    public List<Budget> getBudgets(Principal principal) {
        return budgetRepository.findAllByUserEmailAndRight(principal.getName(), Right.VIEW);
    }

    public List<UserRightsOutput> getUserRights(UUID budgetId) {
        rightValidationService.verifyUserRight(budgetId, Right.VIEW);
        return mapToUserRightsOutput(userRightsRepository.findAllByBudgetId(budgetId));
    }

    public List<UserRightsOutput> updateUserRights(UUID budgetId, List<UserRightsInput> usersRights) {
        rightValidationService.verifyUserRight(budgetId, Right.EDIT_USERS_AND_RIGHTS);
        // TODO: Handle new users and simplify setUserRightsToBudget to operate on budgetId and user email
        List<UserRightsOutput> list = usersRights
                .stream()
                .map(userRightsInput -> {
                    User user = userFacade.getUserByEmail(userRightsInput.email());
                    List<UserRight> userRights = userRightsRepository.setUserRightsToBudget(
                            budgetRepository.findById(budgetId).orElseThrow(),
                            user,
                            userRightsInput.rights()
                    );
                    return new UserRightsOutput(user, userRights.stream().map(UserRight::getRight).toList());
                })
                .toList();
        updateBudgetInitializationStep(budgetId, Step.FINISHED);
        return list;
    }

    private static List<UserRightsOutput> mapToUserRightsOutput(List<UserRight> userRights) {
        return userRights.stream()
                .collect(Collectors.groupingBy(
                        UserRight::getUser,
                        Collectors.mapping(
                                UserRight::getRight,
                                Collectors.toList()
                        )
                ))
                .entrySet()
                .stream()
                .map(entry -> new UserRightsOutput(entry.getKey(), entry.getValue()))
                .toList();
    }

    public void updateBudgetInitializationStep(UUID budgetId, Step step) {
        Budget budget = budgetRepository.findById(budgetId).orElseThrow();
        if (budget.getInitializationStep() == Step.FINISHED) {
            return;
        }
        budget.setInitializationStep(step);
        budgetRepository.save(budget);
    }
}
