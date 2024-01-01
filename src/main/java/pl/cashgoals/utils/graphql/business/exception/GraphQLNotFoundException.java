package pl.cashgoals.utils.graphql.business.exception;

import org.springframework.graphql.execution.ErrorType;

import java.util.Map;

public class GraphQLNotFoundException extends AbstractGraphQLException {
    public GraphQLNotFoundException(String message, Map<String, String> arguments) {
        super(message, ErrorType.NOT_FOUND, arguments);
    }

    public GraphQLNotFoundException(String message) {
        super(message, ErrorType.NOT_FOUND);
    }
}
