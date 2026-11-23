package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access;

import ca.utoronto.csc207.geochallenge.entities.Friendship;
import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import ca.utoronto.csc207.geochallenge.use_cases.friends.FriendshipRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory fake for FriendshipRepository.
 */
public final class InMemoryFriendshipRepository implements FriendshipRepository {
    private final List<Friendship> friendships = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, PlayerProfile> profilesByUsername = new ConcurrentHashMap<>();

    public void registerProfile(PlayerProfile profile) {
        Objects.requireNonNull(profile, "Profile is required.");
        profilesByUsername.put(profile.getUsername().toLowerCase(), profile);
    }

    @Override
    public List<Friendship> friendsOf(UUID userId) {
        Objects.requireNonNull(userId, "User ID is required.");
        synchronized (friendships) {
            return friendships.stream()
                    .filter(f -> f.involves(userId) && f.getStatus() == Friendship.Status.ACCEPTED)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void addFriend(UUID userId, String friendUsername) {
        Objects.requireNonNull(userId, "User ID is required.");
        Objects.requireNonNull(friendUsername, "Friend username is required.");

        PlayerProfile friend = profilesByUsername.get(friendUsername.trim().toLowerCase());
        if (friend == null) {
            throw new NoSuchElementException("No user found with username: " + friendUsername);
        }
        if (friend.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Cannot add yourself as a friend.");
        }

        synchronized (friendships) {
            boolean exists = friendships.stream()
                    .anyMatch(f -> f.involves(userId) && f.involves(friend.getUserId()));
            if (exists) {
                return;
            }
            friendships.add(new Friendship(userId, friend.getUserId(), Friendship.Status.ACCEPTED, Instant.now()));
        }
    }

    @Override
    public void removeFriend(UUID userId, UUID friendId) {
        Objects.requireNonNull(userId, "User ID is required.");
        Objects.requireNonNull(friendId, "Friend ID is required.");

        synchronized (friendships) {
            friendships.removeIf(f -> f.involves(userId) && f.involves(friendId));
        }
    }
}
