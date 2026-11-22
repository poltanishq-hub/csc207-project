package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access;

import ca.utoronto.csc207.geochallenge.entities.PlayerProfile;
import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.api.HttpResponseData;
import ca.utoronto.csc207.geochallenge.frameworks_and_drivers.api.HttpTransport;
import ca.utoronto.csc207.geochallenge.use_cases.auth.AuthGateway;
import ca.utoronto.csc207.geochallenge.use_cases.auth.AuthenticationFailure;
import ca.utoronto.csc207.geochallenge.use_cases.auth.AuthenticationResult;
import ca.utoronto.csc207.geochallenge.use_cases.auth.LoginRequest;
import ca.utoronto.csc207.geochallenge.use_cases.auth.RegistrationRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

/**
 * Supabase Auth and REST implementation of {@link AuthGateway}.
 *
 * <p>The gateway uses only the project's publishable key. Credentials and access tokens exist
 * only for the duration of a request and are never logged or persisted by this class.</p>
 */
public final class SupabaseAuthGateway implements AuthGateway {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    private final SupabaseConfiguration configuration;
    private final HttpTransport httpTransport;
    private final ObjectMapper objectMapper;

    public SupabaseAuthGateway(SupabaseConfiguration configuration, HttpTransport httpTransport) {
        this(configuration, httpTransport, new ObjectMapper());
    }

    SupabaseAuthGateway(SupabaseConfiguration configuration, HttpTransport httpTransport,
                        ObjectMapper objectMapper) {
        this.configuration = java.util.Objects.requireNonNull(configuration, "Configuration is required.");
        this.httpTransport = java.util.Objects.requireNonNull(httpTransport, "HTTP transport is required.");
        this.objectMapper = java.util.Objects.requireNonNull(objectMapper, "Object mapper is required.");
    }

