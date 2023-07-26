package pl.cashgoals.graphql.business.configuration;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import pl.cashgoals.graphql.business.exception.AbstractGraphQLException;

import java.util.*;

@Component
@Slf4j
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    protected List<GraphQLError> resolveToMultipleErrors(
            @NotNull Throwable throwable,
            @NotNull DataFetchingEnvironment environment
    ) {
        if (throwable instanceof ConstraintViolationException constraintViolationException) {
            return constraintViolationException.getConstraintViolations()
                    .stream()
                    .map(constraintViolation -> {
                        List<String> propertyPath = new ArrayList<>();
                        constraintViolation.getPropertyPath()
                                .forEach(node -> propertyPath.add(node.getName()));
                        return GraphqlErrorBuilder.newError()
                                .errorType(graphql.ErrorType.ValidationError)
                                .message(constraintViolation.getMessage())
                                .path(Arrays.asList(propertyPath.toArray()))
                                .location(environment.getField().getSourceLocation())
                                .build();
                    })
                    .toList();
        }
        return super.resolveToMultipleErrors(throwable, environment);
    }

    @Override
    protected GraphQLError resolveToSingleError(
            @NotNull Throwable throwable,
            @NotNull DataFetchingEnvironment environment
    ) {
        ErrorClassification errorType = ErrorType.INTERNAL_ERROR;
        if (throwable instanceof AccessDeniedException) {
            errorType = ErrorType.FORBIDDEN;
        } else if (throwable instanceof AbstractGraphQLException abstractGraphQLException) {
            errorType = abstractGraphQLException.getErrorType();
        }

        GraphqlErrorBuilder<?> graphqlErrorBuilder = GraphqlErrorBuilder.newError()
                .errorType(errorType)
                .message(throwable.getMessage())
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation());
        if (errorType == ErrorType.INTERNAL_ERROR) {
            graphqlErrorBuilder = graphqlErrorBuilder.message("Internal server error");
            log.error(throwable.getMessage(), throwable);
        }
        if (errorType == ErrorType.INTERNAL_ERROR && activeProfile.equals("dev")) {
            Map<String, Object> extensions = new LinkedHashMap<>();

            extensions.put("exception", throwable.getClass().getSimpleName());
            extensions.put("message", throwable.getMessage());
            extensions.put("stacktrace", Arrays.asList(throwable.getStackTrace()));

            graphqlErrorBuilder = graphqlErrorBuilder.extensions(extensions);
        }

        return graphqlErrorBuilder.build();
    }

}
