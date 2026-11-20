package ca.utoronto.csc207.geochallenge.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerProfileTest {
    @Test
    void retainsStablePublicIdentity() {
        UUID userId = UUID.randomUUID();

        PlayerProfile profile = new PlayerProfile(userId, "  northstar  ", "  North Star  ");

        assertEquals(userId, profile.getUserId());
        assertEquals("northstar", profile.getUsername());
        assertEquals("North Star", profile.getDisplayName());
    }

    @Test
    void rejectsBlankPublicIdentityFields() {
        UUID userId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> new PlayerProfile(userId, " ", "Player"));
        assertThrows(IllegalArgumentException.class, () -> new PlayerProfile(userId, "player", " "));
    }

    @Test
    void rejectsMissingUserId() {
        assertThrows(NullPointerException.class, () -> new PlayerProfile(null, "player", "Player"));
    }
}
