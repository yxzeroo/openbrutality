package org.brutality.utils.animations;

import java.math.BigDecimal;
import java.math.MathContext;
import net.minecraft.client.Minecraft;

public final class AnimationsHelper {
    private double x2;
    private double y2;
    private final long timestamp = System.currentTimeMillis();

    public void handleDouble(double d, double d2) {
        this.x2 = computeDouble(this.x2, d, 1.0);
        this.y2 = computeDouble(this.y2, d2, 1.0);
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public static double computeDouble(double d, double d2) {
        double d3 = d2 / 2.0;
        double d4 = StrictMath.floor(d / d2) * d2;
        return d >= d4 + d3
            ? new BigDecimal(StrictMath.ceil(d / d2) * d2, MathContext.DECIMAL64).stripTrailingZeros().doubleValue()
            : new BigDecimal(d4, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
    }

    public double getX() {
        return this.x2;
    }

    public double getY() {
        return this.y2;
    }

    public static double computeDouble(double d, double d2, double d3) {
        double d4 = Math.abs(d - d2);
        int debugFPS = Minecraft.getDebugFPS();
        if (d4 > 0.0) {
            double d5 = computeDouble(Math.min(10.0, Math.max(0.0625, 144.0 / debugFPS * d4 / 10.0 * d3)), 0.0625);
            if (d4 < d5) {
                d5 = d4;
            }

            if (d < d2) {
                return d + d5;
            }

            if (d > d2) {
                return d - d5;
            }
        }

        return d;
    }

    public void handleFloat(float f, float f2) {
        this.x2 = (float)computeDouble(this.x2, f, 1.0);
        this.y2 = (float)computeDouble(this.y2, f2, 1.0);
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public AnimationsHelper(double x2, double y2) {
        this.x2 = x2;
        this.y2 = y2;
    }

    public static float computeFloat(float f, float f2, float f3, float f4) {
        return (float)computeDouble(f, f2, Math.max(10.0F, Math.abs(f - f2) * f3) * f4);
    }
}
