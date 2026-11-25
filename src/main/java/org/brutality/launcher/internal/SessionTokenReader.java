package org.brutality.launcher.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

final class SessionTokenReader {
    private SessionTokenReader() {
    }

    static String computeString() throws Exception {
        String str = System.getProperty("user.name", "unknown")
            + "|"
            + System.getProperty("os.name", "unknown")
            + "|"
            + System.getProperty("os.arch", "unknown")
            + "|"
            + System.getenv().getOrDefault("COMPUTERNAME", "unknown");
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(str.getBytes(StandardCharsets.UTF_8)));
    }
}
