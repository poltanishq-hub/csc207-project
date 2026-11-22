package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access;

import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.api.HttpResponseData;
import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.api.HttpTransport;
import ca.utoronto.csc207.geochallenge.use_cases.auth.AuthenticationFailure;
import ca.utoronto.csc207.geochallenge.use_cases.auth.AuthenticationResult;
import ca.utoronto.csc207.geochallenge.use_cases.auth.LoginRequest;
import ca.utoronto.csc207.geochallenge.use_cases.auth.RegistrationRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SupabaseAuthGatewayTest {
    private static final String USER_ID = "2a27783f-2bf7-452c-bd9f-2f2b6fd9909e";
    private static final String AUTH_RESPONSE = "{\"access_token\":\"token-123\",\"user\":{\"id\":\""
            + USER_ID + "\"}}";
    private static final String PROFILE_RESPONSE = "[{\"user_id\":\"" + USER_ID
            + "\",\"username\":\"traveler_1\",\"display_name\":\"Traveler\"}]";

    @Test
    void logsInAndLoadsTheMatchingPublicProfile() {
        FakeTransport transport = new FakeTransport(
                new HttpResponseData(200, AUTH_RESPONSE),
                new HttpResponseData(200, PROFILE_RESPONSE));
        SupabaseAuthGateway gateway = gateway(transport);

        AuthenticationResult result = gateway.login(
                new LoginRequest("traveler@example.com", "password1".toCharArray()));

        assertTrue(result.isSuccess());
        assertEquals("traveler_1", result.getPlayerProfile().orElseThrow().getUsername());
        assertEquals("Traveler", result.getPlayerProfile().orElseThrow().getDisplayName());
        assertEquals(2, transport.requests.size());
        assertEquals("/auth/v1/token", transport.requests.get(0).uri().getPath());
        assertEquals("grant_type=password", transport.requests.get(0).uri().getQuery());
        assertEquals("publishable-key", transport.requests.get(0).headers()
                .firstValue("apikey").orElseThrow());
        assertEquals("/rest/v1/geochallenge_profiles", transport.requests.get(1).uri().getPath());
        assertEquals("Bearer token-123", transport.requests.get(1).headers()
                .firstValue("Authorization").orElseThrow());
    }

    @Test
    void registersWithPublicProfileMetadataThenLoadsProfile() {
        FakeTransport transport = new FakeTransport(
                new HttpResponseData(200, AUTH_RESPONSE),
                new HttpResponseData(200, PROFILE_RESPONSE));
        SupabaseAuthGateway gateway = gateway(transport);

        AuthenticationResult result = gateway.register(new RegistrationRequest(
                "traveler_1", "Traveler", "traveler@example.com", "password1".toCharArray()));

        assertTrue(result.isSuccess());
        assertEquals("POST", transport.requests.get(0).method());
        assertEquals("/auth/v1/signup", transport.requests.get(0).uri().getPath());
        assertEquals(2, transport.requests.size());
    }

    @Test
    void mapsDuplicateUsernameProviderFailureWithoutFetchingProfile() {
        FakeTransport transport = new FakeTransport(new HttpResponseData(
                409, "{\"message\":\"geochallenge_profiles_username_key already exists\"}"));
        SupabaseAuthGateway gateway = gateway(transport);

        AuthenticationResult result = gateway.register(new RegistrationRequest(
                "traveler_1", "Traveler", "traveler@example.com", "password1".toCharArray()));

        assertFalse(result.isSuccess());
        assertEquals(AuthenticationFailure.USERNAME_UNAVAILABLE, result.getFailure().orElseThrow());
        assertEquals(1, transport.requests.size());
    }

    @Test
    void mapsRejectedLoginToInvalidCredentials() {
        FakeTransport transport = new FakeTransport(
                new HttpResponseData(400, "{\"error\":\"invalid_grant\"}"));
        SupabaseAuthGateway gateway = gateway(transport);

        AuthenticationResult result = gateway.login(
                new LoginRequest("traveler@example.com", "wrongpass".toCharArray()));

        assertFalse(result.isSuccess());
        assertEquals(AuthenticationFailure.INVALID_CREDENTIALS, result.getFailure().orElseThrow());
    }

    @Test
    void mapsTransportFailureToServiceUnavailable() {
        FakeTransport transport = new FakeTransport();
        transport.ioException = new IOException("Network unavailable");
        SupabaseAuthGateway gateway = gateway(transport);

        AuthenticationResult result = gateway.login(
                new LoginRequest("traveler@example.com", "password1".toCharArray()));

        assertFalse(result.isSuccess());
        assertEquals(AuthenticationFailure.SERVICE_UNAVAILABLE, result.getFailure().orElseThrow());
    }

    @Test
    void rejectsMissingTokenWhenEmailConfirmationIsStillEnabled() {
        FakeTransport transport = new FakeTransport(new HttpResponseData(
                200, "{\"user\":{\"id\":\"" + USER_ID + "\"}}"));
        SupabaseAuthGateway gateway = gateway(transport);

        AuthenticationResult result = gateway.register(new RegistrationRequest(
                "traveler_1", "Traveler", "traveler@example.com", "password1".toCharArray()));

        assertFalse(result.isSuccess());
        assertEquals(AuthenticationFailure.SERVICE_UNAVAILABLE, result.getFailure().orElseThrow());
        assertEquals(1, transport.requests.size());
    }

    @Test
    void configurationRejectsMissingOrNonHttpsValues() {
        assertThrows(IllegalArgumentException.class, () -> new SupabaseConfiguration(null, "key"));
        assertThrows(IllegalArgumentException.class, () -> new SupabaseConfiguration("http://localhost", "key"));
        assertThrows(IllegalArgumentException.class,
                () -> new SupabaseConfiguration("https://project.supabase.co", " "));
    }

    private static SupabaseAuthGateway gateway(FakeTransport transport) {
        return new SupabaseAuthGateway(
                new SupabaseConfiguration("https://project.supabase.co", "publishable-key"), transport);
    }

    private static final class FakeTransport implements HttpTransport {
        private final Queue<HttpResponseData> responses = new ArrayDeque<>();
        private final List<HttpRequest> requests = new ArrayList<>();
        private IOException ioException;

        FakeTransport(HttpResponseData... responseData) {
            for (HttpResponseData response : responseData) {
                responses.add(response);
            }
        }

        @Override
        public HttpResponseData send(HttpRequest request) throws IOException {
            requests.add(request);
            if (ioException != null) {
                throw ioException;
            }
            return responses.remove();
        }
    }
}
