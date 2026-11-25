package org.brutality.launcher.internal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

final class Log4jRuntimeValidator {
    static final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10L))
        .sslContext(ClientBootstrapMain.getSslContext())
        .followRedirects(Redirect.NEVER)
        .build();
    private static final String b = "https://bru3y2ty22n2n2nsy6y2hb3b3g2.up.railway.app/api/client/manifest";

    private Log4jRuntimeValidator() {
    }

    static String computeString(String str, String str2) throws Exception {
        String str3 = "{\"license_key\":"
            + ManifestJsonParser.computeString(str)
            + ",\"hwid\":"
            + ManifestJsonParser.computeString(str2)
            + ",\"client_version\":\"bootstrap\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://bru3y2ty22n2n2nsy6y2hb3b3g2.up.railway.app/api/client/manifest"))
            .timeout(Duration.ofSeconds(15L))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(str3))
            .build();
        HttpResponse response = httpClient.send(request, BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            String str4 = null;

            try {
                str4 = ManifestJsonParser.computeString((String)response.body(), "code");
            } catch (RuntimeException ignored) {
            }

            throw PayloadBootstrapException.computePayloadBootstrapException(response.statusCode(), str4);
        }
        return (String)response.body();
    }
}
