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
            String email,
            String firstname,
            String lastname
    ) {
        Map<String, String> userInput = Map.of(
                "username", username,
                "password", password,
                "email", email,
                "firstname", firstname,
                "lastname", lastname
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
            String email,
            String firstname,
            String lastname
    ) {
        Map<String, String> userInput = Map.of(
                "username", username,
                "password", password,
                "email", email,
                "firstname", firstname,
                "lastname", lastname
        );
        return graphQlTester.documentName("user/updateUser")
                .variable("userInput", userInput)
                .execute();
    }

    public GraphQlTester.Response getUser() {
        return graphQlTester.documentName("user/user")
                .execute();
    }
}
