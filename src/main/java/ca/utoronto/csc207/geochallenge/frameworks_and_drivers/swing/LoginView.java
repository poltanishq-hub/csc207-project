package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.swing;

import ca.utoronto.csc207.geochallenge.interface_adapters.controllers.AuthController;
import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.AppNavigator;
import ca.utoronto.csc207.geochallenge.interface_adapters.navigation.AppScreen;
import ca.utoronto.csc207.geochallenge.interface_adapters.view_models.LoginViewModel;

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

/** Swing login panel. It forwards user actions to the controller and displays view-model state. */
public final class LoginView extends JPanel {
    private final AuthController controller;
    private final AppNavigator navigator;
    private final LoginViewModel viewModel;
    private final JTextField emailField = new JTextField(24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final JButton loginButton = new JButton("Log in");
    private final JButton registerButton = new JButton("Create an account");
    private final JLabel errorLabel = new JLabel(" ");

    public LoginView(AuthController controller, AppNavigator navigator, LoginViewModel viewModel) {
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
        setBorder(BorderFactory.createEmptyBorder(32, 160, 32, 160));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;

        addRow(form, constraints, 0, "Email", emailField);
        addRow(form, constraints, 1, "Password", passwordField);

        errorLabel.setForeground(new Color(170, 0, 0));
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        form.add(errorLabel, constraints);

        JPanel actions = new JPanel(new GridLayout(2, 1, 6, 6));
        actions.add(loginButton);
        actions.add(registerButton);
        constraints.gridy = 3;
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
        loginButton.addActionListener(event -> submitLogin());
        registerButton.addActionListener(event -> navigator.show(AppScreen.REGISTER));
    }

    private void submitLogin() {
        final String email = emailField.getText();
        final char[] password = passwordField.getPassword();
        passwordField.setText("");
        setBusy(true);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.login(email, password);
                return null;
            }

            @Override
            protected void done() {
                setBusy(false);
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        emailField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        loginButton.setEnabled(!busy);
        registerButton.setEnabled(!busy);
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
