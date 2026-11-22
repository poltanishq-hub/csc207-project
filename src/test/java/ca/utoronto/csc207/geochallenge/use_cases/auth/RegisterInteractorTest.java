package ca.utoronto.csc207.geochallenge.use_cases.auth;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterInteractorTest {
    @Test
    void registersValidPlayerStartsSessionAndPresentsSuccess() {
        PlayerProfile profile = new PlayerProfile(UUID.randomUUID(), "traveler_1", "Traveler");
        FakeAuthGateway gateway = new FakeAuthGateway(AuthenticationResult.success(profile));
        FakeSession session = new FakeSession();
        RecordingRegisterPresenter presenter = new RecordingRegisterPresenter();
        RegisterInteractor interactor = new RegisterInteractor(gateway, session, presenter);
        RegisterInputData input = new RegisterInputData(
                "traveler_1", "Traveler", "traveler@example.com", "password1".toCharArray());

        interactor.execute(input);

        assertTrue(gateway.registerCalled);
        assertEquals("traveler_1", gateway.registrationRequest.getUsername());
        assertEquals("Traveler", gateway.registrationRequest.getDisplayName());
        assertEquals("traveler@example.com", gateway.registrationRequest.getEmail());
        assertEquals(profile, session.currentUser().orElseThrow());
        assertEquals(profile, presenter.success.getPlayerProfile());
        assertFalse(presenter.failure.isPresent());
        assertArrayEquals(new char[9], input.getPassword());
    }

    @Test
    void rejectsInvalidRegistrationWithoutCallingGateway() {
        FakeAuthGateway gateway = new FakeAuthGateway(AuthenticationResult.failure(AuthenticationFailure.UNEXPECTED_FAILURE));
        FakeSession session = new FakeSession();
        RecordingRegisterPresenter presenter = new RecordingRegisterPresenter();
        RegisterInteractor interactor = new RegisterInteractor(gateway, session, presenter);
        RegisterInputData input = new RegisterInputData(
                "not valid", "Traveler", "traveler@example.com", "password1".toCharArray());

        interactor.execute(input);

        assertFalse(gateway.registerCalled);
        assertFalse(session.currentUser().isPresent());
        assertEquals(AuthenticationFailure.INVALID_INPUT, presenter.failure.orElseThrow());
        assertArrayEquals(new char[9], input.getPassword());
    }

    @Test
    void presentsGatewayFailureWithoutStartingSession() {
        FakeAuthGateway gateway = new FakeAuthGateway(AuthenticationResult.failure(AuthenticationFailure.USERNAME_UNAVAILABLE));
        FakeSession session = new FakeSession();
        RecordingRegisterPresenter presenter = new RecordingRegisterPresenter();
        RegisterInteractor interactor = new RegisterInteractor(gateway, session, presenter);

        interactor.execute(new RegisterInputData(
                "traveler_1", "Traveler", "traveler@example.com", "password1".toCharArray()));

        assertTrue(gateway.registerCalled);
        assertFalse(session.currentUser().isPresent());
        assertEquals(AuthenticationFailure.USERNAME_UNAVAILABLE, presenter.failure.orElseThrow());
    }

    @Test
    void convertsUnexpectedGatewayFailureToSafePresenterFailure() {
        FakeAuthGateway gateway = new FakeAuthGateway(null);
        gateway.throwOnRegister = true;
        FakeSession session = new FakeSession();
        RecordingRegisterPresenter presenter = new RecordingRegisterPresenter();

        new RegisterInteractor(gateway, session, presenter).execute(new RegisterInputData(
                "traveler_1", "Traveler", "traveler@example.com", "password1".toCharArray()));

        assertFalse(session.currentUser().isPresent());
        assertEquals(AuthenticationFailure.SERVICE_UNAVAILABLE, presenter.failure.orElseThrow());
    }

    static final class FakeAuthGateway implements AuthGateway {
        private final AuthenticationResult registrationResult;
        private boolean registerCalled;
        private boolean throwOnRegister;
        private RegistrationRequest registrationRequest;

        FakeAuthGateway(AuthenticationResult registrationResult) {
            this.registrationResult = registrationResult;
        }

        @Override
        public AuthenticationResult register(RegistrationRequest request) {
            registerCalled = true;
            if (throwOnRegister) {
                throw new IllegalStateException("Connection failed");
            }
            registrationRequest = request;
            return registrationResult;
        }

        @Override
        public AuthenticationResult login(LoginRequest request) {
            throw new UnsupportedOperationException("Not used by this test.");
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

    static final class RecordingRegisterPresenter implements RegisterOutputBoundary {
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
