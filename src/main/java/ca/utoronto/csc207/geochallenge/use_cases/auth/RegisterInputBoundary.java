package ca.utoronto.csc207.geochallenge.use_cases.auth;

/** Application boundary for account registration. */
public interface RegisterInputBoundary {
    void execute(RegisterInputData inputData);
}
