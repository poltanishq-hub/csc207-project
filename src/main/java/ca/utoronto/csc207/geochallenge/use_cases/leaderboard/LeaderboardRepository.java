package ca.utoronto.csc207.geochallenge.use_cases.leaderboard;

import ca.utoronto.csc207.geochallenge.entities.LeaderboardEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for querying ranked player results on a daily challenge.
 *
 * <p>Declared in the use cases layer.</p>
 */
public interface LeaderboardRepository {

    /**
     * Retrieves the ranked global leaderboard for a given challenge date.
     *
     * <p>Ranked deterministically: highest score first, then earliest completion time,
     * then alphabetical username.</p>
     *
     * @param date the canonical challenge date
     * @return the list of ranked entries
     */
    List<LeaderboardEntry> globalLeaderboard(LocalDate date);

    /**
     * Retrieves the leaderboard for a player and their accepted friends for a given challenge date.
     *
     * @param userId the current player's user ID
     * @param date   the canonical challenge date
     * @return the list of ranked entries among the player and friends
     */
    List<LeaderboardEntry> friendsLeaderboard(UUID userId, LocalDate date);
}
