package pl.cashgoals.utils.graphql.exception;

import graphql.ErrorClassification;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;
import pl.cashgoals.utils.i18n.message.MessageResolver;

import java.util.Map;
import java.util.ResourceBundle;

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
        String message = ResourceBundle.getBundle("GraphQlExceptionMessages", LocaleContextHolder.getLocale())
                .getString(super.getLocalizedMessage());
        for (Map.Entry<String, String> entry : messageArguments.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return MessageResolver.getGraphQlMessage(super.getLocalizedMessage(), messageArguments);
    }
}
