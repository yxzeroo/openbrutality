package org.brutality.extensions;

import com.google.gson.JsonObject;

public final class KeepSprintHandler {
    public static void handleJsonObject(JsonObject json) {
        if (json.has("KeepSprint") && json.get("KeepSprint").isJsonObject()) {
            JsonObject asJsonObject = json.getAsJsonObject("KeepSprint");
            if (asJsonObject.has("settings") && asJsonObject.get("settings").isJsonObject()) {
                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");
                if (asJsonObject2.has("Mode") && asJsonObject2.get("Mode").isJsonPrimitive() && "Attack Window".equalsIgnoreCase(asJsonObject2.get("Mode").getAsString())) {
                    asJsonObject2.addProperty("Mode", "Watchdog 3");
                }
            }
        }
    }

    private KeepSprintHandler() {
    }
}
