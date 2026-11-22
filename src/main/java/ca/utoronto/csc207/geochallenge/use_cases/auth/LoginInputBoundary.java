package ca.utoronto.csc207.geochallenge.use_cases.auth;

/** Application boundary for account login. */
public interface LoginInputBoundary {
    void execute(LoginInputData inputData);
}
