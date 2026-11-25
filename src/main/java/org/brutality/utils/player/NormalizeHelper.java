package org.brutality.utils.player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.IChatComponent;
import org.brutality.utils.render.ResetColorHelper;

public final class NormalizeHelper {
    private static final Pattern pattern = Pattern.compile("^<([A-Za-z0-9_]{2,16})>");
    private static final Pattern pattern2 = Pattern.compile("^(?:\\[[^\\]]*\\]\\s*)+([A-Za-z0-9_]{2,16}):\\s");
    private static final Pattern pattern3 = Pattern.compile("^(?:\\[[^\\]]*\\]\\s*)*([A-Za-z0-9_]{2,16})\\s*(?:»|>|\\|\\||:)\\s*", 64);
    private static final Pattern pattern4 = Pattern.compile("(?i)^(?:from|to|whisper from)\\s+([A-Za-z0-9_]{2,16})\\s*[:>]");
    private static final Pattern pattern5 = Pattern.compile("^(?:\\[PARTY\\]|\\[P\\])\\s*([A-Za-z0-9_]{2,16})\\s*[:>]");

    public static String computeString(IChatComponent component) {
        if (component == null) {
            return null;
        }
        String str = computeString2(component);
        return computeString(str);
    }

    private NormalizeHelper() {
    }

    private static String normalize(String str) {
        return ResetColorHelper.stripColor(str).trim();
    }

    public static String computeString(String str) {
        if (str != null && !str.isEmpty()) {
            String str2 = normalize(str);
            Matcher matcher = pattern.matcher(str2);
            if (matcher.lookingAt()) {
                return matcher.group(1);
            }
            Matcher matcher2 = pattern3.matcher(str2);
            if (matcher2.lookingAt()) {
                return matcher2.group(1);
            }
            Matcher matcher3 = pattern4.matcher(str2);
            if (matcher3.lookingAt()) {
                return matcher3.group(1);
            }
            Matcher matcher4 = pattern5.matcher(str2);
            if (matcher4.lookingAt()) {
                return matcher4.group(1);
            }
            Matcher matcher5 = pattern2.matcher(str2);
            return matcher5.lookingAt() ? matcher5.group(1) : null;
        }
        return null;
    }

    private static String computeString2(IChatComponent component) {
        StringBuilder sb = new StringBuilder();
        handleIChatComponent(component, sb);
        return sb.toString();
    }

    private static void handleIChatComponent(IChatComponent component, StringBuilder sb) {
        if (component != null) {
            String text = component.getUnformattedText();
            if (text != null) {
                sb.append(text);
            }

            for (IChatComponent sibling : component.getSiblings()) {
                handleIChatComponent(sibling, sb);
            }
        }
    }
}
