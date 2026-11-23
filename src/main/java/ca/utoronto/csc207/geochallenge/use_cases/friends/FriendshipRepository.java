package ca.utoronto.csc207.geochallenge.use_cases.friends;

import ca.utoronto.csc207.geochallenge.entities.Friendship;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing player friendships.
 *
 * <p>Declared in the use cases layer.</p>
 */
public interface FriendshipRepository {

    /**
     * Lists all accepted friendships involving the specified player.
     *
     * @param userId the player's user ID
     * @return the list of active friendships
     */
    List<Friendship> friendsOf(UUID userId);

    /**
     * Creates a friend connection by looking up the friend by their unique username.
     *
     * @param userId         the player initiating the addition
     * @param friendUsername the exact username of the friend to add
     */
    void addFriend(UUID userId, String friendUsername);

    /**
     * Removes an existing friend connection.
     *
     * @param userId   the current player
     * @param friendId the user ID of the friend to remove
     */
    void removeFriend(UUID userId, UUID friendId);
}
