package org.brutality.utils.hypixel.pit;

public final class PitHelper {
    private static final long timestamp = 1660849185000L;
    private static final long timestamp2 = 740000L;
    private static final long timestamp3 = 1420000L;
    private static final long timestamp4 = 2160000L;

    public static long computeLong2(long l) {
        long l2 = computeLong(l);
        return (l2 < 740000L ? 740000L : 2160000L) - l2;
    }

    public static boolean matchesLong(long l) {
        return computeLong(l) < 740000L;
    }

    private PitHelper() {
    }

    private static long computeLong(long l) {
        return Math.floorMod(l - 1660849185000L, 2160000L);
    }
}
