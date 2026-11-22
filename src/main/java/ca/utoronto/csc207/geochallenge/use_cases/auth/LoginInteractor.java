package ca.utoronto.csc207.geochallenge.use_cases.auth;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;

import java.util.Objects;

/** Logs in a player through an authentication gateway and starts their local session. */
public final class LoginInteractor implements LoginInputBoundary {
    private final AuthGateway authGateway;
    private final UserSession userSession;
    private final LoginOutputBoundary presenter;

    public LoginInteractor(AuthGateway authGateway, UserSession userSession,
                           LoginOutputBoundary presenter) {
        this.authGateway = Objects.requireNonNull(authGateway, "Auth gateway is required.");
        this.userSession = Objects.requireNonNull(userSession, "User session is required.");
        this.presenter = Objects.requireNonNull(presenter, "Presenter is required.");
    }

    @Override
    public void execute(LoginInputData inputData) {
        if (!AuthenticationInputValidator.isValidLogin(inputData)) {
            clearPassword(inputData);
            presenter.prepareFailView(AuthenticationFailure.INVALID_INPUT);
            return;
        }

        LoginRequest request = new LoginRequest(inputData.getEmail().trim(), inputData.getPassword());
        try {
            presentResult(authGateway.login(request));
        }
        catch (RuntimeException exception) {
            presenter.prepareFailView(AuthenticationFailure.SERVICE_UNAVAILABLE);
        }
        finally {
            request.clearPassword();
            inputData.clearPassword();
        }
    }

    private void presentResult(AuthenticationResult result) {
        if (result == null) {
            presenter.prepareFailView(AuthenticationFailure.UNEXPECTED_FAILURE);
        }
        else if (result.isSuccess()) {
            PlayerProfile profile = result.getPlayerProfile().orElseThrow();
            userSession.start(profile);
            presenter.prepareSuccessView(new AuthenticationOutputData(profile));
        }
        else {
            presenter.prepareFailView(result.getFailure().orElse(AuthenticationFailure.UNEXPECTED_FAILURE));
        }
    }

    private static void clearPassword(LoginInputData inputData) {
        if (inputData != null) {
            inputData.clearPassword();
        }
    }
}
