package org.brutality.utils.render;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map.Entry;

public class RenderHelper3 {
    public static Gson gson = new Gson();
    public static Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
    public static JsonParser jsonParser = new JsonParser();

    public static JsonElement computeJsonElement(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject json = new JsonObject();

            for (Entry entry : element.getAsJsonObject().entrySet()) {
                json.add((String)entry.getKey(), computeJsonElement((JsonElement)entry.getValue()));
            }

            return json;
        }
        if (!element.isJsonArray()) {
            return element;
        }
        JsonArray jsonArray = new JsonArray();

        for (JsonElement element2 : element.getAsJsonArray()) {
            jsonArray.add(computeJsonElement(element2));
        }

        return jsonArray;
    }
}
