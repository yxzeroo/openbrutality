package org.brutality.extensions;

import com.google.gson.JsonObject;

public final class SettingsHandler {
    public static void handleJsonObject(JsonObject json) {
        if (json.has("Settings") && json.get("Settings").isJsonObject()) {
            JsonObject asJsonObject = json.getAsJsonObject("Settings");
            if (asJsonObject.has("settings") && asJsonObject.get("settings").isJsonObject()) {
                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");
                if (asJsonObject2.has("MovementFix")) {
                    if (!json.has("MovementFix")) {
                        boolean flag = true;

                        try {
                            flag = asJsonObject2.get("MovementFix").getAsBoolean();
                        } catch (RuntimeException ignored) {
                        }

                        JsonObject json2 = new JsonObject();
                        JsonObject json3 = new JsonObject();
                        json2.addProperty("enabled", flag);
                        json2.addProperty("key", 0);
                        json2.addProperty("hidden", false);
                        json3.addProperty("Only When Rotating", true);
                        json2.add("settings", json3);
                        json.add("MovementFix", json2);
                    }

                    asJsonObject2.remove("MovementFix");
                }
            }
        }
    }

    private SettingsHandler() {
    }
}
