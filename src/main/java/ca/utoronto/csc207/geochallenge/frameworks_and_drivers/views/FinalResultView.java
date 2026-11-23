package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.views;

import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.ResultViewModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The final summary screen (total score and five-row breakdown).
 * Displays what is in the ResultViewModel and forwards button clicks.
 */

public class FinalResultView extends JPanel implements PropertyChangeListener {
    private final ResultViewModel viewModel;

    private final JLabel titleLabel = new JLabel("Daily Challenge Results", SwingConstants.CENTER);
    private final JLabel totalLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
    private final DefaultTableModel tableModel;

    /** @param viewModel     the data to display
     * @param onLeaderboard what to do when "Leaderboard" is clicked (the app wiring decides)
     * @param onHome        what to do when "Home" is clicked
     */

    public FinalResultView(ResultViewModel viewModel, Runnable onLeaderboard, Runnable onHome) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);  // informs of data change

        // At the top of the screen (title and total)
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 28f));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(totalLabel, BorderLayout.CENTER);
        topPanel.add(messageLabel, BorderLayout.SOUTH);

        // Middle of the screen (table of final results of all the rounds)
        String[] columns = {"Round", "Target", "Your guess", "Distance", "Points"};
        // Overriding isCellEditable so the player cannot type into the table.
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);

        // Bottom of the screen (buttons for leaderboard, home)
        JButton leaderboardButton = new JButton("Leaderboard");
        JButton homeButton = new JButton("Home");
        leaderboardButton.addActionListener(e -> onLeaderboard.run());
        homeButton.addActionListener(e -> onHome.run());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(homeButton);

        // Combining the layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /** Called  when the View Model fires a change. */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ResultViewModel.RESULT_CHANGED.equals(evt.getPropertyName())) {
            // Swing components must only be changed on the Event Dispatch Thread.
            // invokeLater makes sure refresh() runs there, even if the data
            // was loaded on a background thread.
            SwingUtilities.invokeLater(this::refresh);
        }
    }

    /** Copies everything from View Model onto the screen. */
    private void refresh() {
        titleLabel.setText("Daily Challenge Results  " + viewModel.getDate());
        if (viewModel.getTotalScore().isEmpty()) {
            totalLabel.setText("");
        }
        else {
            totalLabel.setText(viewModel.getTotalScore() + " / " + viewModel.getMaxScore());
        }

        messageLabel.setText(viewModel.getMessage());

        // Clear the old rows, then add a row for each round.
        tableModel.setRowCount(0);
        for (ResultViewModel.RoundRow row : viewModel.getRows()) {
            tableModel.addRow(new Object[] {
                    row.getRoundNumber(),
                    row.getTargetName(),
                    row.getGuess(),
                    row.getDistance(),
                    row.getPoints()
            });
        }
    }
}
