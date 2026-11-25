package org.brutality.utils.misc;

import java.util.Random;

public class NextLongHelper {
    private static final Random random = new Random();

    public static long nextLong(long l, long l2) {
        return (long)nextDouble(l, l2 + 1L);
    }

    public static float computeFloat(float f, float f2) {
        return random.nextFloat() * (f2 - f) + f;
    }

    public static double nextDouble(double d, double d2) {
        return random.nextDouble() * (d2 - d) + d;
    }
}
