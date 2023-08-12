package pl.cashgoals.configuration.requests;

import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Map;

public class UserRequests {
    private final GraphQlTester graphQlTester;

    public UserRequests(GraphQlTester graphQlTester) {
        this.graphQlTester = graphQlTester;
    }

    public GraphQlTester.Response createUser(
            String username,
            String password,
            String email
    ) {
        Map<String, String> userInput = Map.of(
                "username", username,
                "password", password,
                "email", email,
                "activationUrl", "http://some-web.com/activate"
        );
        return graphQlTester.documentName("user/createUser")
                .variable("userInput", userInput)
                .execute();
    }

    public GraphQlTester.Response login(String username, String password) {
        return graphQlTester.documentName("user/login")
                .variable("username", username)
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
            String username,
            String password,
            String email
    ) {
        Map<String, String> userInput = Map.of(
                "username", username,
                "password", password,
                "email", email
        );
        return graphQlTester.documentName("user/updateUser")
                .variable("userInput", userInput)
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
