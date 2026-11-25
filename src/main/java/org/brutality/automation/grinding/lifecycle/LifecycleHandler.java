package org.brutality.automation.grinding.lifecycle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LifecycleHandler {
    private static final Pattern pattern = Pattern.compile("^Streak:\\s*([0-9,]+)\\s*$", 2);
    private int count;

    public void handleString(String str) {
        if (str != null) {
            Matcher matcher = pattern.matcher(str.replaceAll("(?i)§[0-9a-fk-or]", "").trim());
            if (matcher.matches()) {
                try {
                    this.count = Math.max(this.count, Integer.parseInt(matcher.group(1).replace(",", "")));
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    public boolean matchesFlag(boolean flag, int n) {
        return flag && this.count >= n;
    }

    public void reset() {
        this.count = 0;
    }

    public int getCount() {
        return this.count;
    }

    public static boolean matchesString(String str) {
        return str != null && str.replaceAll("(?i)§[0-9a-fk-or]", "").trim().startsWith("DEATH!");
    }
}
