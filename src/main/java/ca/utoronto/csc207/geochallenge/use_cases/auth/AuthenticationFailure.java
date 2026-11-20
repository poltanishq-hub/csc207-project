package ca.utoronto.csc207.geochallenge.use_cases.auth;

/** Expected authentication failures that can be mapped to safe UI messages. */
public enum AuthenticationFailure {
    INVALID_INPUT,
    USERNAME_UNAVAILABLE,
    EMAIL_UNAVAILABLE,
    INVALID_CREDENTIALS,
    SERVICE_UNAVAILABLE,
    UNEXPECTED_FAILURE
}
