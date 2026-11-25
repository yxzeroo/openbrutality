package org.brutality.utils.hypixel.pit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Locale;
import java.util.Set;
import java.util.Map.Entry;

public final class NormalizeHelper {
    private static final Set<String> itemSet = Set.of(
        "doc", "profile", "stats", "pitstats", "prestiges", "renownshop", "renownunlocks", "renownupgrades", "unlocks", "purchasedunlocks"
    );
    private static final String[] stringArray = new String[]{"purchased", "unlocked", "owned"};
    private static final String[] stringArray2 = new String[]{"tier", "level", "count"};

    private static String normalize(String str) {
        return str.toLowerCase(Locale.ROOT).replace("_", "").replace("-", "").replace(" ", "");
    }

    public static String computeString(JsonObject json) {
        if (json != null && json.has("displays") && json.get("displays").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("displays")) {
                if (element.isJsonObject()) {
                    JsonObject asJsonObject = element.getAsJsonObject();
                    if (asJsonObject.has("type") && !asJsonObject.get("type").isJsonNull() && "scammer".equalsIgnoreCase(asJsonObject.get("type").getAsString().trim())) {
                        return asJsonObject.has("notes") && !asJsonObject.get("notes").isJsonNull() ? asJsonObject.get("notes").getAsString() : "Scammer tagged";
                    }
                }
            }

            return null;
        }
        return null;
    }

    private static boolean matchesJsonElement(JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            if (element.isJsonArray()) {
                for (JsonElement element2 : element.getAsJsonArray()) {
                    if (matchesJsonElement(element2)) {
                        return true;
                    }
                }
            } else if (element.isJsonObject()) {
                JsonObject asJsonObject = element.getAsJsonObject();

                for (String str : new String[]{"key", "id", "upgrade", "name"}) {
                    JsonElement element3 = asJsonObject.get(str);
                    if (element3 != null && element3.isJsonPrimitive() && matchesString(normalize(element3.getAsString()))) {
                        return matchesJsonObject(asJsonObject, "key".equals(str));
                    }
                }

                for (Entry entry : asJsonObject.entrySet()) {
                    String str2 = normalize((String)entry.getKey());
                    JsonElement val = (JsonElement)entry.getValue();
                    if (matchesString(str2)) {
                        if (matchesJsonElement2(val)) {
                            return true;
                        }

                        if (val.isJsonObject() && matchesJsonObject(val.getAsJsonObject(), false)) {
                            return true;
                        }
                    }

                    if (itemSet.contains(str2) && matchesJsonElement(val)) {
                        return true;
                    }
                }
            }

            return false;
        }
        return false;
    }

    private NormalizeHelper() {
    }

    private static boolean matchesJsonObject(JsonObject json, boolean flag) {
        boolean flag2 = false;

        for (String str : stringArray) {
            if (json.has(str)) {
                if (!matchesJsonElement2(json.get(str))) {
                    return false;
                }

                flag2 = true;
            }
        }

        if (flag2) {
            return true;
        }
        for (String str2 : stringArray2) {
            if (json.has(str2)) {
                return matchesJsonElement2(json.get(str2));
            }
        }

        return flag;
    }

    private static boolean matchesString(String str) {
        return str.equals("heresy") || str.matches("heresy(?:[1-9][0-9]*|i{1,3}|iv|v)");
    }

    public static boolean matchesJsonObject(JsonObject json) {
        return json == null ? false : matchesJsonElement(json);
    }

    private static boolean matchesJsonElement2(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            try {
                return element.getAsDouble() > 0.0;
            } catch (RuntimeException e) {
                return "true".equalsIgnoreCase(element.getAsString());
            }
        } else {
            return false;
        }
    }
}
