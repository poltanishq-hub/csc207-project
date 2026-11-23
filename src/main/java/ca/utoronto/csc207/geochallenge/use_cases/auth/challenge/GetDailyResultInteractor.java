package ca.utoronto.csc207.geochallenge.use_cases.auth.challenge;
import ca.utoronto.csc207.geochallenge.entities.DailyResult;
import ca.utoronto.csc207.geochallenge.use_cases.challenge.ResultRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * The interactor for the "get daily result" use case.
 * It asks the repository for a player's completed result on a given date,
 * then tells the output boundary (the presenter) what happened.
 */
public class GetDailyResultInteractor {
    private final ResultRepository resultRepository;
    private final GetDailyResultOutputBoundary outputBoundary;

    public GetDailyResultInteractor(ResultRepository resultRepository, GetDailyResultOutputBoundary
            outputBoundary) {
        this.resultRepository = resultRepository;
        this.outputBoundary = outputBoundary;
    }

    /**
     * Runs the use case. The controller calls this method.
     * @param inputData the user ID and the date to look up
     */
    public void execute(GetDailyResultInputData inputData) {
        try {// The repository gives back an Optional. orElse(null) opens it and gives
            // presenter the DailyResult if there is one or null if there isn't.

            // extracting user id and date from input data
            UUID userId = inputData.getUserId();
            LocalDate date = inputData.getDate();

           // getting the Optional from the repository
            Optional<DailyResult> potentialResults = resultRepository.resultFor(userId, date);

           // opening potentialResults and checking if it's empty. If yes, null is used
            DailyResult result = potentialResults.orElse(null);

            if (result != null) { // either passing data to the Presenter or error message
                outputBoundary.prepareSuccessView(new GetDailyResultOutputData(result));
            }
            else {
                outputBoundary.prepareFailView("No completed result found for this date.");
            }
        }

        catch (RuntimeException e) {
            // something went wrong while loading results
            outputBoundary.prepareFailView("Could not load your results. Please try again.");
        }
    }
}