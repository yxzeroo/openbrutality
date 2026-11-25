package org.brutality.mellow.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.brutality.mellow.data.GetPlayerNameHandler;

public class ERRORHandler {
    private final StripColorHandler stripColorHandler;

    public GetPlayerNameHandler computeGetPlayerNameHandler(String str) {
        try {
            String str2 = this.stripColorHandler.computeString(str);
            if (str2 == null || str2.equals("ERROR")) {
                return null;
            }

            String str3 = "https://nadeshiko.io/player/" + str2 + "/network";
            URL url = new URL(str3);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty(
                "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
            );
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setInstanceFollowRedirects(true);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String str4;
                while ((str4 = reader.readLine()) != null) {
                    sb.append(str4);
                }

                reader.close();
                GetPlayerNameHandler getPlayerNameHandler = this.computeGetPlayerNameHandler(sb.toString(), str);
                if (getPlayerNameHandler != null) {
                    return getPlayerNameHandler;
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private GetPlayerNameHandler computeGetPlayerNameHandler(String str, String str2) {
        try {
            Pattern pattern = Pattern.compile("playerData = JSON.parse\\(decodeURIComponent\\(\"(.*?)\"\\)\\)");
            Matcher matcher = pattern.matcher(str);
            String str3;
            if (matcher.find()) {
                str3 = URLDecoder.decode(matcher.group(1), "UTF-8");
            } else {
                str3 = str;
            }

            JsonObject asJsonObject = new JsonParser().parse(str3).getAsJsonObject();
            JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("profile");
            JsonObject asJsonObject3 = asJsonObject.getAsJsonObject("achievements");
            JsonObject asJsonObject4 = asJsonObject.getAsJsonObject("stats").getAsJsonObject("Bedwars");
            String str4 = asJsonObject3.has("bedwars_level") ? asJsonObject3.get("bedwars_level").getAsString() : "0";
            int n = asJsonObject4.has("final_kills_bedwars") ? asJsonObject4.get("final_kills_bedwars").getAsInt() : 0;
            int n2 = asJsonObject4.has("final_deaths_bedwars") ? asJsonObject4.get("final_deaths_bedwars").getAsInt() : 1;
            double d = n2 == 0 ? n : (double)n / n2;
            int n3 = asJsonObject4.has("winstreak") ? asJsonObject4.get("winstreak").getAsInt() : 0;
            int n4 = asJsonObject4.has("wins_bedwars") ? asJsonObject4.get("wins_bedwars").getAsInt() : 0;
            int n5 = asJsonObject4.has("beds_broken_bedwars") ? asJsonObject4.get("beds_broken_bedwars").getAsInt() : 0;
            GetPlayerNameHandler getPlayerNameHandler = new GetPlayerNameHandler();
            getPlayerNameHandler.setPlayerName(str2);
            getPlayerNameHandler.handleInt3(Integer.parseInt(str4.replaceAll("[^0-9]", "")));
            getPlayerNameHandler.handleDouble3(d);
            getPlayerNameHandler.handleInt(n3);
            getPlayerNameHandler.handleInt4(n4);
            getPlayerNameHandler.handleInt5(n5);
            getPlayerNameHandler.handleInt2(n);
            int n6 = asJsonObject4.has("kills_bedwars") ? asJsonObject4.get("kills_bedwars").getAsInt() : 0;
            int n7 = asJsonObject4.has("deaths_bedwars") ? asJsonObject4.get("deaths_bedwars").getAsInt() : 0;
            getPlayerNameHandler.handleInt6(n6);
            getPlayerNameHandler.handleDouble2(n7 == 0 ? n6 : (double)n6 / n7);
            int n8 = asJsonObject4.has("losses_bedwars") ? asJsonObject4.get("losses_bedwars").getAsInt() : 0;
            getPlayerNameHandler.handleDouble(n8 == 0 ? n4 : (double)n4 / n8);
            getPlayerNameHandler.setTimestamp(System.currentTimeMillis());
            return getPlayerNameHandler;
        } catch (Exception e) {
            return null;
        }
    }

    public ERRORHandler(StripColorHandler stripColorHandler) {
        this.stripColorHandler = stripColorHandler;
    }
}
