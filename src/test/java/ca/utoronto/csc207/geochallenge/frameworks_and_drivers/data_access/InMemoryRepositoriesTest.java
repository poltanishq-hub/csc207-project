package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access;

import ca.utoronto.csc207.geochallenge.entities.DailyResult;
import ca.utoronto.csc207.geochallenge.entities.GeoPoint;
import ca.utoronto.csc207.geochallenge.entities.LeaderboardEntry;
import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import ca.utoronto.csc207.geochallenge.entities.RoundResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryRepositoriesTest {

    private InMemoryResultRepository resultRepository;
    private InMemoryFriendshipRepository friendshipRepository;
    private InMemoryLeaderboardRepository leaderboardRepository;

    @BeforeEach
    void setUp() {
        resultRepository = new InMemoryResultRepository();
        friendshipRepository = new InMemoryFriendshipRepository();
        leaderboardRepository = new InMemoryLeaderboardRepository(friendshipRepository);
    }

    private List<RoundResult> createFiveRounds(int scorePerRound) {
        List<RoundResult> rounds = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            rounds.add(new RoundResult(i, "loc" + i, new GeoPoint(40.0 + i, -70.0 + i), 10.0, scorePerRound));
        }
        return rounds;
    }

    @Test
    void savesAndRetrievesDailyResult() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 11, 22);
        DailyResult result = new DailyResult(null, userId, date, createFiveRounds(4000), 20000, Instant.now());

        assertFalse(resultRepository.resultFor(userId, date).isPresent());

        resultRepository.saveCompletedResult(result);

        Optional<DailyResult> retrieved = resultRepository.resultFor(userId, date);
        assertTrue(retrieved.isPresent());
        assertEquals(20000, retrieved.get().getTotalScore());
    }

    @Test
    void rejectsDuplicateResultForSameUserAndDate() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 11, 22);
        DailyResult result1 = new DailyResult(null, userId, date, createFiveRounds(4000), 20000, Instant.now());
        DailyResult result2 = new DailyResult(null, userId, date, createFiveRounds(4500), 22500, Instant.now());

        resultRepository.saveCompletedResult(result1);

        assertThrows(IllegalStateException.class, () -> resultRepository.saveCompletedResult(result2));
    }

    @Test
    void leaderboardOrdersByScoreDescThenTimeAsc() {
        LocalDate date = LocalDate.of(2026, 11, 22);

        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        UUID user3 = UUID.randomUUID();

        leaderboardRepository.registerProfile(new PlayerProfile(user1, "alice", "Alice"));
        leaderboardRepository.registerProfile(new PlayerProfile(user2, "bob", "Bob"));
        leaderboardRepository.registerProfile(new PlayerProfile(user3, "charlie", "Charlie"));

        Instant now = Instant.now();
        // user1 has 20000 score
        DailyResult res1 = new DailyResult(null, user1, date, createFiveRounds(4000), 20000, now.plusSeconds(10));
        // user2 has 25000 score
        DailyResult res2 = new DailyResult(null, user2, date, createFiveRounds(5000), 25000, now.plusSeconds(20));
        // user3 has 20000 score but finished earlier than user1
        DailyResult res3 = new DailyResult(null, user3, date, createFiveRounds(4000), 20000, now);

        leaderboardRepository.addResult(res1);
        leaderboardRepository.addResult(res2);
        leaderboardRepository.addResult(res3);

        List<LeaderboardEntry> leaderboard = leaderboardRepository.globalLeaderboard(date);

        assertEquals(3, leaderboard.size());
        // Rank 1: Bob (25000)
        assertEquals("bob", leaderboard.get(0).getUsername());
        assertEquals(1, leaderboard.get(0).getRank());
        // Rank 2: Charlie (20000, earlier time)
        assertEquals("charlie", leaderboard.get(1).getUsername());
        assertEquals(2, leaderboard.get(1).getRank());
        // Rank 3: Alice (20000, later time)
        assertEquals("alice", leaderboard.get(2).getUsername());
        assertEquals(3, leaderboard.get(2).getRank());
    }

    @Test
    void friendsLeaderboardFiltersToFriendsOnly() {
        LocalDate date = LocalDate.of(2026, 11, 22);

        UUID userMe = UUID.randomUUID();
        UUID userFriend = UUID.randomUUID();
        UUID userStranger = UUID.randomUUID();

        PlayerProfile profileMe = new PlayerProfile(userMe, "me", "Me");
        PlayerProfile profileFriend = new PlayerProfile(userFriend, "friend", "Friend");
        PlayerProfile profileStranger = new PlayerProfile(userStranger, "stranger", "Stranger");

        leaderboardRepository.registerProfile(profileMe);
        leaderboardRepository.registerProfile(profileFriend);
        leaderboardRepository.registerProfile(profileStranger);
        friendshipRepository.registerProfile(profileFriend);

        // Add friend
        friendshipRepository.addFriend(userMe, "friend");

        leaderboardRepository.addResult(new DailyResult(null, userMe, date, createFiveRounds(4000), 20000, Instant.now()));
        leaderboardRepository.addResult(new DailyResult(null, userFriend, date, createFiveRounds(4500), 22500, Instant.now()));
        leaderboardRepository.addResult(new DailyResult(null, userStranger, date, createFiveRounds(5000), 25000, Instant.now()));

        List<LeaderboardEntry> friendsBoard = leaderboardRepository.friendsLeaderboard(userMe, date);

        assertEquals(2, friendsBoard.size());
        assertEquals("friend", friendsBoard.get(0).getUsername());
        assertEquals("me", friendsBoard.get(1).getUsername());
    }
}
