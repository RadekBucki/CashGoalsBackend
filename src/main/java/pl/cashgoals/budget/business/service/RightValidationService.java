package pl.cashgoals.budget.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.cashgoals.budget.business.exception.BudgetNotFoundException;
import pl.cashgoals.budget.persistence.model.Right;
import pl.cashgoals.budget.persistence.repository.UserRightsRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RightValidationService {
    private final UserRightsRepository userRightsRepository;

    public void verifyUserRight(UUID budgetId, Right right) {
        if (Boolean.FALSE.equals(userRightsRepository.hasUserRight(budgetId, getLoggedUserEmail(), right))) {
            throw new BudgetNotFoundException();
        }
    }

    public String getLoggedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }
}
