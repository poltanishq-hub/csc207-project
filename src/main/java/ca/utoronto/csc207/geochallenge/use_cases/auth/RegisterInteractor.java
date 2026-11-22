package ca.utoronto.csc207.geochallenge.use_cases.auth;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;

import java.util.Objects;

/** Registers a player through an authentication gateway and starts their local session. */
public final class RegisterInteractor implements RegisterInputBoundary {
    private final AuthGateway authGateway;
    private final UserSession userSession;
    private final RegisterOutputBoundary presenter;

    public RegisterInteractor(AuthGateway authGateway, UserSession userSession,
                              RegisterOutputBoundary presenter) {
        this.authGateway = Objects.requireNonNull(authGateway, "Auth gateway is required.");
        this.userSession = Objects.requireNonNull(userSession, "User session is required.");
        this.presenter = Objects.requireNonNull(presenter, "Presenter is required.");
    }

    @Override
    public void execute(RegisterInputData inputData) {
        if (!AuthenticationInputValidator.isValidRegistration(inputData)) {
            clearPassword(inputData);
            presenter.prepareFailView(AuthenticationFailure.INVALID_INPUT);
            return;
        }

        RegistrationRequest request = new RegistrationRequest(
                inputData.getUsername().trim(), inputData.getDisplayName().trim(),
                inputData.getEmail().trim(), inputData.getPassword());
        try {
            presentResult(authGateway.register(request));
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

    private static void clearPassword(RegisterInputData inputData) {
        if (inputData != null) {
            inputData.clearPassword();
        }
    }
}
