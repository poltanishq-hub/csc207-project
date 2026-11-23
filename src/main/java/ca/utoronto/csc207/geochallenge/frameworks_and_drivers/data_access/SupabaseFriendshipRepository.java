package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access;

import ca.utoronto.csc207.geochallenge.entities.Friendship;
import ca.utoronto.csc207.geochallenge.use_cases.friends.FriendshipRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Concrete implementation of FriendshipRepository communicating with Supabase PostgREST API.
 */
public class SupabaseFriendshipRepository implements FriendshipRepository {
    private final URI baseUri;
    private final String publishableKey;
    private final HttpClient httpClient;

    public SupabaseFriendshipRepository(URI baseUri, String publishableKey, HttpClient httpClient) {
        this.baseUri = Objects.requireNonNull(baseUri, "Base URI is required.");
        this.publishableKey = Objects.requireNonNull(publishableKey, "Publishable key is required.");
        this.httpClient = Objects.requireNonNull(httpClient, "HTTP client is required.");
    }

    @Override
    public List<Friendship> friendsOf(UUID userId) {
        // Concrete PostgREST query will be implemented in Phase 5
        throw new UnsupportedOperationException("SupabaseFriendshipRepository.friendsOf to be completed in Phase 5.");
    }

    @Override
    public void addFriend(UUID userId, String friendUsername) {
        // Concrete PostgREST insert will be implemented in Phase 5
        throw new UnsupportedOperationException("SupabaseFriendshipRepository.addFriend to be completed in Phase 5.");
    }

    @Override
    public void removeFriend(UUID userId, UUID friendId) {
        // Concrete PostgREST delete will be implemented in Phase 5
        throw new UnsupportedOperationException("SupabaseFriendshipRepository.removeFriend to be completed in Phase 5.");
    }
}
