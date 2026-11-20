package ca.utoronto.csc207.geochallenge.use_cases.auth;

/**
 * Boundary to the authentication provider and the public-player-profile store.
 *
 * <p>Implementations belong in an outer layer. They must not persist a plaintext password
 * in an application table or file, and must not expose provider error bodies to callers.</p>
 */
public interface AuthGateway {
    /** Creates an authenticated account and its public player profile. */
    AuthenticationResult register(RegistrationRequest request);

    /** Authenticates a returning player and retrieves that player's public profile. */
    AuthenticationResult login(LoginRequest request);

    /** Invalidates the provider session when the provider requires an explicit sign-out. */
    void logout();
}
