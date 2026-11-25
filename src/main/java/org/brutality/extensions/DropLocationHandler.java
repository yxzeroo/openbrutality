package org.brutality.extensions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public final class DropLocationHandler {
    private static final Pattern pattern = Pattern.compile("(?i)\\bmid\\b");

    private static void handleJsonObject2(JsonObject json) {
        for (Entry entry : new ArrayList<>(json.entrySet())) {
            String str = pattern.matcher((CharSequence)entry.getKey()).replaceAll("PIT");
            if (!str.equals(entry.getKey())) {
                if (!json.has(str)) {
                    json.add(str, (JsonElement)entry.getValue());
                }

                json.remove((String)entry.getKey());
            }

            JsonElement element = json.get(str);
            if (element.isJsonObject()) {
                handleJsonObject2(element.getAsJsonObject());
            } else if (("Drop Location".equals(str) || "Streak Mode".equals(str)) && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                json.addProperty(str, pattern.matcher(element.getAsString()).replaceAll("PIT"));
            }
        }
    }

    private DropLocationHandler() {
    }

    public static void handleJsonObject(JsonObject json) {
        for (Entry entry : new ArrayList<>(json.entrySet())) {
            if (((JsonElement)entry.getValue()).isJsonObject()) {
                JsonObject asJsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
                if (asJsonObject.has("settings") && asJsonObject.get("settings").isJsonObject()) {
                    JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");
                    handleJsonObject2(asJsonObject2);
                    if ("Displace".equalsIgnoreCase((String)entry.getKey())) {
                        handleJsonObject3(asJsonObject2);
                    }
                }
            }
        }
    }

    private static void handleJsonObject3(JsonObject json) {
        if (json.has("Only If Not Looking")) {
            if (!json.has("Only Displace If Not Looking")) {
                json.add("Only Displace If Not Looking", json.get("Only If Not Looking"));
            }

            json.remove("Only If Not Looking");
        }

        for (Entry entry : new ArrayList<>(json.entrySet())) {
            if (((JsonElement)entry.getValue()).isJsonObject()) {
                handleJsonObject3(((JsonElement)entry.getValue()).getAsJsonObject());
            }
        }
    }
}
