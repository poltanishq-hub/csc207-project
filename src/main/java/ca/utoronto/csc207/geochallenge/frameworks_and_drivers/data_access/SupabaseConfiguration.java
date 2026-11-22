package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.data_access;

import java.net.URI;
import java.util.Objects;

/** Immutable public-client configuration for the shared Supabase project. */
public final class SupabaseConfiguration {
    private final URI projectUri;
    private final String publishableKey;

    public SupabaseConfiguration(String projectUrl, String publishableKey) {
        if (projectUrl == null || projectUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("SUPABASE_URL is required.");
        }
        if (publishableKey == null || publishableKey.trim().isEmpty()) {
            throw new IllegalArgumentException("SUPABASE_PUBLISHABLE_KEY is required.");
        }

        URI parsedUri = URI.create(projectUrl.trim());
        if (!"https".equalsIgnoreCase(parsedUri.getScheme()) || parsedUri.getHost() == null) {
            throw new IllegalArgumentException("SUPABASE_URL must be an HTTPS project URL.");
        }
        this.projectUri = URI.create(ensureTrailingSlash(parsedUri.toString()));
        this.publishableKey = publishableKey.trim();
    }

    /** Reads local launch configuration; .env loading remains an outer application concern. */
    public static SupabaseConfiguration fromEnvironment() {
        return new SupabaseConfiguration(
                System.getenv("SUPABASE_URL"),
                System.getenv("SUPABASE_PUBLISHABLE_KEY"));
    }

    private static String ensureTrailingSlash(String value) {
        return value.endsWith("/") ? value : value + "/";
    }

    public URI getProjectUri() {
        return projectUri;
    }

    public String getPublishableKey() {
        return publishableKey;
    }

    /** Resolves a provider-relative endpoint without exposing config details to use cases. */
    public URI endpoint(String pathAndQuery) {
        return projectUri.resolve(Objects.requireNonNull(pathAndQuery, "Endpoint is required."));
    }
}
