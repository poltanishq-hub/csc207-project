package ca.utoronto.csc207.geochallenge.interface_adapters.presenters;
import ca.utoronto.csc207.geochallenge.entities.DailyResult;
import ca.utoronto.csc207.geochallenge.entities.RoundResult;
import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.ResultViewModel;
import ca.utoronto.csc207.geochallenge.use_cases.challenge.GetDailyResultOutputBoundary;
import ca.utoronto.csc207.geochallenge.use_cases.challenge.GetDailyResultOutputData;
import java.util.ArrayList;
import java.util.List;

/** Presenter for the challenge result screens. Interactor calls the methods below when
 *  it is finished. They turn the entities (numbers, dates) into display text and stores it in the
 * ResultViewModel, then tells the view to refresh.
 * Basically, does the output formatting.
 */

public class ChallengePresenter implements GetDailyResultOutputBoundary {
    private final ResultViewModel resultViewModel;

    public ChallengePresenter(ResultViewModel resultViewModel) {
        this.resultViewModel = resultViewModel;
    }

    /** Called once the player's result is found. */
    @Override
    public void prepareSuccessView(GetDailyResultOutputData outputData) {
        DailyResult result = outputData.getDailyResult();

        // Builds a display row for each of the five rounds.
        List<ResultViewModel.RoundRow> rows = new ArrayList<>();
        for (RoundResult round : result.getRoundResults()) {
            rows.add(makeRow(round));
        }

        // Fill in View Model with text to be displayed.
        resultViewModel.setTotalScore(String.format("%,d", result.getTotalScore()));
        resultViewModel.setMaxScore("25,000");
        resultViewModel.setDate(result.getChallengeDate().toString());
        resultViewModel.setRows(rows);
        resultViewModel.setSaveFailed(false);
        resultViewModel.setMessage("");
        // Tell View the data has changed.
        resultViewModel.fireResultChanged();
    }

    /** Called when there is no result/loading error */
    @Override
    public void prepareFailView(String errorMessage) {
        resultViewModel.setTotalScore("");
        resultViewModel.setDate("");
        resultViewModel.setRows(new ArrayList<>());
        resultViewModel.setMessage(errorMessage);
        resultViewModel.fireResultChanged();
    }

    /** Turns one RoundResult into one row of display text. */
    private ResultViewModel.RoundRow makeRow(RoundResult round) {
        int roundNumber = round.getRoundNumber();

        // TODO: the target's name lives on Location, not RoundResult.
        // For now we show the location ID. Replace this once the team decides
        // how to look up a name from the ID.
        String targetName = String.valueOf(round.getLocationId());
        String guess = String.format("%.2f, %.2f", round.getGuess().getLatitude(), round.getGuess().getLongitude());
        String distance = String.format("%,.0f kilo meters", round.getDistanceKm());
        String points = String.format("%,d", round.getScore());

        return new ResultViewModel.RoundRow(roundNumber, targetName, guess, distance, points);
    }
}

