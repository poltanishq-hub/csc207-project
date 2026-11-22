package ca.utoronto.csc207.geochallenge.use_cases.auth;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogoutInteractorTest {
    @Test
    void clearsLocalSessionAfterSuccessfulProviderLogout() {
        FakeAuthGateway gateway = new FakeAuthGateway(false);
        FakeSession session = new FakeSession(new PlayerProfile(UUID.randomUUID(), "traveler_1", "Traveler"));
        RecordingLogoutPresenter presenter = new RecordingLogoutPresenter();

        new LogoutInteractor(gateway, session, presenter).execute();

        assertTrue(gateway.logoutCalled);
        assertFalse(session.currentUser().isPresent());
        assertTrue(presenter.successCalled);
        assertFalse(presenter.failure.isPresent());
    }

    @Test
    void clearsLocalSessionEvenWhenProviderLogoutFails() {
        FakeAuthGateway gateway = new FakeAuthGateway(true);
        FakeSession session = new FakeSession(new PlayerProfile(UUID.randomUUID(), "traveler_1", "Traveler"));
        RecordingLogoutPresenter presenter = new RecordingLogoutPresenter();

        new LogoutInteractor(gateway, session, presenter).execute();

        assertTrue(gateway.logoutCalled);
        assertFalse(session.currentUser().isPresent());
        assertFalse(presenter.successCalled);
        assertEquals(AuthenticationFailure.SERVICE_UNAVAILABLE, presenter.failure.orElseThrow());
    }

    private static final class FakeAuthGateway implements AuthGateway {
        private final boolean shouldFail;
        private boolean logoutCalled;

        FakeAuthGateway(boolean shouldFail) {
            this.shouldFail = shouldFail;
        }

        @Override
        public AuthenticationResult register(RegistrationRequest request) {
            throw new UnsupportedOperationException("Not used by this test.");
        }

        @Override
        public AuthenticationResult login(LoginRequest request) {
            throw new UnsupportedOperationException("Not used by this test.");
        }

        @Override
        public void logout() {
            logoutCalled = true;
            if (shouldFail) {
                throw new IllegalStateException("Provider unavailable");
            }
        }
    }

    private static final class FakeSession implements UserSession {
        private PlayerProfile currentPlayer;

        FakeSession(PlayerProfile currentPlayer) {
            this.currentPlayer = currentPlayer;
        }

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

    private static final class RecordingLogoutPresenter implements LogoutOutputBoundary {
        private boolean successCalled;
        private Optional<AuthenticationFailure> failure = Optional.empty();

        @Override
        public void prepareSuccessView() {
            successCalled = true;
        }

        @Override
        public void prepareFailView(AuthenticationFailure authenticationFailure) {
            failure = Optional.of(authenticationFailure);
        }
    }
}
