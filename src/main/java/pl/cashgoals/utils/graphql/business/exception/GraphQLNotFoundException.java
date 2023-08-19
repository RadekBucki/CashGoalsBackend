package pl.cashgoals.utils.graphql.business.exception;

import org.springframework.graphql.execution.ErrorType;

public class GraphQLNotFoundException extends AbstractGraphQLException {

    public GraphQLNotFoundException(String message) {
        super(message, ErrorType.NOT_FOUND);
    }
}
