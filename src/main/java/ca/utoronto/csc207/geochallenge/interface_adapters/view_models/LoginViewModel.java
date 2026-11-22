package ca.utoronto.csc207.geochallenge.interface_adapters.view_models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** Presentation state for the login form. */
public final class LoginViewModel {
    public static final String LOGIN_STATE_CHANGED = "loginStateChanged";

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
        support.firePropertyChange(LOGIN_STATE_CHANGED, null, this);
    }
}
