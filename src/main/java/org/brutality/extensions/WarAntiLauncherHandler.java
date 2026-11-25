package org.brutality.extensions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map.Entry;

public final class WarAntiLauncherHandler {
    private WarAntiLauncherHandler() {
    }

    private static void handleJsonObject2(JsonObject json) {
        for (String[] parts : new String[][]{
            {"War AntiLauncher", "AntiLauncher"}, {"War Displace", "Displace"}, {"War KeepSprint", "KeepSprint When Unwatched"}
        }) {
            if (json.has(parts[0])) {
                if (!json.has(parts[1])) {
                    json.add(parts[1], json.get(parts[0]));
                }

                json.remove(parts[0]);
            }
        }

        for (Entry entry : new ArrayList<>(json.entrySet())) {
            if (((JsonElement)entry.getValue()).isJsonObject()) {
                handleJsonObject2(((JsonElement)entry.getValue()).getAsJsonObject());
            }
        }
    }

    public static void handleJsonObject(JsonObject json) {
        if (json.has("AutoHunt") && json.get("AutoHunt").isJsonObject()) {
            handleJsonObject2(json.getAsJsonObject("AutoHunt"));
        }
    }
}
