package org.brutality.utils.hypixel.pit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.brutality.util.ExecuteCommandHelper;

public final class ActiveEffectLinesHelper {
    private static final Pattern pattern = Pattern.compile("^(?:event\\s*:\\s*|major event!?\\s*:?\\s*)?blockhead\\b", 2);
    private static final Pattern pattern2 = Pattern.compile("remaining\\s*:\\s*(\\d+):(\\d{2})", 2);
    private static final Pattern pattern3 = Pattern.compile("^block\\s*:\\s*(.+)$", 2);
    private static final Pattern pattern4 = Pattern.compile("#\\s*(\\d+)\\s*,?\\s*([\\d,]+)\\s+blocks?(?:\\s*\\(([\\d.]+)%\\))?", 2);
    private static final Pattern pattern5 = Pattern.compile("^kills\\s*:\\s*(\\d+)", 2);
    private static final Pattern pattern6 = Pattern.compile("^BLOCKHEAD! You are the (.+) block!$", 2);
    private static final Pattern pattern7 = Pattern.compile("^Respawn\\s*:\\s*(\\d+)\\s*s$", 2);
    private static final Pattern pattern8 = Pattern.compile("^POWERUP! Picked up (Combat Boost|Quicktrail) powerup!$", 2);
    public boolean active;
    public String text = "Unknown";
    public String text2 = "";
    public int count;
    public int count2 = -1;
    public int count3;
    private long timestamp = -1L;
    private long timestamp2;
    private String text3 = "";
    private String text4;
    private long timestamp3;
    private final Map<String, Long> lookupMap = new LinkedHashMap<>();

    public String computeString(long l) {
        if (this.timestamp2 == 0L) {
            return "--:--";
        }
        long l2 = Math.max(0L, (this.timestamp2 - l + 999L) / 1000L);
        return String.format(Locale.ROOT, "%02d:%02d", l2 / 60L, l2 % 60L);
    }

    private static int computeInt2(String str) {
        try {
            return Integer.parseInt(str.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public List<String> activeEffectLines(long l) {
        this.lookupMap.entrySet().removeIf(entry -> entry.getValue() <= l);
        ArrayList list = new ArrayList();

        for (Map.Entry entry2 : this.lookupMap.entrySet()) {
            list.add(
                (((String)entry2.getKey()).equals("Combat Boost") ? "§c" : "§e")
                    + (String)entry2.getKey()
                    + ": §f"
                    + ((Long)entry2.getValue() - l + 999L) / 1000L
                    + "s"
            );
        }

        return list;
    }

    public static int computeInt(String str) {
        Matcher matcher = pattern7.matcher(computeString2(str));
        return matcher.matches() ? Math.min(3600, computeInt2(matcher.group(1))) : -1;
    }

    public void reset() {
        this.active = false;
        this.text = "Unknown";
        this.text2 = "";
        this.count = this.count3 = 0;
        this.count2 = -1;
        this.timestamp2 = 0L;
        this.timestamp = -1L;
        this.text3 = "";
        this.text4 = null;
        this.timestamp3 = 0L;
        this.lookupMap.clear();
    }

    public static String computeString(String str) {
        String str2 = computeString2(str);

        for (String str3 : Arrays.asList("Super Heal", "Diamond Armor", "Quicktrail", "Combat Boost")) {
            if (str3.equalsIgnoreCase(str2)) {
                return str3;
            }
        }

        return null;
    }

    public static String computeString2(String str) {
        return str == null ? "" : str.replaceAll("(?i)§[0-9a-fk-or]", "").replaceAll("[^\\x20-\\x7E]", "").trim();
    }

    public void handleString(String str, long timestamp) {
        String str2 = computeString2(str);
        if (str2.matches("(?i)^MAJOR EVENT!\\s*BLOCKHEAD\\s+starting now[.!]?$")) {
            this.reset();
            this.active = true;
            this.timestamp = timestamp;
        } else {
            Matcher matcher = pattern6.matcher(str2);
            if (matcher.matches()) {
                if (!this.active) {
                    this.reset();
                }

                this.active = true;
                this.timestamp = timestamp;
                this.text = matcher.group(1);
            } else if (this.active) {
                Matcher matcher2 = pattern8.matcher(str2);
                if (matcher2.matches()) {
                    String str3 = computeString(matcher2.group(1));
                    this.lookupMap.put(str3, timestamp + (str3.equals("Combat Boost") ? 30000L : 20000L));
                } else if (ExecuteCommandHelper.matchesString(str2)) {
                    if (!str2.equals(this.text4) || timestamp - this.timestamp3 >= 100L) {
                        this.text4 = str2;
                        this.timestamp3 = timestamp;
                        this.count++;
                    }
                }
            }
        }
    }

    public void handleList(List<String> list, long timestamp) {
        boolean flag = false;
        boolean flag2 = false;

        for (String str : list) {
            String str2 = computeString2(str);
            if (pattern.matcher(str2).find()) {
                flag = true;
            }

            if (str2.toLowerCase(Locale.ROOT).startsWith("event:") && !pattern.matcher(str2).find()) {
                flag2 = true;
            }
        }

        if (flag) {
            if (!this.active) {
                this.reset();
                this.active = true;
            }

            this.timestamp = timestamp;

            for (String str3 : list) {
                String str4 = computeString2(str3);
                Matcher matcher = pattern2.matcher(str4);
                if (matcher.find() && !matcher.group().equals(this.text3)) {
                    this.text3 = matcher.group();
                    this.timestamp2 = timestamp + (Integer.parseInt(matcher.group(1)) * 60L + Integer.parseInt(matcher.group(2))) * 1000L;
                }

                matcher = pattern3.matcher(str4);
                if (matcher.find()) {
                    this.text = matcher.group(1).trim();
                }

                matcher = pattern4.matcher(str4);
                if (matcher.find()) {
                    this.count3 = computeInt2(matcher.group(1));
                    this.count2 = computeInt2(matcher.group(2));
                    this.text2 = matcher.group(3) == null ? "" : matcher.group(3) + "%";
                }

                matcher = pattern5.matcher(str4);
                if (matcher.find()) {
                    this.count = computeInt2(matcher.group(1));
                }
            }
        } else {
            if (flag2 || this.timestamp < 0L || timestamp - this.timestamp > 1500L) {
                this.reset();
            }
        }
    }

    public static boolean matchesString(String str) {
        return computeString2(str).equalsIgnoreCase("PICKUP!");
    }

    public static final class Entry {
        public final String text;
        public boolean available;
        public long timestamp;
        public long timestamp2;
        private int count = -1;

        public String computeString(long l) {
            return this.text + (this.available ? " §aAvailable" : " §cClaimed §7(§e" + Math.max(0L, (this.timestamp - l + 999L) / 1000L) + "s§7)");
        }

        public Entry(String text) {
            this.text = text;
        }

        public void handleString(String str, long l) {
            int n = ActiveEffectLinesHelper.computeInt(str);
            if (ActiveEffectLinesHelper.matchesString(str)) {
                this.available = true;
                this.timestamp = 0L;
                this.count = -1;
                this.timestamp2 = l;
            } else if (n >= 0) {
                if (this.available || n != this.count) {
                    this.timestamp = l + n * 1000L;
                }

                this.available = false;
                this.count = n;
                this.timestamp2 = l;
            }
        }
    }
}
