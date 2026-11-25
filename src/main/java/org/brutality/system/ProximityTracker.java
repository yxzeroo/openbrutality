package org.brutality.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;

public class ProximityTracker {
    private static ProximityTracker instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final String text = "https://brutality-proximity-tracker-production.up.railway.app/api";
    private static final long timestamp = 15000L;
    private static final long timestamp2 = 2000L;
    private long timestamp3 = 0L;
    private long timestamp4 = 0L;
    private Map<String, String> lookupMap = new ConcurrentHashMap<>();
    private boolean enabled2 = true;

    public static ProximityTracker getInstance() {
        if (instance == null) {
            instance = new ProximityTracker();
        }

        return instance;
    }

    public void onEvent(Event e) {
        if (this.enabled2) {
            if (e instanceof EventTick) {
                this.onTick();
            }
        }
    }

    private void onTick() {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            long now = System.currentTimeMillis();
            if (now - this.timestamp3 > 15000L) {
                this.process2();
                this.timestamp3 = now;
            }

            if (now - this.timestamp4 > 2000L) {
                this.process3();
                this.timestamp4 = now;
            }
        }
    }

    private void process2() {
        if (this.mc.thePlayer != null) {
            new Thread(() -> {
                try {
                    String name = this.mc.thePlayer.getName();
                    String str = BackendAuthService.getInstance() != null ? BackendAuthService.getInstance().getUsername() : name;
                    JsonObject json = new JsonObject();
                    json.addProperty("username", name);
                    json.addProperty("brutality_username", str);
                    String str2 = this.computeString("POST", "/heartbeat", json.toString());
                    if (str2 != null) {
                        JsonObject asJsonObject = new JsonParser().parse(str2).getAsJsonObject();
                        if (asJsonObject.has("success") && asJsonObject.get("success").getAsBoolean()) {
                        }
                    }
                } catch (Exception ignored) {
                }
            }).start();
        }
    }

    private void process3() {
        new Thread(() -> {
            try {
                String str = this.computeString("GET", "/online", null);
                if (str != null) {
                    JsonObject asJsonObject = new JsonParser().parse(str).getAsJsonObject();
                    if (asJsonObject.has("clients")) {
                        JsonArray asJsonArray = asJsonObject.getAsJsonArray("clients");
                        ConcurrentHashMap map = new ConcurrentHashMap();

                        for (int i = 0; i < asJsonArray.size(); i++) {
                            JsonObject asJsonObject2 = asJsonArray.get(i).getAsJsonObject();
                            String str2 = asJsonObject2.has("username") ? asJsonObject2.get("username").getAsString() : null;
                            String str3 = asJsonObject2.has("brutality_username") ? asJsonObject2.get("brutality_username").getAsString() : str2;
                            if (str2 != null) {
                                map.put(str2, str3);
                            }
                        }

                        this.lookupMap = new ConcurrentHashMap<>(map);
                    }
                }
            } catch (Exception ignored) {
            }
        }).start();
    }

    private String computeString(String str, String str2, String str3) {
        HttpURLConnection conn = null;

        String str4;
        try {
            URL url = new URL("https://brutality-proximity-tracker-production.up.railway.app/api" + str2);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod(str);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Brutality-Client/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (str3 != null && str.equals("POST")) {
                conn.setDoOutput(true);

                try (OutputStream outputStream = conn.getOutputStream()) {
                    byte[] bytes = str3.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(bytes, 0, bytes.length);
                }
            }

            int responseCode = conn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();

                String str5;
                while ((str5 = reader.readLine()) != null) {
                    sb.append(str5);
                }

                str4 = sb.toString();
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return str4;
    }

    public void process() {
        if (this.mc.thePlayer != null) {
            new Thread(() -> {
                try {
                    String name = this.mc.thePlayer.getName();
                    JsonObject json = new JsonObject();
                    json.addProperty("username", name);
                    this.computeString("POST", "/logout", json.toString());
                } catch (Exception ignored) {
                }
            }).start();
        }
    }

    public Set<String> computeSet() {
        return new HashSet<>(this.lookupMap.keySet());
    }

    public boolean matchesString(String username) {
        return this.lookupMap.containsKey(username);
    }

    public String computeString(String mcUsername) {
        return this.lookupMap.getOrDefault(mcUsername, mcUsername);
    }

    public void setEnabled(boolean enabled) {
        this.enabled2 = enabled;
        if (!enabled) {
            this.process();
            this.lookupMap.clear();
        }
    }

    public boolean isEnabled() {
        return this.enabled2;
    }
}
