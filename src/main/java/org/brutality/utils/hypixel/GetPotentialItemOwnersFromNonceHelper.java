package org.brutality.utils.hypixel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetPotentialItemOwnersFromNonceHelper {
    public static JsonObject computeJsonObject2(String str) {
        try {
            URL url = new URL(str);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty(
                "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            );
            conn.setRequestProperty("accept", "application/json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String str2;
            while ((str2 = reader.readLine()) != null) {
                sb.append(str2);
            }

            reader.close();
            Gson gson = new Gson();
            return gson.fromJson(sb.toString(), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getPotentialItemOwnersFromNonce(Integer integer) {
        JsonObject json = computeJsonObject2("https://pitpanda.rocks/api/itemsearch/nonce" + integer + "?&key=617c9c76-e5a5-4213-a6b9-aded74ae64c8");
        if (json == null) {
            return null;
        }
        ArrayList list = new ArrayList();
        json.get("items").getAsJsonArray().forEach(element -> {
            JsonObject asJsonObject = element.getAsJsonObject();
            list.add(asJsonObject.get("owner").getAsString());
        });
        ArrayList list2 = new ArrayList();
        list.forEach(obj -> list2.add(getName((String)obj)));
        return list2;
    }

    public static JsonObject computeJsonObject(String str) {
        try {
            URL url = new URL(str);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) {
                if (str.startsWith("https://api.hypixel.net/")) {
                    InputStream errorStream = conn.getErrorStream();

                    JsonObject json;
                    try (Scanner scanner = new Scanner(errorStream)) {
                        scanner.useDelimiter("\\Z");
                        String str2 = scanner.next();
                        Gson gson = new Gson();
                        JsonObject json2 = gson.fromJson(str2, JsonObject.class);
                        json = json2;
                    }

                    return json;
                }
                return str.startsWith("https://api.mojang.com/users/profiles/minecraft/") && conn.getResponseCode() == 204 ? null : null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String str3;
            while ((str3 = reader.readLine()) != null) {
                sb.append(str3);
            }

            reader.close();
            Gson gson2 = new Gson();
            return gson2.fromJson(sb.toString(), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getName(String str) {
        JsonObject json = computeJsonObject("https://sessionserver.mojang.com/session/minecraft/profile/" + str.replaceAll("-", ""));
        return json == null ? null : json.getAsJsonObject().get("name").getAsString();
    }
}
