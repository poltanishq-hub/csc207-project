package ca.utoronto.csc207.geochallenge.use_cases.auth;

/** Presenter boundary for registration outcomes. */
public interface RegisterOutputBoundary {
    void prepareSuccessView(AuthenticationOutputData outputData);

    void prepareFailView(AuthenticationFailure failure);
}
