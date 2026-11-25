package org.brutality.system.prestige;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import org.brutality.system.BackendAuthService;

public final class PrestigeStatsStore {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File file = new File(BackendAuthService.file, "prestige_stats.json");

    private PrestigeStatsStore() {
    }

    public static int computeInt(String brutalityUser, String minecraftUser) {
        JsonObject json = computeJsonObject();
        JsonObject json2 = json.has("users") ? json.getAsJsonObject("users") : new JsonObject();
        String str = computeString(brutalityUser, minecraftUser);
        if (!json2.has(str)) {
            return -1;
        }
        JsonObject asJsonObject = json2.getAsJsonObject(str);
        return asJsonObject.has("prestige_level") ? asJsonObject.get("prestige_level").getAsInt() : -1;
    }

    public static void handleString(String brutalityUser, String minecraftUser, int newLevel, int totalAnnouncements) {
        JsonObject json = computeJsonObject();
        JsonObject json2 = json.has("users") ? json.getAsJsonObject("users") : new JsonObject();
        String str = computeString(brutalityUser, minecraftUser);
        JsonObject json3 = new JsonObject();
        json3.addProperty("brutality_username", brutalityUser);
        json3.addProperty("minecraft_username", minecraftUser);
        json3.addProperty("prestige_level", newLevel);
        json3.addProperty("total_prestiges", totalAnnouncements);
        json3.addProperty("updated_at", System.currentTimeMillis());
        json2.add(str, json3);
        json.add("users", json2);
        handleJsonObject(json);
    }

    private static String computeString(String str, String str2) {
        return (str == null ? "unknown" : str.toLowerCase()) + "|" + (str2 == null ? "unknown" : str2.toLowerCase());
    }

    private static JsonObject computeJsonObject() {
        if (!file.exists()) {
            return new JsonObject();
        }
        try {
            JsonObject json;
            try (FileReader fileReader = new FileReader(file)) {
                json = new JsonParser().parse(fileReader).getAsJsonObject();
            }

            return json;
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    private static void handleJsonObject(JsonObject json) {
        try {
            if (!BackendAuthService.file.exists()) {
                BackendAuthService.file.mkdirs();
            }

            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
                gson.toJson((JsonElement)json, fileWriter);
            }
        } catch (Exception ignored) {
        }
    }
}
