package ca.utoronto.csc207.geochallenge.interface_adapters.navigation;

import java.util.Objects;

/**
 * An application navigator whose final Swing destination is supplied during composition.
 *
 * <p>This keeps presenters and views independent from the frame while allowing both to be
 * constructed before the frame that implements {@link AppNavigator} exists.</p>
 */
public final class DelegatingAppNavigator implements AppNavigator {
    private AppNavigator delegate;

    /** Connects this adapter to the final navigation implementation exactly once. */
    public void setDelegate(AppNavigator delegate) {
        if (this.delegate != null) {
            throw new IllegalStateException("Navigation delegate is already configured.");
        }
        this.delegate = Objects.requireNonNull(delegate, "Navigation delegate is required.");
    }

    @Override
    public void show(AppScreen screen) {
        if (delegate == null) {
            throw new IllegalStateException("Navigation delegate has not been configured.");
        }
        delegate.show(screen);
    }
}
