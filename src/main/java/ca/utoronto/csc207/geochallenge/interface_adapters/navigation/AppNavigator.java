package ca.utoronto.csc207.geochallenge.interface_adapters.navigation;

/** Adapter boundary for application navigation; Swing implements this in the outer layer. */
public interface AppNavigator {
    void show(AppScreen screen);
}
