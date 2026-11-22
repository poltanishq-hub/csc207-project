package ca.utoronto.csc207.geochallenge.interface_adapters.presenters;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.AppNavigator;
import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.AppScreen;
import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.LoginViewModel;
import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.RegisterViewModel;
import ca.utoronto.csc207.geochallenge.use_cases.auth.AuthenticationFailure;
import ca.utoronto.csc207.geochallenge.use_cases.auth.AuthenticationOutputData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthPresenterTest {
    @Test
    void registrationFailureUpdatesOnlyRegistrationFormWithSafeMessage() {
        LoginViewModel login = new LoginViewModel();
        login.setErrorMessage("Existing login error");
        RegisterViewModel register = new RegisterViewModel();
        RecordingNavigator navigator = new RecordingNavigator();
        AuthPresenter presenter = new AuthPresenter(login, register, navigator);

        presenter.registerOutputBoundary().prepareFailView(AuthenticationFailure.USERNAME_UNAVAILABLE);

        assertEquals("That username is already taken.", register.getErrorMessage());
        assertEquals("Existing login error", login.getErrorMessage());
        assertNull(navigator.lastScreen);
    }

    @Test
    void successfulLoginClearsLoginErrorAndNavigatesHome() {
        LoginViewModel login = new LoginViewModel();
        login.setErrorMessage("Old error");
        RegisterViewModel register = new RegisterViewModel();
        RecordingNavigator navigator = new RecordingNavigator();
        AuthPresenter presenter = new AuthPresenter(login, register, navigator);

        presenter.loginOutputBoundary().prepareSuccessView(outputData());

        assertEquals("", login.getErrorMessage());
        assertEquals(AppScreen.HOME, navigator.lastScreen);
    }

    @Test
    void invalidCredentialsRemainGenericAndDoNotExposeProviderDetails() {
        LoginViewModel login = new LoginViewModel();
        RegisterViewModel register = new RegisterViewModel();
        AuthPresenter presenter = new AuthPresenter(login, register, new RecordingNavigator());

        presenter.loginOutputBoundary().prepareFailView(AuthenticationFailure.INVALID_CREDENTIALS);

        assertEquals("Email or password is incorrect.", login.getErrorMessage());
    }

    @Test
    void failedProviderLogoutStillReturnsPlayerToLogin() {
        LoginViewModel login = new LoginViewModel();
        RegisterViewModel register = new RegisterViewModel();
        RecordingNavigator navigator = new RecordingNavigator();
        AuthPresenter presenter = new AuthPresenter(login, register, navigator);

        presenter.logoutOutputBoundary().prepareFailView(AuthenticationFailure.SERVICE_UNAVAILABLE);

        assertEquals("Signed out locally, but the server could not be reached.", login.getErrorMessage());
        assertEquals(AppScreen.LOGIN, navigator.lastScreen);
    }

    private static AuthenticationOutputData outputData() {
        return new AuthenticationOutputData(
                new PlayerProfile(UUID.randomUUID(), "traveler_1", "Traveler"));
    }

    private static final class RecordingNavigator implements AppNavigator {
        private AppScreen lastScreen;

        @Override
        public void show(AppScreen screen) {
            lastScreen = screen;
        }
    }
}
