package org.brutality.extensions;

import com.google.gson.JsonObject;

public final class ScaffoldHandler {
    public static void handleJsonObject(JsonObject json) {
        if (json.has("Scaffold") && json.get("Scaffold").isJsonObject()) {
            JsonObject asJsonObject = json.getAsJsonObject("Scaffold");
            if (asJsonObject.has("settings") && asJsonObject.get("settings").isJsonObject()) {
                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");
                if (asJsonObject2.has("Mode") && asJsonObject2.get("Mode").isJsonPrimitive() && "Smooth".equalsIgnoreCase(asJsonObject2.get("Mode").getAsString())) {
                    asJsonObject2.addProperty("Mode", "Normal");
                }
            }
        }
    }

    private ScaffoldHandler() {
    }
}
