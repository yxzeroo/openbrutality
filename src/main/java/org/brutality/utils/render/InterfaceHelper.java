package org.brutality.utils.render;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import org.brutality.Brutality;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;

public final class InterfaceHelper {
    public static final List<String> entries = Arrays.asList(
        "Default",
        "Rainbow",
        "Aurora",
        "Cherry",
        "Cotton Candy",
        "Flare",
        "Flower",
        "Forest",
        "Frost",
        "Gold",
        "Grayscale",
        "Inferno",
        "Royal",
        "Sandstorm",
        "Sky",
        "Vine"
    );
    private static final String[] stringArray = new String[]{
        "Rainbow",
        "Aurora",
        "Cherry",
        "Cotton candy",
        "Flare",
        "Flower",
        "Forest",
        "Frost",
        "Gold",
        "Grayscale",
        "Inferno",
        "Royal",
        "Sandstorm",
        "Sky",
        "Vine"
    };
    private static int count = Integer.MIN_VALUE;
    private static String text = "white";

    public static String computeString2(int count2) {
        if (count2 == count) {
            return text;
        }
        count = count2;
        text = computeString(count2);
        return text;
    }

    public static int computeInt2(int n, float f) {
        int n2 = (0xFF | ~(n >> 24)) - ~(n >> 24);
        int n3 = (int)(n2 * Math.max(0.0F, Math.min(1.0F, f)));
        return n3 << 24 | n & 16777215;
    }

    private static String computeString(int n) {
        if (n != 0) {
            return n >= 1 && n < entries.size() ? entries.get(n) : "white";
        }
        try {
            Module module = Brutality.moduleManager.computeModule("Interface");
            if (module != null && Brutality.settingsManager != null) {
                for (Setting setting : Brutality.settingsManager.computeList(module)) {
                    if (setting instanceof NumberSetting && "HUD Theme Color".equals(setting.getName())) {
                        int n2 = (int)((NumberSetting)setting).computeDouble();
                        if (n2 >= 0 && n2 < stringArray.length) {
                            return stringArray[n2];
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return "white";
    }

    public static double computeDouble(long l, long l2) {
        float f = (float)((l + l2) % 5000L) / 5000.0F;
        return f <= 0.5F ? f * 2.0 : 2.0 - f * 2.0;
    }

    public static int computeInt(int n, int n2, double d) {
        int n3 = clamp((int)((n >> 16 & 0xFF) + ((n2 >> 16 & 0xFF) - (n >> 16 & 0xFF)) * d), 0, 255);
        int n4 = clamp((int)((n >> 8 & 0xFF) + ((n2 >> 8 & 0xFF) - (n >> 8 & 0xFF)) * d), 0, 255);
        int n5 = clamp((int)((n & 0xFF) + ((n2 & 0xFF) - (n & 0xFF)) * d), 0, 255);
        return (n4 << 8) + ((0xFF000000 | n3 << 16) & ~(n4 << 8)) | n5;
    }

    public static int computeInt(String str, long l, long l2) {
        String str2 = str == null ? "white" : str.toLowerCase().trim();
        if (str2.equals("rainbow")) {
            float f = (float)((l2 + l) % 5000L) / 5000.0F;
            return Color.HSBtoRGB(f, 1.0F, 1.0F);
        }
        double d = computeDouble(l2, l);
        if (str2.equals("aurora")) {
            return computeInt(-9240126, -15208271, d);
        }
        if (str2.equals("cherry")) {
            return computeInt(-2278039, -2051145, d);
        }
        if (str2.equals("cotton candy")) {
            return computeInt(-7152920, -1218376, d);
        }
        if (str2.equals("flare")) {
            return computeInt(-890090, -1792483, d);
        }
        if (str2.equals("flower")) {
            return computeInt(-3630376, -5482055, d);
        }
        if (str2.equals("forest")) {
            return computeInt(-14715369, -10443229, d);
        }
        if (str2.equals("frost")) {
            return computeInt(-2104349, -4405814, d);
        }
        if (str2.equals("gold")) {
            return computeInt(-1712336, -2434378, d);
        }
        if (str2.equals("grayscale")) {
            return computeInt(-10394776, -1578774, d);
        }
        if (str2.equals("inferno")) {
            return computeInt(-13303808, -4179694, d);
        }
        if (str2.equals("royal")) {
            return computeInt(-8011800, -14860921, d);
        }
        if (str2.equals("sandstorm")) {
            return computeInt(-6450327, -662604, d);
        }
        if (str2.equals("sky")) {
            return computeInt(-8262920, -15352621, d);
        }
        return str2.equals("vine") ? computeInt(-14162887, -6621023, d) : -38374;
    }

    public static int computeInt(int n, float f) {
        if (f >= 1.0F) {
            return n;
        }
        if (f < 0.0F) {
            f = 0.0F;
        }

        int n2 = n >> 24 & 0xFF;
        int n3 = clamp((int)(n2 * f), 0, 255);
        return n3 << 24 | n & 16777215;
    }

    private InterfaceHelper() {
    }

    public static void process() {
        count = Integer.MIN_VALUE;
    }

    private static int clamp(int n, int n2, int n3) {
        return Math.max(n2, Math.min(n3, n));
    }
}
