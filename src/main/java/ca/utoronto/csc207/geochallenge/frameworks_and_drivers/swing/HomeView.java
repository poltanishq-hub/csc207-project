package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.swing;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import ca.utoronto.csc207.geochallenge.interface_adapters.controllers.AuthController;
import ca.utoronto.csc207.geochallenge.use_cases.auth.UserSession;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

/** Swing home panel for the authenticated player. */
public final class HomeView extends JPanel {
    private static final ZoneId CHALLENGE_ZONE = ZoneId.of("America/Toronto");

    private final AuthController controller;
    private final UserSession userSession;
    private final Clock clock;
    private final JLabel welcomeLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();
    private final JLabel completionLabel = new JLabel("Today's challenge status is not available yet.");
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton playButton = new JButton("Play today's challenge");
    private final JButton leaderboardButton = new JButton("Leaderboards");
    private final JButton friendsButton = new JButton("Friends");
    private final JButton logoutButton = new JButton("Log out");

    public HomeView(AuthController controller, UserSession userSession, Clock clock) {
        super(new BorderLayout(12, 12));
        this.controller = Objects.requireNonNull(controller, "Controller is required.");
        this.userSession = Objects.requireNonNull(userSession, "User session is required.");
        this.clock = Objects.requireNonNull(clock, "Clock is required.");

        buildLayout();
        bindActions();
        refresh();
    }

    private void buildLayout() {
        setBorder(BorderFactory.createEmptyBorder(32, 48, 32, 48));
        JPanel information = new JPanel(new GridLayout(3, 1, 6, 6));
        information.add(welcomeLabel);
        information.add(dateLabel);
        information.add(completionLabel);
        add(information, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(4, 1, 8, 8));
        actions.add(playButton);
        actions.add(leaderboardButton);
        actions.add(friendsButton);
        actions.add(logoutButton);
        add(actions, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void bindActions() {
        playButton.addActionListener(event -> showPendingMessage("Challenge play"));
        leaderboardButton.addActionListener(event -> showPendingMessage("Leaderboards"));
        friendsButton.addActionListener(event -> showPendingMessage("Friends"));
        logoutButton.addActionListener(event -> submitLogout());
    }

    /** Refreshes the account name and Toronto challenge date whenever Home is displayed. */
    public void refresh() {
        Optional<PlayerProfile> profile = userSession.currentUser();
        welcomeLabel.setText(profile.map(value -> "Welcome, " + value.getDisplayName() + "!")
                .orElse("Welcome!"));
        LocalDate challengeDate = LocalDate.now(clock.withZone(CHALLENGE_ZONE));
        dateLabel.setText("Today's challenge: " + challengeDate);
    }

    private void showPendingMessage(String feature) {
        statusLabel.setText(feature + " will be available soon.");
    }

    private void submitLogout() {
        setLogoutBusy(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.logout();
                return null;
            }

            @Override
            protected void done() {
                setLogoutBusy(false);
            }
        }.execute();
    }

    private void setLogoutBusy(boolean busy) {
        logoutButton.setEnabled(!busy);
    }
}
