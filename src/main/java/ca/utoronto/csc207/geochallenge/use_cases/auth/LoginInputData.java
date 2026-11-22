package ca.utoronto.csc207.geochallenge.use_cases.auth;

import java.util.Arrays;

/** Data submitted by a returning player attempting to log in. */
public final class LoginInputData {
    private final String email;
    private final char[] password;

    public LoginInputData(String email, char[] password) {
        this.email = email;
        this.password = password == null ? null : Arrays.copyOf(password, password.length);
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
