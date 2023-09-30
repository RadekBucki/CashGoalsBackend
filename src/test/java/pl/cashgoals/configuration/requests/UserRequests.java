package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;
import pl.cashgoals.user.persistence.model.Theme;

import java.util.Locale;
import java.util.Map;

public class UserRequests {
    private final GraphQlTester graphQlTester;

    public UserRequests(GraphQlTester graphQlTester) {
        this.graphQlTester = graphQlTester;
    }

    public GraphQlTester.Response createUser(
            String name,
            String password,
            Theme theme,
            String email
    ) {
        Map<String, String> userInput = Map.of(
                "name", name,
                "password", password,
                "email", email,
                "theme", theme.name(),
                "activationUrl", "http://some-web.com/activate"
        );
        return graphQlTester.documentName("user/createUser")
                .variable("input", userInput)
                .execute();
    }

    public GraphQlTester.Response login(String email, String password) {
        return graphQlTester.documentName("user/login")
                .variable("email", email)
                .variable("password", password)
                .execute();
    }

    public GraphQlTester.Response refreshToken(String refreshToken) {
        return graphQlTester
                .documentName("user/refreshToken")
                .variable("token", refreshToken)
                .execute();
    }

    public GraphQlTester.Response updateUser(
            String name,
            String password,
            Theme theme,
            String email,
            Locale locale
    ) {
        Map<String, String> input = Map.of(
                "name", name,
                "password", password,
                "email", email,
                "theme", theme.name(),
                "locale", locale.toLanguageTag()
        );
        return graphQlTester.documentName("user/updateUser")
                .variable("input", input)
                .execute();
    }

    public GraphQlTester.Response getUser() {
        return graphQlTester.documentName("user/user")
                .execute();
    }

    public GraphQlTester.Response activateUser(String email, String token) {
        return graphQlTester.documentName("user/activateUser")
                .variable("email", email)
                .variable("token", token)
                .execute();
    }

    public GraphQlTester.Response requestPasswordReset(String email, String resetUrl) {
        return graphQlTester.documentName("user/requestPasswordReset")
                .variable("email", email)
                .variable("resetUrl", resetUrl)
                .execute();
    }

    public GraphQlTester.Response resetPassword(String email, String token, String password) {
        return graphQlTester.documentName("user/resetPassword")
                .variable("email", email)
                .variable("token", token)
                .variable("password", password)
                .execute();
    }
}
