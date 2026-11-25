package org.brutality.system.prestige;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.brutality.utils.render.ResetColorHelper;

public final class PrestigeChatParser {
    private static final Pattern pattern = Pattern.compile("(?i)PRESTIGE!\\s+([A-Za-z0-9_]{2,16})\\s+unlocked\\s+prestige\\s+([IVXLCDM]+|\\d+)\\s*,?\\s*gg!?");

    private PrestigeChatParser() {
    }

    public static PrestigeChatParser.Entry computeEntry(String message) {
        if (message != null && !message.isEmpty()) {
            String str = ResetColorHelper.stripColor(message).trim();
            Matcher matcher = pattern.matcher(str);
            if (!matcher.find()) {
                return null;
            }
            String str2 = matcher.group(1);
            String str3 = matcher.group(2);
            int n = PrestigeLevelFormat.computeInt(str3);
            return n < 0 ? null : new PrestigeChatParser.Entry(str2, n, str);
        }
        return null;
    }

    public static final class Entry {
        public final String text;
        public final int count;
        public final String text2;

        public Entry(String text, int count, String str) {
            this.text = text;
            this.count = count;
            this.text2 = str;
        }
    }
}
