package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access;

import ca.utoronto.csc207.geochallenge.entities.LeaderboardEntry;
import ca.utoronto.csc207.geochallenge.use_cases.leaderboard.LeaderboardRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Concrete implementation of LeaderboardRepository communicating with Supabase PostgREST API.
 */
public class SupabaseLeaderboardRepository implements LeaderboardRepository {
    private final URI baseUri;
    private final String publishableKey;
    private final HttpClient httpClient;

    public SupabaseLeaderboardRepository(URI baseUri, String publishableKey, HttpClient httpClient) {
        this.baseUri = Objects.requireNonNull(baseUri, "Base URI is required.");
        this.publishableKey = Objects.requireNonNull(publishableKey, "Publishable key is required.");
        this.httpClient = Objects.requireNonNull(httpClient, "HTTP client is required.");
    }

    @Override
    public List<LeaderboardEntry> globalLeaderboard(LocalDate date) {
        // Concrete PostgREST query against geochallenge_leaderboard_view will be implemented in Phase 5
        throw new UnsupportedOperationException("SupabaseLeaderboardRepository.globalLeaderboard to be completed in Phase 5.");
    }

    @Override
    public List<LeaderboardEntry> friendsLeaderboard(UUID userId, LocalDate date) {
        // Concrete PostgREST query filtering by friendships will be implemented in Phase 5
        throw new UnsupportedOperationException("SupabaseLeaderboardRepository.friendsLeaderboard to be completed in Phase 5.");
    }
}
