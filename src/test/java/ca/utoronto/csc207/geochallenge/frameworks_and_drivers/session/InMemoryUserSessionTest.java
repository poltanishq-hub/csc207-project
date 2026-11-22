package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.session;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InMemoryUserSessionTest {
    @Test
    void exposesActivePlayerUntilLogoutClearsTheSession() {
        InMemoryUserSession session = new InMemoryUserSession();
        PlayerProfile player = new PlayerProfile(UUID.randomUUID(), "traveler_1", "Traveler");

        assertFalse(session.currentUser().isPresent());

        session.start(player);
        assertEquals(player, session.currentUser().orElseThrow());

        session.clear();
        assertFalse(session.currentUser().isPresent());
    }
}
