package org.brutality.extensions;

import com.google.gson.JsonObject;
import org.brutality.utils.render.RenderHelper3;

public final class WatermarkHandler {
    private static boolean matchesJsonObject(JsonObject json, String str, boolean flag) {
        try {
            return json.has(str) ? json.get(str).getAsBoolean() : flag;
        } catch (RuntimeException e) {
            return flag;
        }
    }

    public static void handleJsonObject(JsonObject json) {
        if (!json.has("Watermark") && json.has("TabGUI") && json.get("TabGUI").isJsonObject()) {
            JsonObject asJsonObject = json.getAsJsonObject("TabGUI");
            if (asJsonObject.has("settings") && asJsonObject.get("settings").isJsonObject()) {
                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");
                JsonObject json2 = new JsonObject();
                JsonObject json3 = new JsonObject();
                boolean flag = matchesJsonObject(asJsonObject2, "Watermark", true);
                json2.addProperty("enabled", matchesJsonObject(asJsonObject, "enabled", false) && flag);
                json2.addProperty("key", 0);
                json2.addProperty("hidden", false);

                for (String str : new String[]{"Font", "Custom Font", "Scale", "Color", "X", "Y", "Glow"}) {
                    if (asJsonObject2.has(str)) {
                        json3.add(str, RenderHelper3.computeJsonElement(asJsonObject2.get(str)));
                    }
                }

                json2.add("settings", json3);
                json.add("Watermark", json2);
                asJsonObject2.remove("Watermark");
            }
        }
    }

    private WatermarkHandler() {
    }
}
