package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access;

import ca.utoronto.csc207.geochallenge.entities.DailyResult;
import ca.utoronto.csc207.geochallenge.use_cases.challenge.ResultRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory fake for ResultRepository.
 *
 * <p>Enforces the same invariants as the database: unique (userId, challengeDate).</p>
 */
public final class InMemoryResultRepository implements ResultRepository {
    private static final class ResultKey {
        private final UUID userId;
        private final LocalDate date;

        ResultKey(UUID userId, LocalDate date) {
            this.userId = userId;
            this.date = date;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ResultKey)) return false;
            ResultKey resultKey = (ResultKey) o;
            return userId.equals(resultKey.userId) && date.equals(resultKey.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, date);
        }
    }

    private final Map<ResultKey, DailyResult> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<DailyResult> resultFor(UUID userId, LocalDate date) {
        Objects.requireNonNull(userId, "User ID is required.");
        Objects.requireNonNull(date, "Date is required.");
        return Optional.ofNullable(storage.get(new ResultKey(userId, date)));
    }

    @Override
    public void saveCompletedResult(DailyResult result) {
        Objects.requireNonNull(result, "Result is required.");
        ResultKey key = new ResultKey(result.getUserId(), result.getChallengeDate());
        if (storage.putIfAbsent(key, result) != null) {
            throw new IllegalStateException("A completed result already exists for user " +
                    result.getUserId() + " on " + result.getChallengeDate());
        }
    }

    public void clear() {
        storage.clear();
    }
}
