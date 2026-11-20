package ca.utoronto.csc207.geochallenge.use_cases.auth;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationResultTest {
    @Test
    void successContainsOnlyTheAuthenticatedProfile() {
        PlayerProfile profile = new PlayerProfile(UUID.randomUUID(), "explorer", "Explorer");

        AuthenticationResult result = AuthenticationResult.success(profile);

        assertTrue(result.isSuccess());
        assertEquals(profile, result.getPlayerProfile().orElseThrow());
        assertFalse(result.getFailure().isPresent());
    }

    @Test
    void failureContainsOnlyASafeFailureType() {
        AuthenticationResult result = AuthenticationResult.failure(AuthenticationFailure.INVALID_CREDENTIALS);

        assertFalse(result.isSuccess());
        assertFalse(result.getPlayerProfile().isPresent());
        assertEquals(AuthenticationFailure.INVALID_CREDENTIALS, result.getFailure().orElseThrow());
    }
}
