package ca.utoronto.csc207.geochallenge.use_cases.challenge;

import ca.utoronto.csc207.geochallenge.entities.DailyResult;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for retrieving and saving daily challenge results.
 *
 * <p>Declared in the use cases layer according to the Dependency Inversion Principle.</p>
 */
public interface ResultRepository {

    /**
     * Retrieves the daily result for a given player and challenge date, if it exists.
     *
     * @param userId the player's unique user ID
     * @param date   the challenge date
     * @return an Optional containing the DailyResult, or empty if not yet completed
     */
    Optional<DailyResult> resultFor(UUID userId, LocalDate date);

    /**
     * Persists a completed daily result.
     *
     * @param result the completed DailyResult (containing 5 rounds and total score)
     * @throws IllegalStateException if a result already exists for this player and date
     */
    void saveCompletedResult(DailyResult result);
}
