package ca.utoronto.csc207.geochallenge.interface_adapters.view_models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The view model for the round-reveal screen (RoundResultView).
 *
 * It stores the text for ONE round: what the player guessed, where the
 * location really was, the distance, and the points. The presenter fills it
 * in and the view displays it. There is no game logic here.
 */
public class RoundViewModel {
    // Event fired when something happens
    public static final String ROUND_CHANGED = "roundChanged";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    // The data the screen displays (all Strings, already formatted)
    private String roundLabel = "";        // e.g. "Round 2 of 5"
    private String targetName = "";        // e.g. "Eiffel Tower, Paris"
    private String guess = "";             // e.g. "43.65, -79.38"
    private String distance = "";          // e.g. "1,234 km"
    private String points = "";            // e.g. "4,312"
    private String nextButtonText = "Next round";  // or "See final results" on round 5

    // Getters (called by View)
    public String getRoundLabel() { return roundLabel; }
    public String getTargetName() { return targetName; }
    public String getGuess() { return guess; }
    public String getDistance() { return distance; }
    public String getPoints() { return points; }
    public String getNextButtonText() { return nextButtonText; }

    // Setters (called by Presenter)
    public void setRoundLabel(String roundLabel) { this.roundLabel = roundLabel; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    public void setGuess(String guess) { this.guess = guess; }
    public void setDistance(String distance) { this.distance = distance; }
    public void setPoints(String points) { this.points = points; }
    public void setNextButtonText(String nextButtonText) { this.nextButtonText = nextButtonText; }

    // Listener Methods
    /** The view calls this once to say "tell me when the data changes". */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /** The presenter calls this after setting the fields, so the view refreshes. */
    public void fireRoundChanged() {
        support.firePropertyChange(ROUND_CHANGED, null, this);
    }
}
