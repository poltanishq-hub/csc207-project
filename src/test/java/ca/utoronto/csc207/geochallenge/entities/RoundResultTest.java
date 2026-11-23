package ca.utoronto.csc207.geochallenge.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoundResultTest {

    @Test
    void createsValidRoundResult() {
        GeoPoint guess = new GeoPoint(43.66, -79.39);
        RoundResult result = new RoundResult(1, "loc_cn_tower", guess, 12.5, 4968);

        assertEquals(1, result.getRoundNumber());
        assertEquals("loc_cn_tower", result.getLocationId());
        assertEquals(guess, result.getGuess());
        assertEquals(12.5, result.getDistanceKm());
        assertEquals(4968, result.getScore());
    }

    @Test
    void rejectsInvalidRoundNumber() {
        GeoPoint guess = new GeoPoint(0.0, 0.0);
        assertThrows(IllegalArgumentException.class, () ->
                new RoundResult(0, "loc1", guess, 10.0, 4000));
        assertThrows(IllegalArgumentException.class, () ->
                new RoundResult(6, "loc1", guess, 10.0, 4000));
    }

    @Test
    void rejectsInvalidScore() {
        GeoPoint guess = new GeoPoint(0.0, 0.0);
        assertThrows(IllegalArgumentException.class, () ->
                new RoundResult(1, "loc1", guess, 10.0, -1));
        assertThrows(IllegalArgumentException.class, () ->
                new RoundResult(1, "loc1", guess, 10.0, 5001));
    }

    @Test
    void rejectsNegativeOrNonFiniteDistance() {
        GeoPoint guess = new GeoPoint(0.0, 0.0);
        assertThrows(IllegalArgumentException.class, () ->
                new RoundResult(1, "loc1", guess, -0.1, 4000));
        assertThrows(IllegalArgumentException.class, () ->
                new RoundResult(1, "loc1", guess, Double.NaN, 4000));
    }

    @Test
    void rejectsMissingLocationOrGuess() {
        GeoPoint guess = new GeoPoint(0.0, 0.0);
        assertThrows(IllegalArgumentException.class, () ->
                new RoundResult(1, "  ", guess, 10.0, 4000));
        assertThrows(IllegalArgumentException.class, () ->
                new RoundResult(1, null, guess, 10.0, 4000));
        assertThrows(IllegalArgumentException.class, () ->
                new RoundResult(1, "loc1", null, 10.0, 4000));
    }
}
