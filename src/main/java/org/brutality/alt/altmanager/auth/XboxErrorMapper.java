package org.brutality.alt.altmanager.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;

public final class XboxErrorMapper {
    public static JsonObject computeJsonObject(int n, String str) throws IOException {
        JsonObject json;
        try {
            json = new JsonParser().parse(str).getAsJsonObject();
        } catch (RuntimeException e) {
            throw new IOException("Xbox returned HTTP " + n + " with an invalid response. Try again later.");
        }

        if (n == 200) {
            return json;
        }
        String str2 = json.has("XErr") && json.get("XErr").isJsonPrimitive() ? json.get("XErr").getAsString() : "";

        String str3 = switch (str2) {
            case "2148916233" -> "Sign in at xbox.com and finish creating your Xbox profile, then retry.";
            case "2148916235" -> "Xbox Live is unavailable for this account's country or region.";
            case "2148916236", "2148916237" -> "Sign in at xbox.com and complete the required age verification.";
            case "2148916238" -> "Ask your family organizer to check your Microsoft family and Xbox permissions.";
            default -> "Sign in again. If this continues, check Xbox service status and account permissions.";
        };
        String str4 = str2.matches("[0-9]{1,20}") ? ", XErr " + str2 : "";
        throw new IOException("Xbox HTTP " + n + str4 + ": " + str3);
    }
}
