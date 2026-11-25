package org.brutality.mellow.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class StripColorHandler {
    public String stripColor(String str) {
        return str.replaceAll("§[0-9a-fk-or]", "").replaceAll("#[0-9a-fA-F]{6}", "");
    }

    public UUID computeUUID(String str) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + str);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String str2;
                while ((str2 = reader.readLine()) != null) {
                    sb.append(str2);
                }

                reader.close();
                JsonObject asJsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
                String asString = asJsonObject.get("id").getAsString();
                return UUID.fromString(
                    asString.substring(0, 8) + "-" + asString.substring(8, 12) + "-" + asString.substring(12, 16) + "-" + asString.substring(16, 20) + "-" + asString.substring(20)
                );
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public String computeString(UUID uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }

                reader.close();
                JsonObject asJsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
                return asJsonObject.get("name").getAsString();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public String computeString(String str) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + str);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String str2;
                while ((str2 = reader.readLine()) != null) {
                    sb.append(str2);
                }

                reader.close();
                JsonObject asJsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
                return asJsonObject.get("id").getAsString();
            }
        } catch (Exception ignored) {
        }

        return "ERROR";
    }
}
