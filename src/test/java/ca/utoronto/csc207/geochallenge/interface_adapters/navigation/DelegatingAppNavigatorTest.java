package ca.utoronto.csc207.geochallenge.interface_adapters.navigation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DelegatingAppNavigatorTest {
    @Test
    void forwardsNavigationAfterTheFrameIsConnected() {
        DelegatingAppNavigator navigator = new DelegatingAppNavigator();
        RecordingNavigator destination = new RecordingNavigator();

        navigator.setDelegate(destination);
        navigator.show(AppScreen.HOME);

        assertEquals(AppScreen.HOME, destination.lastScreen);
    }

    @Test
    void rejectsNavigationBeforeCompositionCompletes() {
        DelegatingAppNavigator navigator = new DelegatingAppNavigator();

        assertThrows(IllegalStateException.class, () -> navigator.show(AppScreen.LOGIN));
    }

    private static final class RecordingNavigator implements AppNavigator {
        private AppScreen lastScreen;

        @Override
        public void show(AppScreen screen) {
            lastScreen = screen;
        }
    }
}
