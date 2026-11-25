package org.brutality.utils;

import net.minecraft.client.gui.ScaledResolution;
import org.brutality.utils.performance.InvalidateHelper;

public final class XPosHelper {
    public static final double amount = 1920.0;
    public static final double amount2 = 1080.0;

    private XPosHelper() {
    }

    public static boolean matchesString2(String str, double d, double d2) {
        return d2 == 1.0 && !(d < 500.0)
            ? str.equals("X")
                || str.endsWith(" X")
                || str.contains("X Pos")
                || str.equals("X Position")
                || str.equals("HUD X")
                || str.equals("Position X")
            : false;
    }

    public static double computeDouble(int n, ScaledResolution sr) {
        return sr.getScaledWidth() <= 0 ? n : n * 1920.0 / sr.getScaledWidth();
    }

    public static int computeInt2(double d, ScaledResolution sr) {
        return sr.getScaledHeight() <= 0 ? (int)d : (int)Math.round(d * sr.getScaledHeight() / 1080.0);
    }

    public static int computeInt2(double d) {
        return computeInt2(d, InvalidateHelper.computeScaledResolution());
    }

    public static int computeInt(double d, ScaledResolution sr) {
        return sr.getScaledWidth() <= 0 ? (int)d : (int)Math.round(d * sr.getScaledWidth() / 1920.0);
    }

    public static boolean matchesString(String str, double d, double d2) {
        return d2 == 1.0 && !(d < 500.0)
            ? str.equals("Y")
                || str.endsWith(" Y")
                || str.contains("Y Pos")
                || str.equals("Y Position")
                || str.equals("HUD Y")
                || str.equals("Position Y")
            : false;
    }

    public static int computeInt(double d) {
        return computeInt(d, InvalidateHelper.computeScaledResolution());
    }

    public static double computeDouble2(int n, ScaledResolution sr) {
        return sr.getScaledHeight() <= 0 ? n : n * 1080.0 / sr.getScaledHeight();
    }
}
