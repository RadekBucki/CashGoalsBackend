package pl.cashgoals.user.business.exception;

public class AuthenticationException extends RuntimeException {
    private static final String MESSAGE = "User with given username and password not found or not active";
    public AuthenticationException() {
        super(MESSAGE);
    }
}
