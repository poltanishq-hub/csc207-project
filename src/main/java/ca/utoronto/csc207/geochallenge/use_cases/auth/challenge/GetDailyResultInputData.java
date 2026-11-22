package ca.utoronto.csc207.geochallenge.use_cases.auth.challenge;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Input data for the get daily result use case.
 * Controller creates one of this and passes it to the interactor.
 * It has user id and date.
 */
public class GetDailyResultInputData {
    private final UUID userId;
    private final LocalDate date;

    public GetDailyResultInputData(UUID userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDate getDate() {
        return date;
    }
}