    @Override
    public AuthenticationResult register(RegistrationRequest request) {
        if (request == null) {
            return AuthenticationResult.failure(AuthenticationFailure.INVALID_INPUT);
        }

        char[] password = request.getPassword();
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("email", request.getEmail());
            body.put("password", password == null ? "" : new String(password));
            ObjectNode metadata = body.putObject("data");
            metadata.put("username", request.getUsername());
            metadata.put("display_name", request.getDisplayName());

            HttpResponseData response = sendJson("POST", "auth/v1/signup", body.toString(), null);
            if (!isSuccess(response)) {
                return AuthenticationResult.failure(mapRegistrationFailure(response));
            }
            return profileFromAuthenticationResponse(response.getBody());
        }
        catch (IOException exception) {
            return AuthenticationResult.failure(AuthenticationFailure.SERVICE_UNAVAILABLE);
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return AuthenticationResult.failure(AuthenticationFailure.SERVICE_UNAVAILABLE);
        }
        catch (RuntimeException exception) {
            return AuthenticationResult.failure(AuthenticationFailure.UNEXPECTED_FAILURE);
        }
        finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
        }
    }

    @Override
    public AuthenticationResult login(LoginRequest request) {
        if (request == null) {
            return AuthenticationResult.failure(AuthenticationFailure.INVALID_INPUT);
        }

        char[] password = request.getPassword();
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("email", request.getEmail());
            body.put("password", password == null ? "" : new String(password));

            HttpResponseData response = sendJson(
                    "POST", "auth/v1/token?grant_type=password", body.toString(), null);
            if (!isSuccess(response)) {
                return AuthenticationResult.failure(mapLoginFailure(response));
            }
            return profileFromAuthenticationResponse(response.getBody());
        }
        catch (IOException exception) {
            return AuthenticationResult.failure(AuthenticationFailure.SERVICE_UNAVAILABLE);
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return AuthenticationResult.failure(AuthenticationFailure.SERVICE_UNAVAILABLE);
        }
        catch (RuntimeException exception) {
            return AuthenticationResult.failure(AuthenticationFailure.UNEXPECTED_FAILURE);
        }
        finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
        }
    }

    /**
     * The application does not persist an Auth token, so local session clearing is sufficient.
     * A future persistent-token session implementation may revoke its token here.
     */
    @Override
    public void logout() {
        // No provider call: this gateway retains no server session or access token.
    }

    private AuthenticationResult profileFromAuthenticationResponse(String authenticationBody)
            throws IOException, InterruptedException {
        JsonNode authentication = objectMapper.readTree(authenticationBody);
        String userId = textAt(authentication, "user", "id");
        String accessToken = textAt(authentication, "access_token");
        if (userId == null || accessToken == null) {
            // This occurs when email confirmation is enabled. The MVP requires confirmation off
            // until a desktop confirmation/deep-link flow exists.
            return AuthenticationResult.failure(AuthenticationFailure.SERVICE_UNAVAILABLE);
        }

        HttpResponseData profileResponse = sendJson(
                "GET", profilePath(userId), null, accessToken);
        if (!isSuccess(profileResponse)) {
            return AuthenticationResult.failure(mapProfileFailure(profileResponse));
        }
        return parseProfile(profileResponse.getBody(), userId);
    }

    private HttpResponseData sendJson(String method, String pathAndQuery, String body, String accessToken)
            throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(configuration.endpoint(pathAndQuery))
                .timeout(REQUEST_TIMEOUT)
                .header("apikey", configuration.getPublishableKey())
                .header("Accept", "application/json");
        if (accessToken != null) {
            request.header("Authorization", "Bearer " + accessToken);
        }
        if ("POST".equals(method)) {
            request.header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body));
        }
        else if ("GET".equals(method)) {
            request.GET();
        }
        else {
            throw new IllegalArgumentException("Unsupported HTTP method.");
        }
        return httpTransport.send(request.build());
    }

    private String profilePath(String userId) {
        String select = URLEncoder.encode("user_id,username,display_name", StandardCharsets.UTF_8);
        return "rest/v1/geochallenge_profiles?select=" + select + "&user_id=eq." + userId;
    }

    private AuthenticationResult parseProfile(String body, String expectedUserId) throws IOException {
        JsonNode profiles = objectMapper.readTree(body);
        if (!profiles.isArray() || profiles.size() != 1) {
            return AuthenticationResult.failure(AuthenticationFailure.UNEXPECTED_FAILURE);
        }

        JsonNode profile = profiles.get(0);
        String returnedUserId = textAt(profile, "user_id");
        String username = textAt(profile, "username");
        String displayName = textAt(profile, "display_name");
        if (!expectedUserId.equals(returnedUserId) || username == null || displayName == null) {
            return AuthenticationResult.failure(AuthenticationFailure.UNEXPECTED_FAILURE);
        }

        try {
            return AuthenticationResult.success(
                    new PlayerProfile(UUID.fromString(returnedUserId), username, displayName));
        }
        catch (IllegalArgumentException exception) {
            return AuthenticationResult.failure(AuthenticationFailure.UNEXPECTED_FAILURE);
        }
    }

    private static boolean isSuccess(HttpResponseData response) {
        return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
    }

    private AuthenticationFailure mapRegistrationFailure(HttpResponseData response) {
        String body = response.getBody().toLowerCase(java.util.Locale.ROOT);
        if (body.contains("username") || body.contains("geochallenge_profiles_username")) {
            return AuthenticationFailure.USERNAME_UNAVAILABLE;
        }
        if (body.contains("user already registered") || body.contains("email")) {
            return AuthenticationFailure.EMAIL_UNAVAILABLE;
        }
        if (response.getStatusCode() == 400 || response.getStatusCode() == 409
                || response.getStatusCode() == 422) {
            return AuthenticationFailure.INVALID_INPUT;
        }
        return mapServiceFailure(response.getStatusCode());
    }

    private AuthenticationFailure mapLoginFailure(HttpResponseData response) {
        int status = response.getStatusCode();
        if (status == 400 || status == 401 || status == 403) {
            return AuthenticationFailure.INVALID_CREDENTIALS;
        }
        return mapServiceFailure(status);
    }

    private AuthenticationFailure mapProfileFailure(HttpResponseData response) {
        return response.getStatusCode() == 401 || response.getStatusCode() == 403
                ? AuthenticationFailure.INVALID_CREDENTIALS
                : mapServiceFailure(response.getStatusCode());
    }

    private static AuthenticationFailure mapServiceFailure(int statusCode) {
        return statusCode == 429 || statusCode >= 500
                ? AuthenticationFailure.SERVICE_UNAVAILABLE
                : AuthenticationFailure.UNEXPECTED_FAILURE;
    }

    private static String textAt(JsonNode node, String... fields) {
        JsonNode current = node;
        for (String field : fields) {
            if (current == null || !current.hasNonNull(field)) {
                return null;
            }
            current = current.get(field);
        }
        return current.isTextual() ? current.asText() : null;
    }
}
