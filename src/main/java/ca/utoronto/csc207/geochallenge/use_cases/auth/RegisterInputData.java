package ca.utoronto.csc207.geochallenge.use_cases.auth;

import java.util.Arrays;

/** Data submitted by a player attempting to create an account. */
public final class RegisterInputData {
    private final String username;
    private final String displayName;
    private final String email;
    private final char[] password;

    public RegisterInputData(String username, String displayName, String email, char[] password) {
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

    public char[] getPassword() {
        return password == null ? null : Arrays.copyOf(password, password.length);
    }

    /** Overwrites the in-memory password after the use case finishes. */
    public void clearPassword() {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }
}
