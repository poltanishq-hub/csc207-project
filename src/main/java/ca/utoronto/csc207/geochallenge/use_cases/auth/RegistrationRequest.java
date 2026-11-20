package ca.utoronto.csc207.geochallenge.use_cases.auth;

import java.util.Arrays;

/**
 * Short-lived registration credentials sent to an authentication provider.
 *
 * <p>The password is copied defensively and is intentionally omitted from {@code toString}.
 * Callers should clear the request after the provider call completes.</p>
 */
public final class RegistrationRequest {
    private final String username;
    private final String displayName;
    private final String email;
    private final char[] password;

    public RegistrationRequest(String username, String displayName, String email, char[] password) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.password = password == null ? null : Arrays.copyOf(password, password.length);
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
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
