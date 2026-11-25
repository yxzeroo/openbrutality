package org.brutality.mellow.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.brutality.mellow.data.GetPlayerNameHandler;

public class SuccessHandler {
    private final StripColorHandler stripColorHandler;

    private GetPlayerNameHandler computeGetPlayerNameHandler(String str, String str2) {
        try {
            JsonObject asJsonObject = new JsonParser().parse(str).getAsJsonObject();
            if (asJsonObject.has("success") && asJsonObject.get("success").getAsBoolean()) {
                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("player");
                JsonObject asJsonObject3 = asJsonObject2.getAsJsonObject("achievements");
                JsonObject asJsonObject4 = asJsonObject2.getAsJsonObject("stats").getAsJsonObject("Bedwars");
                int n = 0;
                if (asJsonObject3.has("bedwars_level")) {
                    try {
                        n = asJsonObject3.get("bedwars_level").getAsInt();
                    } catch (Exception e) {
                        try {
                            String asString = asJsonObject3.get("bedwars_level").getAsString();
                            n = Integer.parseInt(asString.replaceAll("[^0-9]", ""));
                        } catch (Exception ignored) {
                        }
                    }
                }

                int n2 = asJsonObject4.has("final_kills_bedwars") ? asJsonObject4.get("final_kills_bedwars").getAsInt() : 0;
                int n3 = asJsonObject4.has("final_deaths_bedwars") ? asJsonObject4.get("final_deaths_bedwars").getAsInt() : 0;
                double d = n3 == 0 ? n2 : (double)n2 / n3;
                int n4 = asJsonObject4.has("wins_bedwars") ? asJsonObject4.get("wins_bedwars").getAsInt() : 0;
                int n5 = asJsonObject4.has("losses_bedwars") ? asJsonObject4.get("losses_bedwars").getAsInt() : 0;
                double d2 = n5 == 0 ? n4 : (double)n4 / n5;
                GetPlayerNameHandler getPlayerNameHandler = new GetPlayerNameHandler();
                getPlayerNameHandler.setPlayerName(str2);
                getPlayerNameHandler.handleInt3(n);
                getPlayerNameHandler.handleDouble3(d);
                getPlayerNameHandler.handleInt(asJsonObject4.has("winstreak") ? asJsonObject4.get("winstreak").getAsInt() : 0);
                getPlayerNameHandler.handleDouble(d2);
                getPlayerNameHandler.handleInt2(n2);
                getPlayerNameHandler.handleInt5(asJsonObject4.has("beds_broken_bedwars") ? asJsonObject4.get("beds_broken_bedwars").getAsInt() : 0);
                getPlayerNameHandler.handleInt4(n4);
                int n6 = asJsonObject4.has("kills_bedwars") ? asJsonObject4.get("kills_bedwars").getAsInt() : 0;
                int n7 = asJsonObject4.has("deaths_bedwars") ? asJsonObject4.get("deaths_bedwars").getAsInt() : 0;
                getPlayerNameHandler.handleInt6(n6);
                getPlayerNameHandler.handleDouble2(n7 == 0 ? n6 : (double)n6 / n7);
                getPlayerNameHandler.setTimestamp(System.currentTimeMillis());
                return getPlayerNameHandler;
            }
            return null;
        } catch (Exception e2) {
            return null;
        }
    }

    public GetPlayerNameHandler computeGetPlayerNameHandler(String str) {
        try {
            String str2 = this.stripColorHandler.computeString(str);
            if (str2 == null || str2.equals("ERROR")) {
                return null;
            }

            URL url = new URL("http://api.abyssoverlay.com/player?uuid=" + str2);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setRequestProperty("User-Agent", "node-ao/2.0.3");
            conn.setRequestProperty("Accept", "application/json");
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String str3;
                while ((str3 = reader.readLine()) != null) {
                    sb.append(str3);
                }

                reader.close();
                return this.computeGetPlayerNameHandler(sb.toString(), str);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public SuccessHandler(StripColorHandler stripColorHandler) {
        this.stripColorHandler = stripColorHandler;
    }
}
