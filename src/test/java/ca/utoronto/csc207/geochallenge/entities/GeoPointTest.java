package ca.utoronto.csc207.geochallenge.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeoPointTest {
    @Test
    void acceptsValidCoordinates() {
        GeoPoint point = new GeoPoint(43.6532, -79.3832);

        assertEquals(43.6532, point.getLatitude());
        assertEquals(-79.3832, point.getLongitude());
    }

    @Test
    void rejectsLatitudeOutsideRange() {
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(90.1, 0.0));
    }

    @Test
    void rejectsLongitudeOutsideRange() {
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(0.0, -180.1));
    }

    @Test
    void rejectsNonFiniteCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(Double.NaN, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(0.0, Double.POSITIVE_INFINITY));
    }
}
