package ca.utoronto.csc207.geochallenge.use_cases.auth;

import java.util.Arrays;

/**
 * Short-lived login credentials sent to an authentication provider.
 *
 * <p>The email field is the provider login identifier; the username remains the public player
 * identifier displayed throughout the application.</p>
 */
public final class LoginRequest {
    private final String email;
    private final char[] password;

    public LoginRequest(String email, char[] password) {
        this.email = email;
        this.password = password == null ? null : Arrays.copyOf(password, password.length);
    }

    public String getEmail() {
        return email;
    }

    /** Returns a copy so consumers cannot mutate this request's internal credential. */
    public char[] getPassword() {
        return password == null ? null : Arrays.copyOf(password, password.length);
    }

    /** Overwrites the in-memory password after the provider request has finished. */
    public void clearPassword() {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }
}
