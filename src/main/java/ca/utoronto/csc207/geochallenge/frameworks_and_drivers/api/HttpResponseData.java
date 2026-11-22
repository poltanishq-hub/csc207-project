package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.api;

import java.util.Objects;

/** Minimal HTTP response data required by the Supabase data-access adapters. */
public final class HttpResponseData {
    private final int statusCode;
    private final String body;

    public HttpResponseData(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = Objects.requireNonNull(body, "Response body is required.");
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
