package org.brutality.utils.render.shader;

import java.util.Locale;

public final class IntelShader {
    public static boolean matchesString(String str) {
        if (str == null) {
            return false;
        }
        String str2 = str.toLowerCase(Locale.ROOT);
        return str2.contains("intel") || str2.contains("llvmpipe") || str2.contains("softpipe") || str2.contains("microsoft basic");
    }

    private IntelShader() {
    }

    public static long computeLong2(boolean flag) {
        return flag ? 33333333L : 16666666L;
    }

    public static long computeLong(boolean flag) {
        return flag ? 200000000L : 100000000L;
    }

    public static int[] computeIntArray(float f, float f2, boolean flag) {
        int n = flag ? 960 : 1600;
        int n2 = flag ? 540 : 900;
        double d = Math.min(1.0, Math.min(n / Math.max(1.0, (double)f), n2 / Math.max(1.0, (double)f2)));
        return new int[]{Math.max(1, (int)(f * d)), Math.max(1, (int)(f2 * d))};
    }

    public static int computeInt(boolean flag) {
        return flag ? 192 : 256;
    }
}
