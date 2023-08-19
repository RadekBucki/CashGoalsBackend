package pl.cashgoals.user.business.exception;

import pl.cashgoals.utils.graphql.business.exception.GraphQLBadRequestException;

public class BadRefreshTokenException extends GraphQLBadRequestException {
    private static final String MESSAGE = "cashgoals.user.bad-refresh-token";
    public BadRefreshTokenException() {
        super(MESSAGE);
    }
}
