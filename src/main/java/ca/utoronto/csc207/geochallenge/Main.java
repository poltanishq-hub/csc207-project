package ca.utoronto.csc207.geochallenge;

import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.api.JavaHttpTransport;
import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access.SupabaseAuthGateway;
import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access.SupabaseConfiguration;
import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.session.InMemoryUserSession;
import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.swing.HomeView;
import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.swing.LoginView;
import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.swing.MainFrame;
import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.swing.RegisterView;
import ca.utoronto.csc207.geochallenge.interface_adapters.controllers.AuthController;
import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.DelegatingAppNavigator;
import ca.utoronto.csc207.geochallenge.interface_adapters.presenters.AuthPresenter;
import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.LoginViewModel;
import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.RegisterViewModel;
import ca.utoronto.csc207.geochallenge.use_cases.auth.AuthGateway;
import ca.utoronto.csc207.geochallenge.use_cases.auth.LoginInteractor;
import ca.utoronto.csc207.geochallenge.use_cases.auth.LogoutInteractor;
import ca.utoronto.csc207.geochallenge.use_cases.auth.RegisterInteractor;
import ca.utoronto.csc207.geochallenge.use_cases.auth.UserSession;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;

/** Application entry point and composition root. */
public final class Main {
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);

    private Main() {
        // Utility class; do not instantiate.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = createMainFrame();
                mainFrame.setVisible(true);
            }
            catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(
                        null,
                        "The application is not configured. Set SUPABASE_URL and "
                                + "SUPABASE_PUBLISHABLE_KEY, then restart.\n\n"
                                + "See .env.example for the expected variable names.",
                        "Configuration required",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /** Creates and connects every outer implementation needed by the account-access flow. */
    static MainFrame createMainFrame() {
        SupabaseConfiguration configuration = SupabaseConfiguration.fromEnvironment();
        AuthGateway authGateway = new SupabaseAuthGateway(
                configuration,
                new JavaHttpTransport(HttpClient.newBuilder()
                        .connectTimeout(CONNECT_TIMEOUT)
                        .build()));
        UserSession userSession = new InMemoryUserSession();

        LoginViewModel loginViewModel = new LoginViewModel();
        RegisterViewModel registerViewModel = new RegisterViewModel();
        DelegatingAppNavigator navigator = new DelegatingAppNavigator();
        AuthPresenter presenter = new AuthPresenter(loginViewModel, registerViewModel, navigator);

        AuthController controller = new AuthController(
                new RegisterInteractor(authGateway, userSession, presenter.registerOutputBoundary()),
                new LoginInteractor(authGateway, userSession, presenter.loginOutputBoundary()),
                new LogoutInteractor(authGateway, userSession, presenter.logoutOutputBoundary()));

        LoginView loginView = new LoginView(controller, navigator, loginViewModel);
        RegisterView registerView = new RegisterView(controller, navigator, registerViewModel);
        HomeView homeView = new HomeView(controller, userSession, Clock.systemDefaultZone());
        MainFrame mainFrame = new MainFrame(loginView, registerView, homeView);
        navigator.setDelegate(mainFrame);
        return mainFrame;
    }
}
