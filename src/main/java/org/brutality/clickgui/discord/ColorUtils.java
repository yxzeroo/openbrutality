package org.brutality.clickgui.discord;

import java.awt.Color;

public final class ColorUtils {
    public static final Color color = new Color(10, 10, 10, 210);
    public static final Color color2 = new Color(10, 10, 10, 235);
    public static final Color color3 = new Color(14, 14, 14, 240);
    public static final Color color4 = new Color(18, 18, 18, 255);
    public static final Color color5 = new Color(24, 24, 24, 255);
    public static final Color color6 = new Color(8, 8, 8, 255);
    public static final Color color7 = new Color(16, 16, 16, 255);
    public static final Color color8 = new Color(220, 40, 40, 255);
    public static final Color color9 = new Color(212, 170, 56, 255);
    public static final Color color10 = new Color(255, 80, 60, 255);
    public static final Color color11 = new Color(139, 0, 0, 255);
    public static final Color color12 = new Color(220, 40, 40, 45);
    public static final Color color13 = new Color(220, 40, 40, 75);
    public static final Color color14 = new Color(245, 245, 245, 255);
    public static final Color color15 = new Color(160, 160, 160, 230);
    public static final Color color16 = new Color(100, 100, 100, 200);
    public static final Color color17 = new Color(40, 40, 40, 255);
    public static final Color color18 = new Color(220, 40, 40, 180);
    public static final Color color19 = new Color(255, 255, 255, 12);
    public static final Color color20 = new Color(0, 0, 0, 0);
    public static final Color color21 = new Color(80, 210, 130, 255);

    public static Color computeColor(Color color, Color color2, float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        int n = (int)(color.getRed() + (color2.getRed() - color.getRed()) * f);
        int n2 = (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f);
        int n3 = (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f);
        int n4 = (int)(color.getAlpha() + (color2.getAlpha() - color.getAlpha()) * f);
        return new Color(n, n2, n3, n4);
    }

    public static Color computeColor(Color color, float f) {
        int n = (int)(color.getAlpha() * Math.max(0.0F, Math.min(1.0F, f)));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, n)));
    }

    public static Color computeColor(Color color, int n) {
        int n2 = Math.max(0, Math.min(255, n));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), n2);
    }

    private ColorUtils() {
    }
}
