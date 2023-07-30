package pl.cashgoals.user.business.exception;

import pl.cashgoals.graphql.business.exception.GraphQLNotFoundException;

public class UserNotFoundException extends GraphQLNotFoundException {
    private static final String MESSAGE = "User with given username and password not found or not active";
    public UserNotFoundException() {
        super(MESSAGE);
    }
}
