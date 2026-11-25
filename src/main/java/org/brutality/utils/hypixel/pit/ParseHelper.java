package org.brutality.utils.hypixel.pit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ParseHelper {
    private static final long timestamp = 1000L;

    public static ParseHelper.Entry parse(JsonObject json) {
        LinkedHashMap map = new LinkedHashMap();
        ParseHelper.Entry2 entry2 = null;
        if (json != null
            && json.has("success")
            && json.get("success").isJsonPrimitive()
            && json.get("success").getAsBoolean()
            && json.has("items")
            && json.get("items").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("items")) {
                if (element.isJsonObject()) {
                    JsonObject asJsonObject = element.getAsJsonObject();
                    handleMap(map, computeEntry2(asJsonObject, "lastseen"));
                    if (asJsonObject.has("history") && asJsonObject.get("history").isJsonArray()) {
                        for (JsonElement element2 : asJsonObject.getAsJsonArray("history")) {
                            if (element2.isJsonObject()) {
                                handleMap(map, computeEntry2(element2.getAsJsonObject(), "timestamp"));
                            }
                        }
                    }

                    if (asJsonObject.has("original") && asJsonObject.get("original").isJsonObject()) {
                        ParseHelper.Entry2 entry22 = computeEntry2(asJsonObject.getAsJsonObject("original"), "timestamp");
                        if (entry22 != null && (entry2 == null || entry22.timestamp > 0L && (entry2.timestamp == 0L || entry22.timestamp < entry2.timestamp))) {
                            entry2 = entry22;
                        }
                    }
                }
            }
        }

        ArrayList list = new ArrayList(map.values());
        list.sort(Comparator.<ParseHelper.Entry2>comparingLong(entry23 -> entry23.timestamp).reversed());
        return new ParseHelper.Entry(list, entry2);
    }

    private static void handleMap(Map<String, ParseHelper.Entry2> map, ParseHelper.Entry2 entry2) {
        if (entry2 != null) {
            map.putIfAbsent(entry2.text + ":" + entry2.timestamp, entry2);
        }
    }

    private static ParseHelper.Entry2 computeEntry2(JsonObject json, String str) {
        if (json.has("owner") && json.get("owner").isJsonPrimitive() && json.get("owner").getAsJsonPrimitive().isString()) {
            String str2 = json.get("owner").getAsString().replace("-", "").toLowerCase(Locale.ROOT);
            if (!str2.matches("[0-9a-f]{32}")) {
                return null;
            }
            long l = 0L;
            if (json.has(str) && json.get(str).isJsonPrimitive()) {
                try {
                    long asLong = json.get(str).getAsLong();
                    if (asLong > 0L) {
                        l = Math.multiplyExact(asLong, timestamp);
                    }
                } catch (ArithmeticException | NumberFormatException ignored) {
                }
            }

            return new ParseHelper.Entry2(str2, l);
        }
        return null;
    }

    private ParseHelper() {
    }

    public static final class Entry {
        public final List<ParseHelper.Entry2> entries;
        public final ParseHelper.Entry2 entry2;

        public Entry(List<ParseHelper.Entry2> entry2s, ParseHelper.Entry2 entry2) {
            this.entries = Collections.unmodifiableList(entry2s);
            this.entry2 = entry2;
        }
    }

    public static final class Entry2 {
        public final String text;
        public final long timestamp;

        public Entry2(String text, long timestamp) {
            this.text = text;
            this.timestamp = timestamp;
        }
    }
}
