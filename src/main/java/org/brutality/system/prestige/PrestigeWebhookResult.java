package org.brutality.system.prestige;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class PrestigeWebhookResult {
    private final boolean[] flagArray;
    public final int count;

    private PrestigeWebhookResult(boolean[] flagArray, int count) {
        this.flagArray = flagArray;
        this.count = count;
    }

    public boolean matchesInt(int destination) {
        return destination >= 0 && destination < this.flagArray.length && this.flagArray[destination];
    }

    public boolean checkState() {
        return this.flagArray[0] && this.flagArray[1];
    }

    public static PrestigeWebhookResult computePrestigeWebhookResult(String body) {
        boolean[] flags = new boolean[2];
        int n = -1;

        try {
            JsonObject asJsonObject = new JsonParser().parse(body).getAsJsonObject();
            if (asJsonObject.has("total_server")) {
                n = asJsonObject.get("total_server").getAsInt();
            }

            if (asJsonObject.has("webhook_delivery") && asJsonObject.get("webhook_delivery").isJsonArray()) {
                JsonArray asJsonArray = asJsonObject.getAsJsonArray("webhook_delivery");

                for (int i = 0; i < flags.length && i < asJsonArray.size(); i++) {
                    JsonElement element = asJsonArray.get(i);
                    flags[i] = element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean() && element.getAsBoolean();
                }
            }
        } catch (RuntimeException ignored) {
        }

        return new PrestigeWebhookResult(flags, n);
    }
}
