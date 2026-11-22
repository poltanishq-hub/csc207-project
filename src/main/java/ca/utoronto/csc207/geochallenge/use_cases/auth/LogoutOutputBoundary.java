package ca.utoronto.csc207.geochallenge.use_cases.auth;

/** Presenter boundary for logout outcomes. */
public interface LogoutOutputBoundary {
    void prepareSuccessView();

    void prepareFailView(AuthenticationFailure failure);
}
