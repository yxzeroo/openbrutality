package org.brutality.utils.player;

import java.util.Random;

public class PlayerHelper2 {
    private static final Random random = new Random();
    public static int count = 0;

    public int computeInt(int n) {
        return this.computeInt(n, n);
    }

    public int computeInt(int n, int n2) {
        if (n < n2) {
            int n3 = n;
            n = n2;
            n2 = n3;
        }

        n = Math.max(1, Math.min(20, n));
        n2 = Math.max(1, Math.min(20, n2));
        double d = n2 + (n - n2) * random.nextDouble();
        double d2 = 1000.0 / d;
        int n4 = (int)Math.max(1L, Math.round(d2 / 50.0));
        int n5 = n4 * 50;
        count = n5;
        return n5;
    }
}
