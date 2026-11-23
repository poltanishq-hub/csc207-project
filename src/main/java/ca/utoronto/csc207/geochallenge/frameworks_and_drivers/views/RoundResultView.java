package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.views;
import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.RoundViewModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The round-reveal screen: shown after the player submits a guess.
 *
 * It shows the actual location, the distance, the points, and a button to
 * continue. It only displays what is in the RoundViewModel.
 */
public class RoundResultView extends JPanel implements PropertyChangeListener {

    private final RoundViewModel viewModel;

    private final JLabel roundLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel targetLabel = new JLabel("");
    private final JLabel guessLabel = new JLabel("");
    private final JLabel distanceLabel = new JLabel("");
    private final JLabel pointsLabel = new JLabel("");
    private final JButton nextButton = new JButton("Next round");

    /**
     * @param viewModel   the data to display
     * @param onNextRound what to do when the button is clicked (the app wiring
     *                    decides: go to the next round, or to the final results)
     */
    public RoundResultView(RoundViewModel viewModel, Runnable onNextRound) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);  // "tell me when the data changes"

        // ---- Top: "Round 2 of 5" ----
        roundLabel.setFont(roundLabel.getFont().deriveFont(Font.BOLD, 22f));

        // ---- Middle: space for the map, then the details ----
        // TODO: the map showing the guess marker and target marker goes here.
        // Agree with P2 on one shared map panel and add it in the middle.
        JPanel mapPlaceholder = new JPanel(new BorderLayout());
        mapPlaceholder.setBorder(BorderFactory.createTitledBorder("Map"));
        mapPlaceholder.add(new JLabel("Guess and target markers will be shown here",
                SwingConstants.CENTER), BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 1));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        detailsPanel.add(targetLabel);
        detailsPanel.add(guessLabel);
        detailsPanel.add(distanceLabel);
        detailsPanel.add(pointsLabel);

        // ---- Bottom: the continue button ----
        nextButton.addActionListener(e -> onNextRound.run());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(nextButton);

        // ---- Put the parts together ----
        setLayout(new BorderLayout());
        add(roundLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(mapPlaceholder, BorderLayout.CENTER);
        centerPanel.add(detailsPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /** Called automatically when the view model fires a change. */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (RoundViewModel.ROUND_CHANGED.equals(evt.getPropertyName())) {
            // Swing components must only be changed on the Event Dispatch Thread.
            SwingUtilities.invokeLater(this::refresh);
        }
    }

    /** Copies everything from the view model onto the screen. */
    private void refresh() {
        roundLabel.setText(viewModel.getRoundLabel());
        targetLabel.setText("Actual location: " + viewModel.getTargetName());
        guessLabel.setText("Your guess: " + viewModel.getGuess());
        distanceLabel.setText("Distance: " + viewModel.getDistance());
        pointsLabel.setText("Points: " + viewModel.getPoints());
        nextButton.setText(viewModel.getNextButtonText());
    }
}
