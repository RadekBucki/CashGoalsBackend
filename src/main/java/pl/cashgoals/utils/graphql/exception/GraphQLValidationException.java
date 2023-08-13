package pl.cashgoals.utils.graphql.exception;

public class GraphQLValidationException extends AbstractGraphQLException {

    public GraphQLValidationException(String message) {
        super(message, graphql.ErrorType.ValidationError);
    }
}
