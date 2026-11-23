package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access;

import ca.utoronto.csc207.geochallenge.entities.DailyResult;
import ca.utoronto.csc207.geochallenge.use_cases.challenge.ResultRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Concrete implementation of ResultRepository communicating with Supabase PostgREST API.
 */
public class SupabaseResultRepository implements ResultRepository {
    private final URI baseUri;
    private final String publishableKey;
    private final HttpClient httpClient;

    public SupabaseResultRepository(URI baseUri, String publishableKey, HttpClient httpClient) {
        this.baseUri = Objects.requireNonNull(baseUri, "Base URI is required.");
        this.publishableKey = Objects.requireNonNull(publishableKey, "Publishable key is required.");
        this.httpClient = Objects.requireNonNull(httpClient, "HTTP client is required.");
    }

    @Override
    public Optional<DailyResult> resultFor(UUID userId, LocalDate date) {
        // Concrete PostgREST query will be implemented in Phase 5
        throw new UnsupportedOperationException("SupabaseResultRepository.resultFor to be completed in Phase 5.");
    }

    @Override
    public void saveCompletedResult(DailyResult result) {
        // Concrete PostgREST insert will be implemented in Phase 5
        throw new UnsupportedOperationException("SupabaseResultRepository.saveCompletedResult to be completed in Phase 5.");
    }
}
