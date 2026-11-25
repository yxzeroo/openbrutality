package org.brutality.utils.player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class PlayerHelper5 {
    public static final DecimalFormat decimalFormat = new DecimalFormat("0");
    public static final DecimalFormat decimalFormat2 = new DecimalFormat("0.0");
    public static final DecimalFormat decimalFormat3 = new DecimalFormat("0.00");
    public static final DecimalFormat decimalFormat4 = new DecimalFormat("0.#");
    public static final DecimalFormat decimalFormat5 = new DecimalFormat("0.##");
    public static final SecureRandom secureRandom = new SecureRandom();

    public static float computeFloat3(float f, float f2) {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextFloat() * (f2 - f) + f;
    }

    public static double computeDouble(double d, double d2) {
        return Math.random() * (d - d2) + d2;
    }

    public static int computeInt(int n, int n2, double d) {
        return computeDoubleType(n, n2, (float)d).intValue();
    }

    public static double computeDouble(double d) {
        return Math.round(d * 2.0) / 2.0;
    }

    public static int computeInt2(int n, int n2) {
        return (int)(Math.random() * (n2 - n) + n);
    }

    public static int computeInt(double d) {
        BigDecimal bigDecimal = new BigDecimal(d);
        return Math.max(0, bigDecimal.stripTrailingZeros().scale());
    }

    public static double computeDouble(double d, int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bigDecimal = new BigDecimal(d);
        bigDecimal = bigDecimal.setScale(n, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public static float computeFloat(float f, float f2) {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextFloat() * (f - f2) + f2;
    }

    public static double[] computeDoubleArray(float f, double d) {
        return new double[]{-MathHelper.sinVanilla(f) * d, MathHelper.cosVanilla(f) * d};
    }

    public static Double computeDoubleType(double d, double d2, double d3) {
        return d + (d2 - d) * d3;
    }

    public static int computeInt(int n, int n2) {
        Random random = new Random();
        return random.nextInt(n2 - n) + n;
    }

    public static double[] computeDoubleArray(double d) {
        return computeDoubleArray(Minecraft.getMinecraft().thePlayer.rotationYaw * MathHelper.deg2Rad, d);
    }

    public static double computeDouble2(double d, double d2) {
        SecureRandom secureRandom = new SecureRandom();
        return d == d2 ? d : secureRandom.nextDouble() * (d2 - d) + d;
    }

    public static float computeFloat2(float f, float f2) {
        double d = 1.0 / Math.sqrt((Math.PI * 2) * (f2 * f2));
        return (float)(d * Math.exp(-(f * f) / (2.0 * (f2 * f2))));
    }

    public static String computeString(String str, int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bigDecimal = new BigDecimal(str);
        bigDecimal = bigDecimal.stripTrailingZeros();
        bigDecimal = bigDecimal.setScale(n, RoundingMode.HALF_UP);
        return bigDecimal.toString();
    }

    public static double lerp(double d, double d2, double d3) {
        return (1.0 - d3) * d + d3 * d2;
    }

    public static double computeDouble4(double d, double d2) {
        double d3 = 1.0 / d2;
        return Math.round(d * d3) / d3;
    }

    public static float computeFloat(float f, float f2, double d) {
        return computeDoubleType(f, f2, (float)d).floatValue();
    }

    public static double computeDouble5(double d, double d2) {
        return Math.random() * (d - d2) + d2;
    }

    public static double computeDouble3(double d, double d2) {
        BigDecimal bigDecimal = new BigDecimal(d);
        bigDecimal = bigDecimal.setScale((int)d2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
