package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.api;

import java.io.IOException;
import java.net.http.HttpRequest;

/** Small HTTP seam that allows outer-layer gateways to be tested without network access. */
public interface HttpTransport {
    HttpResponseData send(HttpRequest request) throws IOException, InterruptedException;
}
