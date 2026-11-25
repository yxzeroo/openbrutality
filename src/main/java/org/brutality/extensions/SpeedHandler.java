package org.brutality.extensions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.brutality.utils.render.RenderHelper3;

public final class SpeedHandler {
    public static void handleJsonObject(JsonObject json) {
        JsonElement element = json.remove("45 Jump");
        if (element != null && element.isJsonObject()) {
            JsonObject asJsonObject = element.getAsJsonObject();
            JsonObject json2 = json.has("Speed") && json.get("Speed").isJsonObject() ? json.getAsJsonObject("Speed") : new JsonObject();
            JsonObject json3 = json2.has("settings") && json2.get("settings").isJsonObject() ? json2.getAsJsonObject("settings") : new JsonObject();
            if (asJsonObject.has("settings") && asJsonObject.get("settings").isJsonObject()) {
                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");

                for (String str : new String[]{"Flying", "Holding blocks", "Holding bow", "Holding throwable", "Holding rod", "In liquid", "Targeting"}) {
                    if (!json3.has(str) && asJsonObject2.has(str)) {
                        json3.add(str, RenderHelper3.computeJsonElement(asJsonObject2.get(str)));
                    }
                }
            }

            if ((!json2.has("enabled") || !json2.get("enabled").getAsBoolean()) && asJsonObject.has("enabled") && asJsonObject.get("enabled").getAsBoolean()) {
                json2.addProperty("enabled", true);
                json3.addProperty("Mode", "Watchdog Sim");
                if (asJsonObject.has("key")) {
                    json2.add("key", RenderHelper3.computeJsonElement(asJsonObject.get("key")));
                }
            }

            json2.add("settings", json3);
            json.add("Speed", json2);
        }
    }

    private SpeedHandler() {
    }
}
