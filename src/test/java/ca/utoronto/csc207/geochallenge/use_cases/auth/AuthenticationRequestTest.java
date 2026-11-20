package ca.utoronto.csc207.geochallenge.use_cases.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationRequestTest {
    @Test
    void registrationRequestDefensivelyCopiesAndClearsPassword() {
        char[] password = "secret".toCharArray();
        RegistrationRequest request = new RegistrationRequest("traveler", "Traveler", "t@example.com", password);
        password[0] = 'X';

        assertArrayEquals("secret".toCharArray(), request.getPassword());
        assertEquals("traveler", request.getUsername());

        request.clearPassword();

        assertArrayEquals(new char[] {'\0', '\0', '\0', '\0', '\0', '\0'}, request.getPassword());
    }

    @Test
    void loginRequestDefensivelyCopiesAndClearsPassword() {
        char[] password = "secret".toCharArray();
        LoginRequest request = new LoginRequest("t@example.com", password);
        char[] returnedPassword = request.getPassword();
        returnedPassword[0] = 'X';

        assertArrayEquals("secret".toCharArray(), request.getPassword());
        assertEquals("t@example.com", request.getEmail());

        request.clearPassword();

        assertArrayEquals(new char[] {'\0', '\0', '\0', '\0', '\0', '\0'}, request.getPassword());
    }
}
