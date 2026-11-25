package org.brutality.system;

import java.time.Instant;

public final class EntitlementState {
    private static final boolean flag = Boolean.getBoolean("brut.dev");
    private static volatile boolean flag2;
    private static volatile long timestamp;
    private static volatile boolean flag3;
    private static volatile boolean flag4;
    private static volatile long timestamp2;

    private EntitlementState() {
    }

    public static boolean checkState() {
        return flag
            || flag2 && (timestamp <= 0L || timestamp > Instant.now().getEpochSecond()) && flag3 && (flag4 || timestamp2 > Instant.now().getEpochSecond());
    }

    public static long getTimestamp2() {
        return timestamp2;
    }

    public static void handleFlag(boolean baseValid, long baseExpiry, boolean active, boolean lifetime, long expiresAt) {
        flag2 = baseValid;
        timestamp = baseExpiry;
        flag3 = active;
        flag4 = lifetime;
        timestamp2 = expiresAt;
    }

    public static void clear() {
        handleFlag(false, 0L, false, false, 0L);
    }
}
