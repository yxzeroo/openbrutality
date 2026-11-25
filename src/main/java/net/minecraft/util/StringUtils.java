package net.minecraft.util;

import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    private static final Pattern colorPatternCodes = Pattern.compile("(?i)\\u00A7[\\dA-F]");

    public static String ticksToElapsedTime(int ticks) {
        int n = ticks / 20;
        int n2 = n / 60;
        n %= 60;
        return n < 10 ? n2 + ":0" + n : n2 + ":" + n;
    }

    public static String stripControlCodes(String p_76338_0_) {
        return patternControlCode.matcher(p_76338_0_).replaceAll("");
    }

    public static boolean isNullOrEmpty(String string) {
        return org.apache.commons.lang3.StringUtils.isEmpty(string);
    }

    public static String stripColorCodes(String text) {
        return colorPatternCodes.matcher(text).replaceAll("");
    }
}
