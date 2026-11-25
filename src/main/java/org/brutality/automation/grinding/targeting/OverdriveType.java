package org.brutality.automation.grinding.targeting;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.StringUtils;

public enum OverdriveType {
    OVERDRIVE("Overdrive", "OVRDRV"),
    HIGHLANDER("Highlander", "HIGH"),
    BEASTMODE("Beastmode", "BEAST"),
    HERMIT("Hermit", "HERMIT"),
    MOON("To the Moon", "MOON"),
    UBER("Uberstreak", "UBER");

    private static final Pattern PREFIX = Pattern.compile("^\\[?(OVRDRV|HIGH|BEAST|HERMIT|MOON|UBER(?:100|200|300|400)?)\\]?\\s+$");
    private final String label;
    private final String tag;

    private OverdriveType(String label, String tag) {
        this.label = label;
        this.tag = tag;
    }

    public static OverdriveType computeOverdriveType(String str, String str2) {
        if (str != null && str2 != null && !str2.isEmpty()) {
            String str3 = StringUtils.stripControlCodes(str).trim().toUpperCase(Locale.ROOT);
            int n = str3.lastIndexOf(str2.toUpperCase(Locale.ROOT));
            if (n <= 0) {
                return null;
            }
            Matcher matcher = PREFIX.matcher(str3.substring(0, n));
            if (!matcher.matches()) {
                return null;
            }
            String str4 = matcher.group(1);

            for (OverdriveType overdriveType : values()) {
                if (str4.equals(overdriveType.tag) || overdriveType == UBER && str4.startsWith("UBER")) {
                    return overdriveType;
                }
            }

            return null;
        }
        return null;
    }

    public String getLabel() {
        return this.label;
    }

    private static OverdriveType[] computeOverdriveTypeArray() {
        return new OverdriveType[]{OVERDRIVE, HIGHLANDER, BEASTMODE, HERMIT, MOON, UBER};
    }
}
