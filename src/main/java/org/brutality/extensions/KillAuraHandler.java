package org.brutality.extensions;

import com.google.gson.JsonObject;

public final class KillAuraHandler {
    public static void handleJsonObject(JsonObject json) {
        if (json.has("Kill Aura") && json.get("Kill Aura").isJsonObject()) {
            JsonObject asJsonObject = json.getAsJsonObject("Kill Aura");
            if (asJsonObject.has("settings") && asJsonObject.get("settings").isJsonObject()) {
                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");
                if (asJsonObject2.has("Watchdog Mode")
                    && asJsonObject2.get("Watchdog Mode").isJsonPrimitive()
                    && "10-CPS Sync".equalsIgnoreCase(asJsonObject2.get("Watchdog Mode").getAsString())) {
                    asJsonObject2.addProperty("Watchdog Mode", "10-CPS");
                }
            }
        }
    }

    private KillAuraHandler() {
    }
}
