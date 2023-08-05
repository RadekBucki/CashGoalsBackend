package pl.cashgoals.user.business.exception;

import pl.cashgoals.graphql.business.exception.GraphQLNotFoundException;

public class UserNotFoundException extends GraphQLNotFoundException {
    private static final String MESSAGE = "cashgoals.user.not-found";
    public UserNotFoundException() {
        super(MESSAGE);
    }
}
