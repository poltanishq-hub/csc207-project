package ca.utoronto.csc207.geochallenge.use_cases.auth;

import java.util.Objects;

/** Ends the local session and requests provider logout where the provider supports it. */
public final class LogoutInteractor implements LogoutInputBoundary {
    private final AuthGateway authGateway;
    private final UserSession userSession;
    private final LogoutOutputBoundary presenter;

    public LogoutInteractor(AuthGateway authGateway, UserSession userSession,
                            LogoutOutputBoundary presenter) {
        this.authGateway = Objects.requireNonNull(authGateway, "Auth gateway is required.");
        this.userSession = Objects.requireNonNull(userSession, "User session is required.");
        this.presenter = Objects.requireNonNull(presenter, "Presenter is required.");
    }

    @Override
    public void execute() {
        try {
            authGateway.logout();
            presenter.prepareSuccessView();
        }
        catch (RuntimeException exception) {
            presenter.prepareFailView(AuthenticationFailure.SERVICE_UNAVAILABLE);
        }
        finally {
            userSession.clear();
        }
    }
}
