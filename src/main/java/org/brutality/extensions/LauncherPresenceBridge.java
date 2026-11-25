package org.brutality.extensions;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

public final class LauncherPresenceBridge {
    private static long timestamp;
    private static boolean flag;
    private static final long timestamp2 = 1000L;

    public static boolean matchesString(String str) {
        long now = System.currentTimeMillis();
        if (now < timestamp) {
            return flag;
        }
        timestamp = now + timestamp2;
        String property = System.getProperty("brutality.presence.file");
        String property2 = System.getProperty("brutality.presence.parent");
        flag = false;
        if (property != null && property2 != null) {
            try {
                flag = ProcessHandle.of(Long.parseLong(property2)).map(ProcessHandle::isAlive).orElse(false);
                if (!flag) {
                    return false;
                }

                Path path = Path.of(property);
                Path path2 = path.resolveSibling(path.getFileName() + ".tmp");

                try {
                    Files.writeString(path2, computeString(str));

                    try {
                        Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                    } catch (AtomicMoveNotSupportedException e) {
                        Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
                    }
                } finally {
                    Files.deleteIfExists(path2);
                }
            } catch (IllegalArgumentException | IOException ignored) {
            }

            return flag;
        }
        return false;
    }

    private LauncherPresenceBridge() {
    }

    public static String computeString(String str) {
        if (str != null && !str.isBlank()) {
            String str2 = str.toLowerCase(Locale.ROOT).trim();
            int n = str2.indexOf(58);
            if (n > 0) {
                str2 = str2.substring(0, n);
            }

            String str3;
            if (str2.contains("hypixel") || str2.contains("liquidproxy")) {
                str3 = "Hypixel Pit";
            } else if (str2.contains("orionpit")) {
                str3 = "Orion Pit";
            } else if (str2.contains("pitclassic.net") || str2.contains("classicpit") || str2.contains("classic-pit") || str2.contains("classic.pit")) {
                str3 = "Classic Pit";
            } else if (str2.contains("hypit")) {
                str3 = "Hypit";
            } else if (str2.contains("harrys")) {
                str3 = "Harrys Pit";
            } else if (str2.contains("jungle")) {
                str3 = "Jungle Pit";
            } else if (str2.contains("godfights")) {
                str3 = "GodfightsPit";
            } else if (str2.contains("weasel")) {
                str3 = "Weasel Pit";
            } else {
                str3 = str2;
            }

            return "Playing (" + str3 + ")";
        }
        return "Idle";
    }
}
