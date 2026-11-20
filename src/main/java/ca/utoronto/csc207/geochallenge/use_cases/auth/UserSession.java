package ca.utoronto.csc207.geochallenge.use_cases.auth;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;

import java.util.Optional;

/**
 * Application-level authenticated-user state shared by feature use cases.
 *
 * <p>This port deliberately exposes a profile, not provider access tokens. A concrete outer
 * layer may manage any provider session separately.</p>
 */
public interface UserSession {
    /** Returns the current player, or empty when no one is authenticated. */
    Optional<PlayerProfile> currentUser();

    /** Starts a local session after successful registration or login. */
    void start(PlayerProfile playerProfile);

    /** Clears the local authenticated-player state. */
    void clear();
}
