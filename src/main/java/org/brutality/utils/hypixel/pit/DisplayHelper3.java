package org.brutality.utils.hypixel.pit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.EnumChatFormatting;

public final class DisplayHelper3 {
    public static final String text = "Night Quest";

    private DisplayHelper3() {
    }

    public static String computeString(long l) {
        long l2 = Math.max(0L, l) / 1000L;
        return String.format(Locale.ROOT, "%02d:%02d", l2 / 60L, l2 % 60L);
    }

    public static List<DisplayHelper3.Entry> display(List<DisplayHelper3.Entry> list, long l, int n, Long val) {
        int n2 = Math.max(1, n);
        List<DisplayHelper3.Entry> list2 = new ArrayList<>();

        for (DisplayHelper3.Entry entry : list) {
            if (entry.timestamp > l && !entry.text.equalsIgnoreCase("Night Quest")) {
                list2.add(entry);
            }
        }

        list2.sort(Comparator.comparingLong(entry2 -> entry2.timestamp));
        int n3 = n2 - (val == null ? 0 : 1);
        if (list2.size() > n3) {
            list2 = new ArrayList(list2.subList(0, n3));
        }

        if (val != null) {
            list2.add(new DisplayHelper3.Entry("Night Quest", l + Math.max(0L, val), "local"));
        }

        list2.sort(Comparator.<DisplayHelper3.Entry>comparingLong(entry3 -> entry3.timestamp).reversed());
        return Collections.unmodifiableList(list2);
    }

    public static List<DisplayHelper3.Entry> parse(String str, long l) {
        JsonElement element = new JsonParser().parse(str);
        JsonArray jsonArray = element.isJsonArray() ? element.getAsJsonArray() : element.getAsJsonObject().getAsJsonArray("events");
        if (jsonArray == null) {
            throw new IllegalArgumentException("Missing event list");
        }
        List<DisplayHelper3.Entry> list = new ArrayList<>();
        HashSet set = new HashSet();

        for (JsonElement element2 : jsonArray) {
            try {
                JsonObject asJsonObject = element2.getAsJsonObject();
                String str2 = asJsonObject.get("event").getAsString().trim();
                long asLong = asJsonObject.get("timestamp").getAsLong();
                String str3 = asJsonObject.has("type") ? asJsonObject.get("type").getAsString() : "unknown";
                if (!str2.isEmpty() && asLong > l && set.add(str2.toLowerCase(Locale.ROOT) + ":" + asLong)) {
                    list.add(new DisplayHelper3.Entry(str2, asLong, str3));
                }
            } catch (RuntimeException ignored) {
            }
        }

        list.sort(Comparator.comparingLong(entry -> entry.timestamp));
        return Collections.unmodifiableList(list);
    }

    public static EnumChatFormatting computeEnumChatFormatting(String str) {
        String str2 = str.toLowerCase(Locale.ROOT);
        switch (str2) {
            case "night quest":
            case "blockhead":
                return EnumChatFormatting.BLUE;
            case "kotl":
            case "beast":
                return EnumChatFormatting.GREEN;
            case "2x rewards":
                return EnumChatFormatting.DARK_GREEN;
            case "auction":
                return EnumChatFormatting.YELLOW;
            case "koth":
                return EnumChatFormatting.AQUA;
            case "care package":
            case "all bounty":
            case "robbery":
            case "raffle":
                return EnumChatFormatting.GOLD;
            case "quick maths":
            case "giant cake":
                return EnumChatFormatting.LIGHT_PURPLE;
            case "dragon egg":
            case "team deathmatch":
            case "spire":
            case "squads":
                return EnumChatFormatting.DARK_PURPLE;
            case "pizza":
            case "rage pit":
                return EnumChatFormatting.RED;
            default:
                return EnumChatFormatting.WHITE;
        }
    }

    public static final class Entry {
        public final String text;
        public final String text2;
        public final long timestamp;

        public Entry(String text, long timestamp, String str) {
            this.text = text;
            this.timestamp = timestamp;
            this.text2 = str;
        }

        public long computeLong(long l) {
            return Math.max(0L, this.timestamp - l);
        }
    }
}
