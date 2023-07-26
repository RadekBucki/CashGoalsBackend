package pl.cashgoals.graphql.business.exception;

import graphql.ErrorClassification;
import lombok.Getter;

public abstract class AbstractGraphQLException extends RuntimeException {
    @Getter
    protected final transient ErrorClassification errorType;

    protected AbstractGraphQLException(String message, ErrorClassification errorType) {
        super(message);
        this.errorType = errorType;
    }
}
