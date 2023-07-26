package pl.cashgoals.graphql.business.exception;

import org.springframework.graphql.execution.ErrorType;

public class GraphQLBadRequestException extends AbstractGraphQLException {

    public GraphQLBadRequestException(String message) {
        super(message, ErrorType.BAD_REQUEST);
    }
}
