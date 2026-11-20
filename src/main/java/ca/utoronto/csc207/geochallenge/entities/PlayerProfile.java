package ca.utoronto.csc207.geochallenge.entities;

import java.util.Objects;
import java.util.UUID;

/**
 * Public, provider-independent information about a player.
 *
 * <p>Authentication credentials and access tokens do not belong in this entity.
 * The user ID is the stable identifier shared by results, friendships, and leaderboard
 * features.</p>
 */
public final class PlayerProfile {
    private final UUID userId;
    private final String username;
    private final String displayName;

    public PlayerProfile(UUID userId, String username, String displayName) {
        this.userId = Objects.requireNonNull(userId, "User ID is required.");
        this.username = requireNonBlank(username, "Username is required.");
        this.displayName = requireNonBlank(displayName, "Display name is required.");
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }
}
