package ca.utoronto.csc207.geochallenge.interface_adapters.presenters;

import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.AppNavigator;
import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.AppScreen;
import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.LoginViewModel;
import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.RegisterViewModel;
import ca.utoronto.csc207.geochallenge.use_cases.auth.AuthenticationFailure;
import ca.utoronto.csc207.geochallenge.use_cases.auth.AuthenticationOutputData;
import ca.utoronto.csc207.geochallenge.use_cases.auth.LoginOutputBoundary;
import ca.utoronto.csc207.geochallenge.use_cases.auth.LogoutOutputBoundary;
import ca.utoronto.csc207.geochallenge.use_cases.auth.RegisterOutputBoundary;

import java.util.Objects;

/** Presents account-access outcomes as form state and navigation requests. */
public final class AuthPresenter {
    private final LoginViewModel loginViewModel;
    private final RegisterViewModel registerViewModel;
    private final AppNavigator navigator;

    public AuthPresenter(LoginViewModel loginViewModel, RegisterViewModel registerViewModel,
                         AppNavigator navigator) {
        this.loginViewModel = Objects.requireNonNull(loginViewModel, "Login view model is required.");
        this.registerViewModel = Objects.requireNonNull(registerViewModel, "Register view model is required.");
        this.navigator = Objects.requireNonNull(navigator, "Navigator is required.");
    }

    public RegisterOutputBoundary registerOutputBoundary() {
        return new RegisterOutputBoundary() {
            @Override
            public void prepareSuccessView(AuthenticationOutputData outputData) {
                presentRegistrationSuccess(outputData);
            }

            @Override
            public void prepareFailView(AuthenticationFailure failure) {
                presentRegistrationFailure(failure);
            }
        };
    }

    public LoginOutputBoundary loginOutputBoundary() {
        return new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(AuthenticationOutputData outputData) {
                presentLoginSuccess(outputData);
            }

            @Override
            public void prepareFailView(AuthenticationFailure failure) {
                presentLoginFailure(failure);
            }
        };
    }

    public LogoutOutputBoundary logoutOutputBoundary() {
        return new LogoutOutputBoundary() {
            @Override
            public void prepareSuccessView() {
                presentLogoutSuccess();
            }

            @Override
            public void prepareFailView(AuthenticationFailure failure) {
                presentLogoutFailure(failure);
            }
        };
    }

    private void presentRegistrationSuccess(AuthenticationOutputData outputData) {
        Objects.requireNonNull(outputData, "Authentication output is required.");
        registerViewModel.setErrorMessage("");
        registerViewModel.fireStateChanged();
        navigator.show(AppScreen.HOME);
    }

    private void presentRegistrationFailure(AuthenticationFailure failure) {
        registerViewModel.setErrorMessage(registrationMessage(failure));
        registerViewModel.fireStateChanged();
    }

    private void presentLoginSuccess(AuthenticationOutputData outputData) {
        Objects.requireNonNull(outputData, "Authentication output is required.");
        loginViewModel.setErrorMessage("");
        loginViewModel.fireStateChanged();
        navigator.show(AppScreen.HOME);
    }

    private void presentLoginFailure(AuthenticationFailure failure) {
        loginViewModel.setErrorMessage(loginMessage(failure));
        loginViewModel.fireStateChanged();
    }

    private void presentLogoutSuccess() {
        loginViewModel.setErrorMessage("");
        loginViewModel.fireStateChanged();
        navigator.show(AppScreen.LOGIN);
    }

    private void presentLogoutFailure(AuthenticationFailure failure) {
        loginViewModel.setErrorMessage("Signed out locally, but the server could not be reached.");
        loginViewModel.fireStateChanged();
        navigator.show(AppScreen.LOGIN);
    }

    private static String registrationMessage(AuthenticationFailure failure) {
        if (failure == null) {
            return "We could not create your account. Please try again.";
        }
        switch (failure) {
            case INVALID_INPUT:
                return "Check your username, display name, email, and password.";
            case USERNAME_UNAVAILABLE:
                return "That username is already taken.";
            case EMAIL_UNAVAILABLE:
                return "That email address is already registered.";
            case SERVICE_UNAVAILABLE:
                return "The service is unavailable. Please try again.";
            default:
                return "We could not create your account. Please try again.";
        }
    }

    private static String loginMessage(AuthenticationFailure failure) {
        if (failure == AuthenticationFailure.INVALID_INPUT) {
            return "Enter a valid email address and password.";
        }
        if (failure == AuthenticationFailure.INVALID_CREDENTIALS) {
            return "Email or password is incorrect.";
        }
        if (failure == AuthenticationFailure.SERVICE_UNAVAILABLE) {
            return "The service is unavailable. Please try again.";
        }
        return "We could not log you in. Please try again.";
    }
}
