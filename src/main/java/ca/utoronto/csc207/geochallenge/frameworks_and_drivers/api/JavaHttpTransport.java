package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.api;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

/** Production {@link HttpTransport} backed by Java's standard HTTP client. */
public final class JavaHttpTransport implements HttpTransport {
    private final HttpClient httpClient;

    public JavaHttpTransport(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient, "HTTP client is required.");
    }

    @Override
    public HttpResponseData send(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return new HttpResponseData(response.statusCode(), response.body());
    }
}
