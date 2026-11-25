package org.brutality.utils.render;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vector3d;

public final class RenderHelper4 {
    public static double clamp(double d, double d2, double d3) {
        return Math.max(d, Math.min(d2, d3));
    }

    public static float computeFloat(float f, float f2) {
        float f3 = Math.round(f / f2);
        return f2 * f3;
    }

    @Generated
    private RenderHelper4() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static double computeDouble2(double d, double d2) {
        double d3 = Math.round(d / d2) * d2;
        d3 *= 1000.0;
        d3 = (int)d3;
        return d3 / 1000.0;
    }

    public static double computeDouble(double d, int n, double d2) {
        double d3 = d2 / 2.0;
        double d4 = Math.floor(d / d2) * d2;
        return d >= d4 + d3
            ? new BigDecimal(Math.ceil(d / d2) * d2).setScale(n, RoundingMode.HALF_UP).doubleValue()
            : new BigDecimal(d4).setScale(n, RoundingMode.HALF_UP).doubleValue();
    }

    public static double computeDouble(double d, double d2, double d3, double d4, double d5, double d6) {
        double d7 = d4 - d;
        double d8 = d5 - d2;
        double d9 = d6 - d3;
        return MathHelper.sqrt_double(d7 * d7 + d8 * d8 + d9 * d9);
    }

    public static double computeDouble(double d, double d2) {
        return Math.min(Math.abs(d - d2), Math.min(Math.abs(d - 360.0) - Math.abs(d2 - 0.0), Math.abs(d2 - 360.0) - Math.abs(d - 0.0)));
    }

    public static double computeDouble(double d, int n) {
        try {
            BigDecimal bigDecimal = BigDecimal.valueOf(d);
            return bigDecimal.setScale(n, RoundingMode.HALF_UP).doubleValue();
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static double lerp(double d, double d2, double d3) {
        return d + d3 * (d2 - d);
    }

    public static double computeDouble3(double d, double d2) {
        if (d == d2) {
            return d;
        }
        if (d > d2) {
            double d3 = d;
            d = d2;
            d2 = d3;
        }

        return ThreadLocalRandom.current().nextDouble(d, d2);
    }

    public static float computeFloat(float f, float f2, float f3) {
        return f + f3 * (f2 - f);
    }

    public static double computeDouble(Vector3d vector3d, Vector3d vector3d2) {
        double d = vector3d2.field_181059_a - vector3d.field_181060_b;
        double d2 = vector3d2.field_181061_c - vector3d.field_181061_c;
        double d3 = Math.toDegrees(Math.atan2(d2, d)) - 90.0;
        return MathHelper.wrapAngleTo180_float((float)d3);
    }
}
