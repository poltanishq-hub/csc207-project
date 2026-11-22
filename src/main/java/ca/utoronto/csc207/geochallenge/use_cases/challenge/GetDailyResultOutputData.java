package ca.utoronto.csc207.geochallenge.use_cases.challenge;
import ca.utoronto.csc207.geochallenge.entities.DailyResult;

/**
 * The output data for the "get daily result" use case.
 * Interactor uses (creates) one of this after calculating player's result and
 * hands it to Output Boundary. Presenter then reads the DailyResult
 * and turns it into text for View Model.
 */
public class GetDailyResultOutputData {
    private final DailyResult dailyResult;

    public GetDailyResultOutputData(DailyResult dailyResult) {
        this.dailyResult = dailyResult;
    }

    public DailyResult getDailyResult() {
        return dailyResult;
    }
}
