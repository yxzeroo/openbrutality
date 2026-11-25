package org.brutality.utils.hypixel.pit;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DOUBLEHelper {
    private static final Pattern pattern = Pattern.compile("\\bKILL!\\s+(?:\\(\\d+\\)\\s+)?(?:on\\s+)?([A-Za-z0-9_]{3,16})\\s+\\+", 2);
    private static final Pattern pattern2 = Pattern.compile(
        "\\b([A-Za-z0-9_]{3,16})\\s+(?:(?:DOUBLE|TRIPLE|QUADRA|PENTA|MULTI)(?:\\s*\\(\\d+\\))?\\s+)?KILL!", 2
    );
    private static final Pattern pattern3 = Pattern.compile("\\bASSIST!\\s+\\d+%\\s+on\\s+([A-Za-z0-9_]{3,16})\\s+\\+", 2);
    private static final Pattern pattern4 = Pattern.compile("\\bASSIST!.*?on\\s+([A-Za-z0-9_]{3,16})\\s+\\+", 2);
    private static final Pattern pattern5 = Pattern.compile("\\[\\d+\\]");

    public static String computeString(String str) {
        String str2 = computeString3(str);
        if (str2 == null) {
            return null;
        }
        Matcher matcher = pattern3.matcher(str2);
        if (matcher.find()) {
            return matcher.group(1);
        }
        Matcher matcher2 = pattern4.matcher(str2);
        return matcher2.find() ? matcher2.group(1) : null;
    }

    private static String computeString3(String str) {
        return str == null ? null : pattern5.matcher(str.replaceAll("(?i)§[0-9a-fk-or]", "")).replaceAll(" ").trim();
    }

    private DOUBLEHelper() {
    }

    private static boolean matchesString(String str) {
        String str2 = str.toUpperCase(Locale.ROOT);
        return str2.equals("DOUBLE") || str2.equals("TRIPLE") || str2.equals("QUADRA") || str2.equals("PENTA") || str2.equals("MULTI");
    }

    public static String computeString2(String str) {
        String str2 = computeString3(str);
        if (str2 == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(str2);
        if (matcher.find()) {
            return matcher.group(1);
        }
        Matcher matcher2 = pattern2.matcher(str2);

        while (matcher2.find()) {
            String str3 = matcher2.group(1);
            if (!matchesString(str3)) {
                return str3;
            }
        }

        return null;
    }
}
