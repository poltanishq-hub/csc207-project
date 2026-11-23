package ca.utoronto.csc207.geochallenge.entities;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DailyResultTest {

    private List<RoundResult> createFiveRounds(int scorePerRound) {
        List<RoundResult> rounds = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            rounds.add(new RoundResult(i, "loc" + i, new GeoPoint(40.0 + i, -70.0 + i), 10.0, scorePerRound));
        }
        return rounds;
    }

    @Test
    void createsValidDailyResult() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 11, 22);
        List<RoundResult> rounds = createFiveRounds(4000);
        Instant completedAt = Instant.now();

        DailyResult dailyResult = new DailyResult(null, userId, date, rounds, 20000, completedAt);

        assertNotNull(dailyResult.getId());
        assertEquals(userId, dailyResult.getUserId());
        assertEquals(date, dailyResult.getChallengeDate());
        assertEquals(5, dailyResult.getRoundResults().size());
        assertEquals(20000, dailyResult.getTotalScore());
        assertEquals(completedAt, dailyResult.getCompletedAt());
    }

    @Test
    void rejectsMismatchedTotalScore() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 11, 22);
        List<RoundResult> rounds = createFiveRounds(4000); // sum is 20000

        assertThrows(IllegalArgumentException.class, () ->
                new DailyResult(null, userId, date, rounds, 19999, Instant.now()));
    }

    @Test
    void rejectsIncorrectNumberOfRounds() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 11, 22);
        List<RoundResult> fourRounds = createFiveRounds(4000).subList(0, 4);

        assertThrows(IllegalArgumentException.class, () ->
                new DailyResult(null, userId, date, fourRounds, 16000, Instant.now()));
    }

    @Test
    void rejectsOutOfOrderRounds() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 11, 22);
        List<RoundResult> rounds = createFiveRounds(4000);
        // Swap round 1 and 2
        RoundResult first = rounds.get(0);
        rounds.set(0, rounds.get(1));
        rounds.set(1, first);

        assertThrows(IllegalArgumentException.class, () ->
                new DailyResult(null, userId, date, rounds, 20000, Instant.now()));
    }
}
