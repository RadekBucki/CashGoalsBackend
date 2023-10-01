package pl.cashgoals.user.business.exception;

import pl.cashgoals.utils.graphql.business.exception.GraphQLBadRequestException;

public class BadPasswordException extends GraphQLBadRequestException {
    private static final String MESSAGE = "cashgoals.user.bad-password";
    public BadPasswordException() {
        super(MESSAGE);
    }
}
