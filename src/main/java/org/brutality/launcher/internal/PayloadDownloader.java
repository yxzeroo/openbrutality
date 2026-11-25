package org.brutality.launcher.internal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

final class PayloadDownloader {
    private PayloadDownloader() {
    }

    private static Path computePath(byte[] bytes, PayloadManifest payloadManifest, String str) throws Exception {
        try {
            return PinnedX509TrustManager.computePath(bytes, payloadManifest.text, str);
        } catch (IOException e) {
            PayloadBootstrapException e2 = new PayloadBootstrapException(
                "The verified client could not be prepared locally. Check free disk space and folder permissions."
            );
            e2.initCause(e);
            throw e2;
        }
    }

    static Path computePath(PayloadManifest payloadManifest, String str, String str2, String str3) throws Exception {
        Path path = ClientPayloadCache.computePath(payloadManifest);
        if (path != null) {
            System.out.println("[Brutality] Client is up to date; using verified local copy.");
            byte[] bytes = Files.readAllBytes(path);
            Ed25519SignatureVerifier.handleIntArray(bytes, payloadManifest);
            return computePath(bytes, payloadManifest, str3);
        }
        System.out.println("[Brutality] Downloading updated client " + payloadManifest.text + ".");
        HttpRequest request = HttpRequest.newBuilder(URI.create(payloadManifest.b))
            .timeout(Duration.ofMinutes(5L))
            .header("X-Brutality-License", str)
            .header("X-Brutality-HWID", str2)
            .GET()
            .build();

        HttpResponse response;
        try {
            response = Log4jRuntimeValidator.httpClient.send(request, BodyHandlers.ofByteArray());
        } catch (IOException e) {
            PayloadBootstrapException e2 = new PayloadBootstrapException(
                "Payload download was interrupted or could not connect. Try again; if it repeats, contact support."
            );
            e2.initCause(e);
            throw e2;
        }

        if (response.statusCode() / 100 != 2) {
            throw PayloadBootstrapException.computePayloadBootstrapException("Payload download", response.statusCode());
        }
        Ed25519SignatureVerifier.handleIntArray((byte[])response.body(), payloadManifest);

        try {
            ClientPayloadCache.handleIntArray((byte[])response.body(), payloadManifest);
        } catch (IOException e3) {
            PayloadBootstrapException e4 = new PayloadBootstrapException(
                "The verified client could not be saved to the local cache. Check free disk space and folder permissions."
            );
            e4.initCause(e3);
            throw e4;
        }

        return computePath((byte[])response.body(), payloadManifest, str3);
    }
}
