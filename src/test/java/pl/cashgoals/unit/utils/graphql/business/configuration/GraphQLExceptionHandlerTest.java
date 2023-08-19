package pl.cashgoals.unit.utils.graphql.business.configuration;

import graphql.GraphQLError;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.ResultPath;
import graphql.language.Field;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import pl.cashgoals.utils.graphql.business.configuration.GraphQLExceptionHandler;
import pl.cashgoals.utils.graphql.business.exception.AbstractGraphQLException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraphQLExceptionHandlerTest {
    GraphQLExceptionHandler graphQLExceptionHandler = new GraphQLExceptionHandler();
    DataFetchingEnvironment dataFetchingEnvironment = mock(DataFetchingEnvironment.class);

    @Nested
    @DisplayName("Multiple errors test")
    class MultipleErrorsTest {
        @BeforeEach
        void setUp() {
            SourceLocation sourceLocation = mock(SourceLocation.class);
            Field field = mock(Field.class);
            when(field.getSourceLocation()).thenReturn(sourceLocation);
            when(dataFetchingEnvironment.getField()).thenReturn(field);

            ReflectionTestUtils.setField(graphQLExceptionHandler, "activeProfile", "prod");
        }

        @DisplayName("should return multiple errors when ConstraintViolationException is thrown")
        @Test
        void shouldReturnMultipleErrorsWhenConstraintViolationExceptionIsThrown() {
            Path.Node node1 = mock(Path.Node.class);
            when(node1.getName()).thenReturn("path1");

            Path.Node node2 = mock(Path.Node.class);
            when(node2.getName()).thenReturn("path2");

            Path path = () -> List.of(node1, node2).iterator();

            ConstraintViolation<?> constraintViolation1 = mock(ConstraintViolation.class);
            when(constraintViolation1.getMessage()).thenReturn("message1");
            when(constraintViolation1.getPropertyPath()).thenReturn(path);

            ConstraintViolation<?> constraintViolation2 = mock(ConstraintViolation.class);
            when(constraintViolation2.getMessage()).thenReturn("message2");
            when(constraintViolation2.getPropertyPath()).thenReturn(path);

            Exception exception = new ConstraintViolationException(Set.of(
                    constraintViolation1,
                    constraintViolation2
            ));

            Mono<List<GraphQLError>> errors = graphQLExceptionHandler.resolveException(exception, dataFetchingEnvironment);
            List<GraphQLError> errorsList = Objects.requireNonNull(errors.block());

            assertEquals(2, errorsList.size());
            assertEquals(
                    1,
                    errorsList
                            .stream()
                            .filter(error -> error.getMessage().equals("message1"))
                            .filter(error -> error.getErrorType().equals(graphql.ErrorType.ValidationError))
                            .count()
            );
            assertEquals(
                    1,
                    errorsList
                            .stream()
                            .filter(error -> error.getMessage().equals("message2"))
                            .filter(error -> error.getErrorType().equals(graphql.ErrorType.ValidationError))
                            .count()
            );

        }
    }

    @Nested
    @DisplayName("Single error test")
    class SingleErrorTest {
        @BeforeEach
        void setUp() {
            SourceLocation sourceLocation = mock(SourceLocation.class);
            Field field = mock(Field.class);
            when(field.getSourceLocation()).thenReturn(sourceLocation);

            ResultPath resultPath = mock(ResultPath.class);
            ExecutionStepInfo executionStepInfo = mock(ExecutionStepInfo.class);
            when(executionStepInfo.getPath()).thenReturn(resultPath);

            when(dataFetchingEnvironment.getExecutionStepInfo()).thenReturn(executionStepInfo);
            when(dataFetchingEnvironment.getField()).thenReturn(field);

            ReflectionTestUtils.setField(graphQLExceptionHandler, "activeProfile", "prod");

            Locale.setDefault(
                    new Locale.Builder()
                            .setLanguage("test")
                            .build()
            );
        }

        @DisplayName("should return forbidden error when AccessDeniedException is thrown")
        @Test
        void shouldReturnSingleErrorWhenConstraintViolationExceptionIsThrown() {
            Exception exception = new AccessDeniedException("message");

            Mono<List<GraphQLError>> error = graphQLExceptionHandler.resolveException(exception, dataFetchingEnvironment);

            List<GraphQLError> graphQLErrors = Objects.requireNonNull(error.block());
            assertEquals(1, graphQLErrors.size());
            GraphQLError graphQLError = graphQLErrors.get(0);

            assertEquals("cashgoals.user.forbidden", graphQLError.getMessage());
            assertEquals(ErrorType.FORBIDDEN, graphQLError.getErrorType());
        }

        @DisplayName("should return unauthorized error when AuthenticationCredentialsNotFoundException is thrown")
        @Test
        void shouldReturnUnauthorizedErrorWhenAuthenticationCredentialsNotFoundExceptionIsThrown() {
            Exception exception = new AuthenticationCredentialsNotFoundException("message");

            Mono<List<GraphQLError>> error = graphQLExceptionHandler.resolveException(exception, dataFetchingEnvironment);

            List<GraphQLError> graphQLErrors = Objects.requireNonNull(error.block());
            assertEquals(1, graphQLErrors.size());
            GraphQLError graphQLError = graphQLErrors.get(0);

            assertEquals("cashgoals.user.unauthorized", graphQLError.getMessage());
            assertEquals(ErrorType.UNAUTHORIZED, graphQLError.getErrorType());
        }

        @DisplayName("should return bad request error when JwtException is thrown")
        @Test
        void shouldReturnBadRequestErrorWhenJwtExceptionIsThrown() {
            Exception exception = new AuthenticationCredentialsNotFoundException("message");

            Mono<List<GraphQLError>> error = graphQLExceptionHandler.resolveException(exception, dataFetchingEnvironment);

            List<GraphQLError> graphQLErrors = Objects.requireNonNull(error.block());
            assertEquals(1, graphQLErrors.size());
            GraphQLError graphQLError = graphQLErrors.get(0);

            assertEquals("cashgoals.user.unauthorized", graphQLError.getMessage());
            assertEquals(ErrorType.UNAUTHORIZED, graphQLError.getErrorType());
        }

        @DisplayName("should return proper error when AbstractGraphQLException is thrown")
        @ParameterizedTest
        @CsvSource({
                "cashgoals.user.not-found, NOT_FOUND",
                "cashgoals.user.bad-refresh-token, BAD_REQUEST",
                "cashgoals.user.forbidden, FORBIDDEN",
                "cashgoals.user.unauthorized, UNAUTHORIZED"
        })
        void shouldReturnProperErrorWhenAbstractGraphQLExceptionIsThrown(String message, ErrorType errorType) {
            Exception exception = new AbstractGraphQLException(
                    message,
                    errorType
            ) {
            };

            Mono<List<GraphQLError>> error = graphQLExceptionHandler.resolveException(exception, dataFetchingEnvironment);

            List<GraphQLError> graphQLErrors = Objects.requireNonNull(error.block());
            assertEquals(1, graphQLErrors.size());
            GraphQLError graphQLError = graphQLErrors.get(0);

            assertEquals(message, graphQLError.getMessage());
            assertEquals(errorType, graphQLError.getErrorType());
        }

        @DisplayName("should return internal error when not recognized exception is thrown")
        @Test
        void shouldReturnInternalErrorWhenNotRecognizedExceptionIsThrown() {
            Exception exception = new Exception("message");

            Mono<List<GraphQLError>> error = graphQLExceptionHandler.resolveException(exception, dataFetchingEnvironment);

            List<GraphQLError> graphQLErrors = Objects.requireNonNull(error.block());
            assertEquals(1, graphQLErrors.size());
            GraphQLError graphQLError = graphQLErrors.get(0);

            assertEquals("Internal server error", graphQLError.getMessage());
            assertEquals(ErrorType.INTERNAL_ERROR, graphQLError.getErrorType());
        }

        @DisplayName("should return internal server error details in dev profile")
        @Test
        void shouldReturnInternalServerErrorDetailsInDevProfile() {
            ReflectionTestUtils.setField(graphQLExceptionHandler, "activeProfile", "dev");
            Exception exception = new Exception("message");

            Mono<List<GraphQLError>> error = graphQLExceptionHandler.resolveException(exception, dataFetchingEnvironment);

            List<GraphQLError> graphQLErrors = Objects.requireNonNull(error.block());
            assertEquals(1, graphQLErrors.size());
            GraphQLError graphQLError = graphQLErrors.get(0);

            assertEquals("Internal server error", graphQLError.getMessage());
            assertEquals(ErrorType.INTERNAL_ERROR, graphQLError.getErrorType());
            assertEquals("Exception", graphQLError.getExtensions().get("exception"));
            assertEquals("message", graphQLError.getExtensions().get("message"));
            assertNotEquals(0, graphQLError.getExtensions().get("stackTrace"));
        }
    }
}