package pl.cashgoals.user.business.exception;

import pl.cashgoals.utils.business.exception.GraphQLNotFoundException;

public class UserNotFoundException extends GraphQLNotFoundException {
    private static final String MESSAGE = "cashgoals.user.not-found";
    public UserNotFoundException() {
        super(MESSAGE);
    }
}
