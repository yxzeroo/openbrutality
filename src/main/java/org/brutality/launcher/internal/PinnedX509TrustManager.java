package org.brutality.launcher.internal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.stream.Stream;

final class PinnedX509TrustManager {
    private PinnedX509TrustManager() {
    }

    static Path computePath(byte[] bytes, String str, String str2) throws Exception {
        String str3 = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(str2.getBytes(StandardCharsets.UTF_8)));
        Path path = Path.of(System.getProperty("user.home"), "Brutality", "payloads", str3);
        Files.createDirectories(path);
        Path path2 = Files.createTempDirectory(path, "instance-");
        Path path3 = Files.createTempFile(path2, "license-", ".download");

        try {
            Files.writeString(path3, str2, StandardCharsets.UTF_8);
            Files.move(path3, path2.resolve("license.dat"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            Files.deleteIfExists(path3);
        }

        Path path4 = path2.resolve("payload-" + str.replaceAll("[^A-Za-z0-9._-]", "_") + ".jar");
        Path path5 = Files.createTempFile(path2, "payload-", ".download");

        Path path6;
        try {
            Files.write(path5, bytes);
            Files.move(path5, path4, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            path6 = path4;
        } finally {
            Files.deleteIfExists(path5);
        }

        return path6;
    }

    static void handlePath(Path path) {
        b(path.getParent());
    }

    private static void b(Path path) {
        if (path != null && path.getFileName().toString().startsWith("instance-")) {
            try (var stream = Files.walk(path)) {
                for (Path path2 : stream.sorted(Comparator.reverseOrder()).toList()) {
                    try {
                        Files.deleteIfExists(path2);
                    } catch (IOException ignored) {
                    }
                }
            } catch (IOException ignored2) {
            }
        }
    }
}
