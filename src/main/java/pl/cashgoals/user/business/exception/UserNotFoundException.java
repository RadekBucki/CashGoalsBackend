package pl.cashgoals.user.business.exception;

import pl.cashgoals.utils.graphql.business.exception.GraphQLNotFoundException;

import java.util.Map;

public class UserNotFoundException extends GraphQLNotFoundException {
    private static final String MESSAGE = "cashgoals.user.not-found";
    public UserNotFoundException(String user) {
        super(MESSAGE, Map.of("user", user));
    }
}
