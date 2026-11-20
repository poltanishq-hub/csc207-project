package ca.utoronto.csc207.geochallenge.use_cases.auth;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;

import java.util.Objects;
import java.util.Optional;

/** Result returned by an authentication provider without provider-specific response details. */
public final class AuthenticationResult {
    private final PlayerProfile playerProfile;
    private final AuthenticationFailure failure;

    private AuthenticationResult(PlayerProfile playerProfile, AuthenticationFailure failure) {
        this.playerProfile = playerProfile;
        this.failure = failure;
    }

    public static AuthenticationResult success(PlayerProfile playerProfile) {
        return new AuthenticationResult(Objects.requireNonNull(playerProfile, "Player profile is required."), null);
    }

    public static AuthenticationResult failure(AuthenticationFailure failure) {
        return new AuthenticationResult(null, Objects.requireNonNull(failure, "Failure is required."));
    }

    public boolean isSuccess() {
        return playerProfile != null;
    }

    public Optional<PlayerProfile> getPlayerProfile() {
        return Optional.ofNullable(playerProfile);
    }

    public Optional<AuthenticationFailure> getFailure() {
        return Optional.ofNullable(failure);
    }
}
