package ca.utoronto.csc207.geochallenge.interface_adapters.controllers;

import ca.utoronto.csc207.geochallenge.use_cases.auth.LoginInputBoundary;
import ca.utoronto.csc207.geochallenge.use_cases.auth.LoginInputData;
import ca.utoronto.csc207.geochallenge.use_cases.auth.LogoutInputBoundary;
import ca.utoronto.csc207.geochallenge.use_cases.auth.RegisterInputBoundary;
import ca.utoronto.csc207.geochallenge.use_cases.auth.RegisterInputData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthControllerTest {
    @Test
    void forwardsRegistrationFormDataAndClearsCallersPasswordArray() {
        RecordingRegisterUseCase registerUseCase = new RecordingRegisterUseCase();
        AuthController controller = controller(registerUseCase, new RecordingLoginUseCase(), new RecordingLogoutUseCase());
        char[] password = "password1".toCharArray();

        controller.register("traveler_1", "Traveler", "traveler@example.com", password);

        assertEquals("traveler_1", registerUseCase.inputData.getUsername());
        assertEquals("Traveler", registerUseCase.inputData.getDisplayName());
        assertEquals("traveler@example.com", registerUseCase.inputData.getEmail());
        assertArrayEquals("password1".toCharArray(), registerUseCase.inputData.getPassword());
        assertArrayEquals(new char[9], password);
    }

    @Test
    void forwardsLoginAndLogoutToTheirDedicatedUseCases() {
        RecordingLoginUseCase loginUseCase = new RecordingLoginUseCase();
        RecordingLogoutUseCase logoutUseCase = new RecordingLogoutUseCase();
        AuthController controller = controller(new RecordingRegisterUseCase(), loginUseCase, logoutUseCase);
        char[] password = "password1".toCharArray();

        controller.login("traveler@example.com", password);
        controller.logout();

        assertEquals("traveler@example.com", loginUseCase.inputData.getEmail());
        assertArrayEquals("password1".toCharArray(), loginUseCase.inputData.getPassword());
        assertArrayEquals(new char[9], password);
        assertTrue(logoutUseCase.called);
    }

    private static AuthController controller(RegisterInputBoundary registerUseCase,
                                             LoginInputBoundary loginUseCase,
                                             LogoutInputBoundary logoutUseCase) {
        return new AuthController(registerUseCase, loginUseCase, logoutUseCase);
    }

    private static final class RecordingRegisterUseCase implements RegisterInputBoundary {
        private RegisterInputData inputData;

        @Override
        public void execute(RegisterInputData inputData) {
            this.inputData = inputData;
        }
    }

    private static final class RecordingLoginUseCase implements LoginInputBoundary {
        private LoginInputData inputData;

        @Override
        public void execute(LoginInputData inputData) {
            this.inputData = inputData;
        }
    }

    private static final class RecordingLogoutUseCase implements LogoutInputBoundary {
        private boolean called;

        @Override
        public void execute() {
            called = true;
        }
    }
}
