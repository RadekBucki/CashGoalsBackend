package pl.cashgoals.user.business.exception;

import pl.cashgoals.graphql.business.exception.GraphQLNotFoundException;

public class UserNotFound extends GraphQLNotFoundException {
    private static final String MESSAGE = "User with given username and password not found or not active";
    public UserNotFound() {
        super(MESSAGE);
    }
}
