package ca.utoronto.csc207.geochallenge.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A completed 5-round daily challenge result for a single player on a specific date.
 *
 * <p>Enforces core domain invariants: exactly 5 rounds, matching total score sum,
 * and valid score bounds.</p>
 */
public final class DailyResult {
    private final UUID id;
    private final UUID userId;
    private final LocalDate challengeDate;
    private final List<RoundResult> roundResults;
    private final int totalScore;
    private final Instant completedAt;

    public DailyResult(UUID id, UUID userId, LocalDate challengeDate,
                       List<RoundResult> roundResults, int totalScore, Instant completedAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.userId = Objects.requireNonNull(userId, "User ID is required.");
        this.challengeDate = Objects.requireNonNull(challengeDate, "Challenge date is required.");
        this.completedAt = Objects.requireNonNull(completedAt, "Completion timestamp is required.");

        if (roundResults == null || roundResults.size() != 5) {
            throw new IllegalArgumentException("Daily challenge must contain exactly 5 round results.");
        }

        int calculatedTotal = 0;
        for (int i = 0; i < 5; i++) {
            RoundResult r = roundResults.get(i);
            if (r == null) {
                throw new IllegalArgumentException("Round results cannot contain null entries.");
            }
            if (r.getRoundNumber() != i + 1) {
                throw new IllegalArgumentException("Rounds must be ordered 1 through 5.");
            }
            calculatedTotal += r.getScore();
        }

        if (totalScore < 0 || totalScore > 25000) {
            throw new IllegalArgumentException("Total score must be between 0 and 25000.");
        }
        if (totalScore != calculatedTotal) {
            throw new IllegalArgumentException("Total score (" + totalScore +
                    ") does not equal sum of round scores (" + calculatedTotal + ").");
        }

        this.totalScore = totalScore;
        this.roundResults = Collections.unmodifiableList(new ArrayList<>(roundResults));
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDate getChallengeDate() {
        return challengeDate;
    }

    public List<RoundResult> getRoundResults() {
        return roundResults;
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
        if (!(o instanceof DailyResult)) return false;
        DailyResult that = (DailyResult) o;
        return totalScore == that.totalScore &&
                id.equals(that.id) &&
                userId.equals(that.userId) &&
                challengeDate.equals(that.challengeDate) &&
                roundResults.equals(that.roundResults) &&
                completedAt.equals(that.completedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, challengeDate, roundResults, totalScore, completedAt);
    }
}
