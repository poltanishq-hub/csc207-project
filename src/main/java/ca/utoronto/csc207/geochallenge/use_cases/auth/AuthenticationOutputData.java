package ca.utoronto.csc207.geochallenge.use_cases.auth;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;

import java.util.Objects;

/** Successful account-access output for presenters and navigation adapters. */
public final class AuthenticationOutputData {
    private final PlayerProfile playerProfile;

    public AuthenticationOutputData(PlayerProfile playerProfile) {
        this.playerProfile = Objects.requireNonNull(playerProfile, "Player profile is required.");
    }

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }
}
