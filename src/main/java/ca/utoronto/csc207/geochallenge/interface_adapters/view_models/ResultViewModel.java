package ca.utoronto.csc207.geochallenge.interface_adapters.view_models;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * The view model for the final summary screen - Stores the text itll display.
 * Presenter fills it in w/ setters, View reads from it w/ getters
 */

public class ResultViewModel {
    // Event that is called when something changes. View listens for it
    public static final String RESULT_CHANGED = "resultChanged";

    // PropertyChangeSupport is a built-in Java helper that keeps a list of
    // listeners (our view) and notifies them when we tell it to.
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    //Data displayed on screen
    private String totalScore = "";
    private String maxScore = "25000";
    private String date = "";
    private List<RoundRow> rows = new ArrayList<>();  // one row per round

    //Error info for when saving result fails
    private boolean saveFailed = false;
    private String message = "";           //text user will see

    // Getters (called by the view)
    public String getTotalScore() { return totalScore; }
    public String getMaxScore() { return maxScore; }
    public String getDate() { return date; }
    public List<RoundRow> getRows() { return rows; }
    public boolean isSaveFailed() { return saveFailed; }
    public String getMessage() { return message; }

    //Setters (called by the presenter)
    public void setTotalScore(String totalScore) { this.totalScore = totalScore; }
    public void setMaxScore(String maxScore) { this.maxScore = maxScore; }
    public void setDate(String date) { this.date = date; }
    public void setRows(List<RoundRow> rows) { this.rows = rows; }
    public void setSaveFailed(boolean saveFailed) { this.saveFailed = saveFailed; }
    public void setMessage(String message) { this.message = message; }

    // Listeners

    /** Called by View to check for changes */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /** Called by Presenter to tell View to refresh itself */
    public void fireResultChanged() {
        support.firePropertyChange(RESULT_CHANGED, null, this);
    }

    // Formatting for a row on the breakdown table (like e.g. info for round 1)

    /** Stores all the text info for one round */
    public static class RoundRow {
        private final int roundNumber;
        private final String targetName;    // e.g. "AGO, Toronto"
        private final String guess;
        private final String distance;
        private final String points;

        public RoundRow(int roundNumber, String targetName, String guess,
                        String distance, String points) {
            this.roundNumber = roundNumber;
            this.targetName = targetName;
            this.guess = guess;
            this.distance = distance;
            this.points = points;
        }

        // All the methods called by View after Presenter sets rows with SetRows
        public int getRoundNumber() { return roundNumber; }
        public String getTargetName() { return targetName; }
        public String getGuess() { return guess; }
        public String getDistance() { return distance; }
        public String getPoints() { return points; }
    }
}