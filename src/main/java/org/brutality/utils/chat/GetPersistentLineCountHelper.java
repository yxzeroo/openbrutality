package org.brutality.utils.chat;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.ChatLine;

public class GetPersistentLineCountHelper {
    private static final List<ChatLine> entries = Lists.newArrayList();
    private static final List<ChatLine> entries2 = Lists.newArrayList();
    private static final int count = 10000;
    private static boolean enabled = true;

    public static void handleChatLine(ChatLine chatLine) {
        if (enabled) {
            entries2.add(0, chatLine);

            while (entries2.size() > 10000) {
                entries2.remove(entries2.size() - 1);
            }
        }
    }

    public static List<ChatLine> computeList2() {
        return new ArrayList<>(entries);
    }

    public static List<ChatLine> computeList() {
        return new ArrayList<>(entries2);
    }

    public static int getPersistentLineCount() {
        return entries2.size();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled2) {
        enabled = enabled2;
        if (!enabled2) {
            entries.clear();
            entries2.clear();
        }
    }

    public static void process() {
        entries.clear();
        entries2.clear();
    }

    public static void handleChatLine2(ChatLine chatLine) {
        if (enabled) {
            entries.add(0, chatLine);

            while (entries.size() > 10000) {
                entries.remove(entries.size() - 1);
            }
        }
    }
}
