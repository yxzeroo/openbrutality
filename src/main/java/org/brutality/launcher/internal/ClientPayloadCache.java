package org.brutality.launcher.internal;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

final class ClientPayloadCache {
    private ClientPayloadCache() {
    }

    static Path computePath(String str) throws PayloadBootstrapException {
        if (str != null && str.matches("[a-fA-F0-9]{64}")) {
            return Path.of(System.getProperty("user.home"), "Brutality", "cache", "clients", str.toLowerCase(Locale.ROOT) + ".jar");
        }
        throw new PayloadBootstrapException("Invalid client checksum in manifest.");
    }

    static void handleIntArray(byte[] bytes, PayloadManifest payloadManifest) throws Exception {
        Ed25519SignatureVerifier.handleIntArray(bytes, payloadManifest);
        Path path = computePath(payloadManifest.text2);
        Files.createDirectories(path.getParent());
        Path path2 = Files.createTempFile(path.getParent(), "client-", ".download");

        try {
            Files.write(path2, bytes);

            try {
                Files.move(path2, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(path2);
        }
    }

    static Path computePath(PayloadManifest payloadManifest) throws Exception {
        Path path = computePath(payloadManifest.text2);
        if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
            return null;
        }
        try {
            Ed25519SignatureVerifier.handleIntArray(Files.readAllBytes(path), payloadManifest);
            return path;
        } catch (SecurityException | IOException e) {
            return null;
        }
    }
}
