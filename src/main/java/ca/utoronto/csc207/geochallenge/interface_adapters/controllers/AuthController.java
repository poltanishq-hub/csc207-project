package ca.utoronto.csc207.geochallenge.interface_adapters.controllers;

import ca.utoronto.csc207.geochallenge.use_cases.auth.LoginInputBoundary;
import ca.utoronto.csc207.geochallenge.use_cases.auth.LoginInputData;
import ca.utoronto.csc207.geochallenge.use_cases.auth.LogoutInputBoundary;
import ca.utoronto.csc207.geochallenge.use_cases.auth.RegisterInputBoundary;
import ca.utoronto.csc207.geochallenge.use_cases.auth.RegisterInputData;

import java.util.Arrays;
import java.util.Objects;

/** Translates account-form actions into use-case input data. */
public final class AuthController {
    private final RegisterInputBoundary registerUseCase;
    private final LoginInputBoundary loginUseCase;
    private final LogoutInputBoundary logoutUseCase;

    public AuthController(RegisterInputBoundary registerUseCase, LoginInputBoundary loginUseCase,
                          LogoutInputBoundary logoutUseCase) {
        this.registerUseCase = Objects.requireNonNull(registerUseCase, "Register use case is required.");
        this.loginUseCase = Objects.requireNonNull(loginUseCase, "Login use case is required.");
        this.logoutUseCase = Objects.requireNonNull(logoutUseCase, "Logout use case is required.");
    }

    public void register(String username, String displayName, String email, char[] password) {
        try {
            registerUseCase.execute(new RegisterInputData(username, displayName, email, password));
        }
        finally {
            clearPassword(password);
        }
    }

    public void login(String email, char[] password) {
        try {
            loginUseCase.execute(new LoginInputData(email, password));
        }
        finally {
            clearPassword(password);
        }
    }

    public void logout() {
        logoutUseCase.execute();
    }

    private static void clearPassword(char[] password) {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }
}
