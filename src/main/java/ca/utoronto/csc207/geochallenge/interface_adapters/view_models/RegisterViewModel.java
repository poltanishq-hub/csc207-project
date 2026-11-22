package ca.utoronto.csc207.geochallenge.interface_adapters.view_models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** Presentation state for the account-registration form. */
public final class RegisterViewModel {
    public static final String REGISTER_STATE_CHANGED = "registerStateChanged";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String errorMessage = "";

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage == null ? "" : errorMessage;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void fireStateChanged() {
        support.firePropertyChange(REGISTER_STATE_CHANGED, null, this);
    }
}
