package org.brutality.utils.hypixel.pit;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DiedWhileVenomedHelper {
    private static final Pattern pattern = Pattern.compile(
        "\\b([A-Za-z0-9_]{3,16})\\s*(?:[-:|]\\s*)?(?:(?:is|was|got|became|currently)\\s+)?(?:now\\s+)?venomed\\b", 2
    );
    private static final Pattern pattern2 = Pattern.compile("\\bvenomed\\s*(?:[:,-]\\s*)?([A-Za-z0-9_]{3,16})\\b", 2);
    private static final Pattern pattern3 = Pattern.compile(
        "\\bX\\s*[:=]\\s*(-?\\d+(?:\\.\\d+)?)\\s*[,;]?\\s*Y\\s*[:=]\\s*(-?\\d+(?:\\.\\d+)?)\\s*[,;]?\\s*Z\\s*[:=]\\s*(-?\\d+(?:\\.\\d+)?)\\b", 2
    );

    public static DiedWhileVenomedHelper.Entry computeEntry(String str) {
        if (str != null && !str.isEmpty()) {
            String str2 = str.replaceAll("(?i)§[0-9a-fk-or]", "");
            Matcher matcher = pattern3.matcher(str2);
            if (!matcher.find()) {
                return null;
            }
            Matcher matcher2 = pattern.matcher(str2);
            boolean flag = matcher2.find();
            if (!flag) {
                matcher2 = pattern2.matcher(str2);
                flag = matcher2.find();
            }

            if (!flag) {
                return null;
            }
            try {
                double d = Double.parseDouble(matcher.group(1));
                double d2 = Double.parseDouble(matcher.group(2));
                double d3 = Double.parseDouble(matcher.group(3));
                return Double.isFinite(d)
                        && Double.isFinite(d2)
                        && Double.isFinite(d3)
                        && !(Math.abs(d) > 30000.0)
                        && !(d2 < -64.0)
                        && !(d2 > 512.0)
                        && !(Math.abs(d3) > 30000.0)
                    ? new DiedWhileVenomedHelper.Entry(matcher2.group(1), d, d2, d3)
                    : null;
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private DiedWhileVenomedHelper() {
    }

    public static String computeString(String str) {
        if (str == null) {
            return null;
        }
        String str2 = str.replaceAll("(?i)§[0-9a-fk-or]", "");
        if (str2.toLowerCase(Locale.ROOT).contains("died while venomed")) {
            return null;
        }
        Matcher matcher = pattern.matcher(str2);
        if (matcher.find()) {
            return matcher.group(1);
        }
        matcher = pattern2.matcher(str2);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static final class Entry {
        public final String text;
        public final double amount;
        public final double amount2;
        public final double amount3;

        public Entry(String text, double amount, double amount2, double amount3) {
            this.text = text;
            this.amount = amount;
            this.amount2 = amount2;
            this.amount3 = amount3;
        }
    }
}
