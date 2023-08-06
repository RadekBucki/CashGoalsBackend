package pl.cashgoals.utils.business.message;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Map;
import java.util.ResourceBundle;

public final class MessageResolver {
    private MessageResolver() {
    }

    public static String getGraphQlMessage(String messageKey) {
        return getLocalizedMessage("GraphQlExceptionMessages", messageKey, Map.of());
    }

    public static String getGraphQlMessage(String messageKey, Map<String, String> messageArguments) {
        return getLocalizedMessage("GraphQlExceptionMessages", messageKey, messageArguments);
    }

    private static String getLocalizedMessage(String bundle, String messageKey, Map<String, String> messageArguments) {
        String message = ResourceBundle.getBundle(bundle, LocaleContextHolder.getLocale())
                .getString(messageKey);
        for (Map.Entry<String, String> entry : messageArguments.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
}
