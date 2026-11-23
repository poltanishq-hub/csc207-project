package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access;

import ca.utoronto.csc207.geochallenge.entities.DailyResult;
import ca.utoronto.csc207.geochallenge.entities.Friendship;
import ca.utoronto.csc207.geochallenge.entities.LeaderboardEntry;
import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import ca.utoronto.csc207.geochallenge.use_cases.friends.FriendshipRepository;
import ca.utoronto.csc207.geochallenge.use_cases.leaderboard.LeaderboardRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory fake for LeaderboardRepository.
 *
 * <p>Implements deterministic ranking:
 * 1. Score descending
 * 2. Completion time ascending
 * 3. Username alphabetically</p>
 */
public final class InMemoryLeaderboardRepository implements LeaderboardRepository {
    private final Map<UUID, PlayerProfile> profiles = new ConcurrentHashMap<>();
    private final List<DailyResult> results = Collections.synchronizedList(new ArrayList<>());
    private final FriendshipRepository friendshipRepository;

    public InMemoryLeaderboardRepository(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public void registerProfile(PlayerProfile profile) {
        Objects.requireNonNull(profile, "Profile is required.");
        profiles.put(profile.getUserId(), profile);
    }

    public void addResult(DailyResult result) {
        Objects.requireNonNull(result, "Result is required.");
        results.add(result);
    }

    @Override
    public List<LeaderboardEntry> globalLeaderboard(LocalDate date) {
        Objects.requireNonNull(date, "Date is required.");
        List<DailyResult> dailyResults;
        synchronized (results) {
            dailyResults = results.stream()
                    .filter(r -> r.getChallengeDate().equals(date))
                    .collect(Collectors.toList());
        }
        return rankResults(dailyResults);
    }

    @Override
    public List<LeaderboardEntry> friendsLeaderboard(UUID userId, LocalDate date) {
        Objects.requireNonNull(userId, "User ID is required.");
        Objects.requireNonNull(date, "Date is required.");

        Set<UUID> allowedUserIds = ConcurrentHashMap.newKeySet();
        allowedUserIds.add(userId);

        if (friendshipRepository != null) {
            List<Friendship> friendships = friendshipRepository.friendsOf(userId);
            for (Friendship f : friendships) {
                if (f.getStatus() == Friendship.Status.ACCEPTED) {
                    if (f.getRequesterId().equals(userId)) {
                        allowedUserIds.add(f.getAddresseeId());
                    } else {
                        allowedUserIds.add(f.getRequesterId());
                    }
                }
            }
        }

        List<DailyResult> filteredResults;
        synchronized (results) {
            filteredResults = results.stream()
                    .filter(r -> r.getChallengeDate().equals(date) && allowedUserIds.contains(r.getUserId()))
                    .collect(Collectors.toList());
        }

        return rankResults(filteredResults);
    }

    private List<LeaderboardEntry> rankResults(List<DailyResult> dailyResults) {
        Comparator<DailyResult> comparator = Comparator
                .comparingInt(DailyResult::getTotalScore).reversed()
                .thenComparing(DailyResult::getCompletedAt)
                .thenComparing(r -> {
                    PlayerProfile p = profiles.get(r.getUserId());
                    return p != null ? p.getUsername() : r.getUserId().toString();
                });

        dailyResults.sort(comparator);

        List<LeaderboardEntry> entries = new ArrayList<>(dailyResults.size());
        for (int i = 0; i < dailyResults.size(); i++) {
            DailyResult r = dailyResults.get(i);
            PlayerProfile p = profiles.get(r.getUserId());
            String username = p != null ? p.getUsername() : "player_" + r.getUserId().toString().substring(0, 8);
            String displayName = p != null ? p.getDisplayName() : username;

            entries.add(new LeaderboardEntry(
                    i + 1,
                    r.getUserId(),
                    username,
                    displayName,
                    r.getTotalScore(),
                    r.getCompletedAt()
            ));
        }

        return Collections.unmodifiableList(entries);
    }
}
