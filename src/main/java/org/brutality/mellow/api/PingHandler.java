package org.brutality.mellow.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PingHandler {
    private final StripColorHandler stripColorHandler;

    public String computeString(String str, String str2) {
        try {
            String str3 = "https://urchin.gg/api/v1/tag/" + str;
            if (str2 != null && !str2.isEmpty()) {
                str3 = str3 + "?key=" + str2;
            }

            URL url = new URL(str3);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str4 = reader.readLine();
                reader.close();
                if (str4 != null) {
                    JsonObject asJsonObject = new JsonParser().parse(str4).getAsJsonObject();
                    if (asJsonObject.has("tag")) {
                        return asJsonObject.get("tag").getAsString();
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public PingHandler(StripColorHandler stripColorHandler) {
        this.stripColorHandler = stripColorHandler;
    }

    public String computeString(String str) {
        try {
            URL url = new URL("https://urchin.gg/api/v1/ping/" + str);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str2 = reader.readLine();
                reader.close();
                if (str2 != null) {
                    JsonObject asJsonObject = new JsonParser().parse(str2).getAsJsonObject();
                    if (asJsonObject.has("ping")) {
                        return asJsonObject.get("ping").getAsString();
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}
