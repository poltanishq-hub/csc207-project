package ca.utoronto.csc207.geochallenge.use_cases.auth;

/** Presenter boundary for login outcomes. */
public interface LoginOutputBoundary {
    void prepareSuccessView(AuthenticationOutputData outputData);

    void prepareFailView(AuthenticationFailure failure);
}
