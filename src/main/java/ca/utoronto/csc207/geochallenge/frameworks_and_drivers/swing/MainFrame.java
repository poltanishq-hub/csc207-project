package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.swing;

import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.AppNavigator;
import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.AppScreen;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;

/** The single application window that switches focused panels using a {@link CardLayout}. */
public final class MainFrame extends JFrame implements AppNavigator {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final Runnable homeRefresh;

    /**
     * Temporary constructor retained until the composition root is wired in Step 7.
     * The injected constructor below is the production UI entry point.
     */
    public MainFrame() {
        this(setupPanel(), setupPanel(), setupPanel(), () -> { });
    }

    /** Creates a navigation-capable frame from the focused account-access panels. */
    public MainFrame(LoginView loginView, RegisterView registerView, HomeView homeView) {
        this(loginView, registerView, homeView, homeView::refresh);
    }

    private MainFrame(JPanel loginView, JPanel registerView, JPanel homeView, Runnable homeRefresh) {
        super("GeoGuessr Daily Challenge");
        this.homeRefresh = homeRefresh;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);

        cards.add(loginView, AppScreen.LOGIN.name());
        cards.add(registerView, AppScreen.REGISTER.name());
        cards.add(homeView, AppScreen.HOME.name());
        setContentPane(cards);
        show(AppScreen.LOGIN);
    }

    @Override
    public void show(AppScreen screen) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> show(screen));
            return;
        }
        if (screen == AppScreen.HOME) {
            homeRefresh.run();
        }
        cardLayout.show(cards, screen.name());
    }

    private static JPanel setupPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel heading = new JLabel("GeoGuessr Daily Challenge", SwingConstants.CENTER);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 26.0f));
        JLabel status = new JLabel(
                "Authentication screens are ready to be wired by the application composition root.",
                SwingConstants.CENTER);

        panel.add(heading, BorderLayout.CENTER);
        panel.add(status, BorderLayout.SOUTH);
        return panel;
    }
}
