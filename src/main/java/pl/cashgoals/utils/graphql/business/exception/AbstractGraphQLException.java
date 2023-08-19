package pl.cashgoals.utils.graphql.business.exception;

import graphql.ErrorClassification;
import lombok.Getter;
import pl.cashgoals.utils.i18n.message.business.MessageResolver;

import java.util.Map;

@Getter
public abstract class AbstractGraphQLException extends RuntimeException {
    protected final transient ErrorClassification errorType;
    protected final transient Map<String, String> messageArguments;

    protected AbstractGraphQLException(
            String message,
            ErrorClassification errorType,
            Map<String, String> messageArguments
    ) {
        super(message);
        this.errorType = errorType;
        this.messageArguments = messageArguments;
    }

    protected AbstractGraphQLException(String message, ErrorClassification errorType) {
        super(message);
        this.errorType = errorType;
        this.messageArguments = Map.of();
    }

    @Override
    public String getLocalizedMessage() {
        return MessageResolver.getGraphQlMessage(super.getLocalizedMessage(), messageArguments);
    }
}
