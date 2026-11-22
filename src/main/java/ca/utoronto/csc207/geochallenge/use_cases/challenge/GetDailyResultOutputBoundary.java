package ca.utoronto.csc207.geochallenge.use_cases.challenge;

/**
 * The output boundary for the "get daily result" use case.
 * Interactor calls these methods when it is finished. The presenter
 * implements this interface, so the interactor never needs to know anything
 * about views or Swing.
 */
public interface GetDailyResultOutputBoundary {
    /** Called once the player's result was calculated.
     * @param outputData is the result to show (contains the DailyResult)
     */
    void prepareSuccessView(GetDailyResultOutputData outputData);
    /** Called when the result couldnt be shown, like when player has no result for that day.
     * @param errorMessage the text to show the user
     */
    void prepareFailView(String errorMessage);
}
