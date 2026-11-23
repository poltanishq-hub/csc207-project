package ca.utoronto.csc207.geochallenge.interface_adapters.controllers;
import ca.utoronto.csc207.geochallenge.use_cases.challenge.GetDailyResultInputData;
import ca.utoronto.csc207.geochallenge.use_cases.challenge.GetDailyResultInteractor;
import java.time.LocalDate;
import java.util.UUID;

/**
 * The controller for the challenge result screens.
 * View calls the methods here when the user clicks something. The controller packages the
 * information into input data and passes it to the Interactor.
 */

public class ChallengeController {
    private final GetDailyResultInteractor getDailyResultInteractor;

    public ChallengeController(GetDailyResultInteractor getDailyResultInteractor) {
        this.getDailyResultInteractor = getDailyResultInteractor;
    }

    /** Loads a player's completed result for a given day.
     * Called when the user clicks "View results".
     * @param userId the logged-in player's ID
     * @param date the challenge date to look up
     */

    public void viewResults(UUID userId, LocalDate date) {
        GetDailyResultInputData inputData = new GetDailyResultInputData(userId, date);
        getDailyResultInteractor.execute(inputData);
    }
}
