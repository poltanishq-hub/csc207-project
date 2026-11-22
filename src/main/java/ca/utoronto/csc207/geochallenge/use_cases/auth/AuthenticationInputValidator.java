package ca.utoronto.csc207.geochallenge.use_cases.auth;

/** Shared, provider-independent validation for account-access input. */
final class AuthenticationInputValidator {
    private static final String USERNAME_PATTERN = "[a-z0-9_]{3,24}";

    private AuthenticationInputValidator() {
    }

    static boolean isValidRegistration(RegisterInputData inputData) {
        return inputData != null
                && isValidUsername(inputData.getUsername())
                && isValidDisplayName(inputData.getDisplayName())
                && isValidEmail(inputData.getEmail())
                && isValidPassword(inputData.getPassword());
    }

    static boolean isValidLogin(LoginInputData inputData) {
        return inputData != null
                && isValidEmail(inputData.getEmail())
                && isValidPassword(inputData.getPassword());
    }

    private static boolean isValidUsername(String username) {
        return username != null && username.trim().matches(USERNAME_PATTERN);
    }

    private static boolean isValidDisplayName(String displayName) {
        return displayName != null
                && !displayName.trim().isEmpty()
                && displayName.trim().length() <= 64;
    }

    private static boolean isValidEmail(String email) {
        return email != null
                && email.length() <= 254
                && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    private static boolean isValidPassword(char[] password) {
        return password != null && password.length >= 8;
    }
}
