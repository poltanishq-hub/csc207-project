package ca.utoronto.csc207.geochallenge.entities;

import java.util.Objects;

/**
 * The outcome of a single round within a daily challenge.
 *
 * <p>Immutable and independent of UI and persistence frameworks.</p>
 */
public final class RoundResult {
    private final int roundNumber;
    private final String locationId;
    private final GeoPoint guess;
    private final double distanceKm;
    private final int score;

    public RoundResult(int roundNumber, String locationId, GeoPoint guess, double distanceKm, int score) {
        if (roundNumber < 1 || roundNumber > 5) {
            throw new IllegalArgumentException("Round number must be between 1 and 5.");
        }
        if (locationId == null || locationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Location ID is required.");
        }
        if (guess == null) {
            throw new IllegalArgumentException("Guess location is required.");
        }
        if (!Double.isFinite(distanceKm) || distanceKm < 0.0) {
            throw new IllegalArgumentException("Distance must be a finite non-negative number.");
        }
        if (score < 0 || score > 5000) {
            throw new IllegalArgumentException("Score must be between 0 and 5000.");
        }
        this.roundNumber = roundNumber;
        this.locationId = locationId.trim();
        this.guess = guess;
        this.distanceKm = distanceKm;
        this.score = score;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getLocationId() {
        return locationId;
    }

    public GeoPoint getGuess() {
        return guess;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoundResult)) return false;
        RoundResult that = (RoundResult) o;
        return roundNumber == that.roundNumber &&
                Double.compare(that.distanceKm, distanceKm) == 0 &&
                score == that.score &&
                locationId.equals(that.locationId) &&
                Double.compare(guess.getLatitude(), that.guess.getLatitude()) == 0 &&
                Double.compare(guess.getLongitude(), that.guess.getLongitude()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundNumber, locationId, guess.getLatitude(), guess.getLongitude(), distanceKm, score);
    }
}
