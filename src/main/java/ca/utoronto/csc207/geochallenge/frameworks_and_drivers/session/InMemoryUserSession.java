package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.session;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import ca.utoronto.csc207.geochallenge.use_cases.auth.UserSession;

import java.util.Objects;
import java.util.Optional;

/** Per-process authenticated-player session; no profile, credential, or token is written to disk. */
public final class InMemoryUserSession implements UserSession {
    private PlayerProfile currentPlayer;

    @Override
    public synchronized Optional<PlayerProfile> currentUser() {
        return Optional.ofNullable(currentPlayer);
    }

    @Override
    public synchronized void start(PlayerProfile playerProfile) {
        currentPlayer = Objects.requireNonNull(playerProfile, "Player profile is required.");
    }

    @Override
    public synchronized void clear() {
        currentPlayer = null;
    }
}
