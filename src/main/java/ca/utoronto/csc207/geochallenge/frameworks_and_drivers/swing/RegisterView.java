package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.swing;

import ca.utoronto.csc207.geochallenge.interface_adapters.controllers.AuthController;
import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.AppNavigator;
import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.AppScreen;
import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.RegisterViewModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Objects;

/** Swing account-registration panel. */
public final class RegisterView extends JPanel {
    private final AuthController controller;
    private final AppNavigator navigator;
    private final RegisterViewModel viewModel;
    private final JTextField usernameField = new JTextField(24);
    private final JTextField displayNameField = new JTextField(24);
    private final JTextField emailField = new JTextField(24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final JButton registerButton = new JButton("Create account");
    private final JButton loginButton = new JButton("Back to log in");
    private final JLabel errorLabel = new JLabel(" ");

    public RegisterView(AuthController controller, AppNavigator navigator, RegisterViewModel viewModel) {
        super(new BorderLayout());
        this.controller = Objects.requireNonNull(controller, "Controller is required.");
        this.navigator = Objects.requireNonNull(navigator, "Navigator is required.");
        this.viewModel = Objects.requireNonNull(viewModel, "View model is required.");

        buildLayout();
        bindActions();
        viewModel.addPropertyChangeListener(event -> refreshFromViewModel());
        refreshFromViewModel();
    }

    private void buildLayout() {
        setBorder(BorderFactory.createEmptyBorder(24, 160, 24, 160));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 6, 5, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;

        addRow(form, constraints, 0, "Username", usernameField);
        addRow(form, constraints, 1, "Display name", displayNameField);
        addRow(form, constraints, 2, "Email", emailField);
        addRow(form, constraints, 3, "Password", passwordField);

        errorLabel.setForeground(new Color(170, 0, 0));
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        form.add(errorLabel, constraints);

        JPanel actions = new JPanel(new GridLayout(2, 1, 6, 6));
        actions.add(registerButton);
        actions.add(loginButton);
        constraints.gridy = 5;
        form.add(actions, constraints);

        add(form, BorderLayout.CENTER);
    }

    private static void addRow(JPanel panel, GridBagConstraints constraints, int row,
                               String labelText, java.awt.Component field) {
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0.0;
        panel.add(new JLabel(labelText), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        panel.add(field, constraints);
    }

    private void bindActions() {
        registerButton.addActionListener(event -> submitRegistration());
        loginButton.addActionListener(event -> navigator.show(AppScreen.LOGIN));
    }

    private void submitRegistration() {
        final String username = usernameField.getText();
        final String displayName = displayNameField.getText();
        final String email = emailField.getText();
        final char[] password = passwordField.getPassword();
        passwordField.setText("");
        setBusy(true);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.register(username, displayName, email, password);
                return null;
            }

            @Override
            protected void done() {
                setBusy(false);
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        usernameField.setEnabled(!busy);
        displayNameField.setEnabled(!busy);
        emailField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        registerButton.setEnabled(!busy);
        loginButton.setEnabled(!busy);
    }

    private void refreshFromViewModel() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refreshFromViewModel);
            return;
        }
        String message = viewModel.getErrorMessage();
        errorLabel.setText(message.isEmpty() ? " " : message);
    }
}
