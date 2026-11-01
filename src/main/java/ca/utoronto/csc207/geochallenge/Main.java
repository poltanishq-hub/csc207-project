package ca.utoronto.csc207.geochallenge;

import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.swing.MainFrame;

import javax.swing.SwingUtilities;

/** Application entry point and composition root. */
public final class Main {
    private Main() {
        // Utility class; do not instantiate.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
