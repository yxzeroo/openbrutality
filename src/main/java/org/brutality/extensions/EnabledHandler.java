package org.brutality.extensions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map.Entry;
import org.brutality.utils.render.RenderHelper3;

public final class EnabledHandler {
    private static void handleJsonObject(JsonObject json, String str, String str2, String str3, boolean flag) {
        JsonObject json2 = computeJsonObject(json.get(str2));
        JsonObject json3 = computeJsonObject(json.get(str3));
        if (!json.has(str) && (json2 != null || json3 != null)) {
            boolean flag2 = matchesJsonObject(json2);
            boolean flag3 = matchesJsonObject(json3);
            JsonObject json4 = !flag2 && json3 != null ? json3 : json2;
            if (json4 == null) {
                json4 = json2;
            }

            JsonObject asJsonObject = RenderHelper3.computeJsonElement(json4).getAsJsonObject();
            asJsonObject.addProperty("enabled", flag2 || flag3);
            JsonObject json5 = new JsonObject();
            JsonObject json6 = json2 == null ? null : computeJsonObject(json2.get("settings"));
            if (json6 != null) {
                for (Entry entry : json6.entrySet()) {
                    json5.add((String)entry.getKey(), RenderHelper3.computeJsonElement((JsonElement)entry.getValue()));
                }
            }

            JsonObject json7 = json3 == null ? null : computeJsonObject(json3.get("settings"));
            if (json7 != null) {
                for (Entry entry2 : json7.entrySet()) {
                    String key = (String)entry2.getKey();
                    if (flag) {
                        if (key.equals("Cake")) {
                            key = "ESP Cakes";
                        } else if (key.equals("Cherries")) {
                            key = "ESP Cherries";
                        } else if (key.equals("Choc Chips")) {
                            key = "ESP Choc Chips";
                        }
                    }

                    json5.add(key, RenderHelper3.computeJsonElement((JsonElement)entry2.getValue()));
                }
            }

            json5.addProperty("Aura", flag2 || !flag3);
            json5.addProperty("ESP", flag3 || !flag2);
            asJsonObject.add("settings", json5);
            json.add(str, asJsonObject);
        }

        json.remove(str2);
        json.remove(str3);
    }

    private EnabledHandler() {
    }

    public static void handleJsonObject(JsonObject json) {
        JsonElement element = json.remove("RobberyESP");
        if (!json.has("Robbery") && element != null) {
            json.add("Robbery", element);
        }

        JsonElement element2 = json.remove("AutoAuction");
        if (!json.has("Auction") && element2 != null) {
            json.add("Auction", element2);
        }

        handleJsonObject(json, "Cake", "CakeAura", "CakeESP", true);
        handleJsonObject(json, "Dragon Egg", "DragonEggAura", "DragonEggESP", false);
    }

    private static JsonObject computeJsonObject(JsonElement element) {
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    private static boolean matchesJsonObject(JsonObject json) {
        JsonElement element = json == null ? null : json.get("enabled");
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean() && element.getAsBoolean();
    }
}
