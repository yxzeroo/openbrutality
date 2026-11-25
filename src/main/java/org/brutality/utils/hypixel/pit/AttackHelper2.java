package org.brutality.utils.hypixel.pit;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AttackHelper2 {
    public static final long timestamp = 1500L;
    private static final Pattern pattern = Pattern.compile("^([A-Za-z0-9_]{1,16})\\s+[^\\s]*[❤♥][^\\s]*\\s+[0-9]+(?:\\.[0-9]+)?HP$", 2);
    private static final Pattern pattern2 = Pattern.compile("^([A-Za-z0-9_]{1,16})\\s+KILL!$", 2);
    private final Map<String, Long> lookupMap = new HashMap<>();
    private long timestamp2 = Long.MIN_VALUE;

    public String computeString(byte b, String str, long l) {
        if (b != 2) {
            return null;
        }
        String str2 = computeString(str);
        if (str2 == null) {
            return null;
        }
        if (this.timestamp2 != Long.MIN_VALUE && l - this.timestamp2 <= 1500L) {
            this.timestamp2 = Long.MIN_VALUE;
            this.lookupMap.remove(str2.toLowerCase(Locale.ROOT));
            return null;
        }
        Long val = this.lookupMap.remove(str2.toLowerCase(Locale.ROOT));
        return val != null && l >= val && l - val <= 1500L ? str2 : null;
    }

    public void attack(String str, long l) {
        this.lookupMap.entrySet().removeIf(entry -> l - entry.getValue() > 1500L);
        this.lookupMap.put(str.toLowerCase(Locale.ROOT), l);
    }

    public void clear() {
        this.lookupMap.clear();
        this.timestamp2 = Long.MIN_VALUE;
    }

    public void setTimestamp2(long l) {
        this.timestamp2 = l;
    }

    public static String computeString(String str) {
        if (str == null) {
            return null;
        }
        String str2 = str.replaceAll("§[0-9A-FK-ORa-fk-or]", "").trim();
        Matcher matcher = pattern.matcher(str2);
        if (!matcher.matches()) {
            matcher = pattern2.matcher(str2);
        }

        return matcher.matches() ? matcher.group(1) : null;
    }
}
