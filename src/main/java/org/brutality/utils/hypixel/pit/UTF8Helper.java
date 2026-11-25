package org.brutality.utils.hypixel.pit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.module.impl.pit.hypixel.hud.StreakInfo;

public class UTF8Helper implements Runnable {
    private static final String text = "617c9c76-e5a5-4213-a6b9-aded74ae64c8";
    private int prestige;
    private static long timestamp = 0L;
    private static final long timestamp2 = 60000L;
    private static boolean flag = false;

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        if (now - timestamp >= 60000L) {
            HttpURLConnection conn = null;

            try {
                timestamp = System.currentTimeMillis();
                flag = false;
                String username = Minecraft.getMinecraft().getSession().getUsername();
                URL url = new URL("https://pitpanda.rocks/api/players/" + URLEncoder.encode(username, "UTF-8") + "?key=617c9c76-e5a5-4213-a6b9-aded74ae64c8");
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    throw new IOException("PitPanda API returned HTTP " + responseCode);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }

                reader.close();
                JsonObject asJsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
                if (!asJsonObject.has("success") || !asJsonObject.get("success").getAsBoolean() || !asJsonObject.has("data") || asJsonObject.get("data").isJsonNull()) {
                    throw new IOException("PitPanda returned no player data");
                }

                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("data");
                JsonObject json = asJsonObject2.has("doc") && !asJsonObject2.get("doc").isJsonNull() ? asJsonObject2.getAsJsonObject("doc") : new JsonObject();
                this.prestige = json.has("prestige") ? json.get("prestige").getAsInt() : 0;
                double d = asJsonObject2.has("currentGold") ? asJsonObject2.get("currentGold").getAsDouble() : 0.0;
                StreakInfo streakInfo = (StreakInfo)Brutality.moduleManager.computeModule("StreakInfo");
                streakInfo.handleInt(this.prestige);
                if (streakInfo.doubleValue < d) {
                    streakInfo.handleDoubleType(d);
                }
            } catch (Exception e) {
                if (e instanceof IOException && e.getMessage() != null && e.getMessage().contains("429") && !flag) {
                    flag = true;
                }

                this.prestige = 0;

                try {
                    StreakInfo streakInfo2 = (StreakInfo)Brutality.moduleManager.computeModule("StreakInfo");
                    streakInfo2.handleInt(0);
                } catch (Exception ignored) {
                }
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }
}
