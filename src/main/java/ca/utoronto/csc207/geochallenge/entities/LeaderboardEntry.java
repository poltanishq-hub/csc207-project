package ca.utoronto.csc207.geochallenge.entities;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A single player's ranked standing on a daily leaderboard.
 *
 * <p>Immutable view model/domain entity used for displaying global and friend rankings.</p>
 */
public final class LeaderboardEntry {
    private final int rank;
    private final UUID userId;
    private final String username;
    private final String displayName;
    private final int totalScore;
    private final Instant completedAt;

    public LeaderboardEntry(int rank, UUID userId, String username, String displayName,
                            int totalScore, Instant completedAt) {
        if (rank < 1) {
            throw new IllegalArgumentException("Rank must be positive.");
        }
        this.rank = rank;
        this.userId = Objects.requireNonNull(userId, "User ID is required.");
        this.username = requireNonBlank(username, "Username is required.");
        this.displayName = requireNonBlank(displayName, "Display name is required.");
        if (totalScore < 0 || totalScore > 25000) {
            throw new IllegalArgumentException("Total score must be between 0 and 25000.");
        }
        this.totalScore = totalScore;
        this.completedAt = Objects.requireNonNull(completedAt, "Completion timestamp is required.");
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    public int getRank() {
        return rank;
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

    public int getTotalScore() {
        return totalScore;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaderboardEntry)) return false;
        LeaderboardEntry that = (LeaderboardEntry) o;
        return rank == that.rank &&
                totalScore == that.totalScore &&
                userId.equals(that.userId) &&
                username.equals(that.username) &&
                displayName.equals(that.displayName) &&
                completedAt.equals(that.completedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, userId, username, displayName, totalScore, completedAt);
    }
}
