package pl.cashgoals.graphql.business.exception;

public class GraphQLValidationException extends AbstractGraphQLException {

    public GraphQLValidationException(String message) {
        super(message, graphql.ErrorType.ValidationError);
    }
}
