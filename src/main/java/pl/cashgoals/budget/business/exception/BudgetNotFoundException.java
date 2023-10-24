package pl.cashgoals.budget.business.exception;

import pl.cashgoals.utils.graphql.business.exception.GraphQLNotFoundException;

public class BudgetNotFoundException extends GraphQLNotFoundException {
    private static final String MESSAGE = "cashgoals.budget.not-found";
    public BudgetNotFoundException() {
        super(MESSAGE);
    }
}
