package ca.utoronto.csc207.geochallenge.use_cases.auth;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginInteractorTest {
    @Test
    void logsInValidPlayerStartsSessionAndPresentsSuccess() {
        PlayerProfile profile = new PlayerProfile(UUID.randomUUID(), "traveler_1", "Traveler");
        FakeAuthGateway gateway = new FakeAuthGateway(AuthenticationResult.success(profile));
        FakeSession session = new FakeSession();
        RecordingLoginPresenter presenter = new RecordingLoginPresenter();
        LoginInteractor interactor = new LoginInteractor(gateway, session, presenter);
        LoginInputData input = new LoginInputData("traveler@example.com", "password1".toCharArray());

        interactor.execute(input);

        assertTrue(gateway.loginCalled);
        assertEquals("traveler@example.com", gateway.loginRequest.getEmail());
        assertEquals(profile, session.currentUser().orElseThrow());
        assertEquals(profile, presenter.success.getPlayerProfile());
        assertFalse(presenter.failure.isPresent());
        assertArrayEquals(new char[9], input.getPassword());
    }

    @Test
    void rejectsInvalidLoginWithoutCallingGateway() {
        FakeAuthGateway gateway = new FakeAuthGateway(AuthenticationResult.failure(AuthenticationFailure.UNEXPECTED_FAILURE));
        FakeSession session = new FakeSession();
        RecordingLoginPresenter presenter = new RecordingLoginPresenter();
        LoginInteractor interactor = new LoginInteractor(gateway, session, presenter);

        interactor.execute(new LoginInputData("not-an-email", "password1".toCharArray()));

        assertFalse(gateway.loginCalled);
        assertFalse(session.currentUser().isPresent());
        assertEquals(AuthenticationFailure.INVALID_INPUT, presenter.failure.orElseThrow());
    }

    @Test
    void presentsInvalidCredentialsWithoutStartingSession() {
        FakeAuthGateway gateway = new FakeAuthGateway(AuthenticationResult.failure(AuthenticationFailure.INVALID_CREDENTIALS));
        FakeSession session = new FakeSession();
        RecordingLoginPresenter presenter = new RecordingLoginPresenter();
        LoginInteractor interactor = new LoginInteractor(gateway, session, presenter);

        interactor.execute(new LoginInputData("traveler@example.com", "password1".toCharArray()));

        assertTrue(gateway.loginCalled);
        assertFalse(session.currentUser().isPresent());
        assertEquals(AuthenticationFailure.INVALID_CREDENTIALS, presenter.failure.orElseThrow());
    }

    @Test
    void convertsUnexpectedGatewayFailureToSafePresenterFailure() {
        FakeAuthGateway gateway = new FakeAuthGateway(null);
        gateway.throwOnLogin = true;
        FakeSession session = new FakeSession();
        RecordingLoginPresenter presenter = new RecordingLoginPresenter();

        new LoginInteractor(gateway, session, presenter).execute(
                new LoginInputData("traveler@example.com", "password1".toCharArray()));

        assertFalse(session.currentUser().isPresent());
        assertEquals(AuthenticationFailure.SERVICE_UNAVAILABLE, presenter.failure.orElseThrow());
    }

    static final class FakeAuthGateway implements AuthGateway {
        private final AuthenticationResult loginResult;
        private boolean loginCalled;
        private boolean throwOnLogin;
        private LoginRequest loginRequest;

        FakeAuthGateway(AuthenticationResult loginResult) {
            this.loginResult = loginResult;
        }

        @Override
        public AuthenticationResult register(RegistrationRequest request) {
            throw new UnsupportedOperationException("Not used by this test.");
        }

        @Override
        public AuthenticationResult login(LoginRequest request) {
            loginCalled = true;
            if (throwOnLogin) {
                throw new IllegalStateException("Connection failed");
            }
            loginRequest = request;
            return loginResult;
        }

        @Override
        public void logout() {
            throw new UnsupportedOperationException("Not used by this test.");
        }
    }

    static final class FakeSession implements UserSession {
        private PlayerProfile currentPlayer;

        @Override
        public Optional<PlayerProfile> currentUser() {
            return Optional.ofNullable(currentPlayer);
        }

        @Override
        public void start(PlayerProfile playerProfile) {
            currentPlayer = playerProfile;
        }

        @Override
        public void clear() {
            currentPlayer = null;
        }
    }

    static final class RecordingLoginPresenter implements LoginOutputBoundary {
        private AuthenticationOutputData success;
        private Optional<AuthenticationFailure> failure = Optional.empty();

        @Override
        public void prepareSuccessView(AuthenticationOutputData outputData) {
            success = outputData;
        }

        @Override
        public void prepareFailView(AuthenticationFailure authenticationFailure) {
            failure = Optional.of(authenticationFailure);
        }
    }
}
