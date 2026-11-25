package org.brutality.utils.render;

public final class RenderHelper9 {
    public static final int count = 300;
    public static final int count2 = 550;
    public static final int count3 = 500;

    private RenderHelper9() {
    }

    public static float computeFloat2(long l) {
        return -10.0F * (1.0F - computeFloat((float)l / 300.0F));
    }

    public static float computeFloat(long l) {
        return computeFloat((float)l / 550.0F);
    }

    public static float computeFloat3(long l) {
        return computeFloat((float)l / 500.0F);
    }

    private static float computeFloat(float f) {
        float f2 = Math.max(0.0F, Math.min(1.0F, f));
        return f2 * f2 * (3.0F - 2.0F * f2);
    }
}
