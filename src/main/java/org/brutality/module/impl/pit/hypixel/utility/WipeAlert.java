package org.brutality.module.impl.pit.hypixel.utility;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.notification.old.ModuleEnabledHandler;

public class WipeAlert extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private long timestamp;
    private String text;
    private int generation;
    private CompletableFuture<JsonObject> completableFuture;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && event instanceof EventUpdate && mc.thePlayer != null && mc.theWorld != null) {
            long now = System.currentTimeMillis();
            if (this.completableFuture == null && now >= this.timestamp) {
                this.timestamp = now + 60000L;
                int generation = this.generation;
                this.completableFuture = CompletableFuture.supplyAsync(() -> {
                    HttpURLConnection conn = null;

                    Object obj;
                    try {
                        conn = (HttpURLConnection)new URL("https://pitpal.rocks/api/ign/latest-wipe").openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setReadTimeout(10000);
                        conn.setRequestProperty("User-Agent", "Brutality-WipeAlert");
                        if (conn.getResponseCode() == 200) {
                            try (InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                                return new JsonParser().parse(inputStreamReader).getAsJsonObject();
                            }
                        }

                        obj = null;
                    } catch (Exception e) {
                        return null;
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }

                    return (JsonObject)obj;
                });
                this.completableFuture.thenAccept(json -> mc.addScheduledTask(() -> {
                    if (this.generation == generation && this.checkState44()) {
                        this.completableFuture = null;
                        String str = this.computeString(json);
                        if (str != null) {
                            ModuleEnabledHandler.handleString5("Pit Wipe", str + " was wiped!", 8000L);
                        }
                    }
                }));
            }
        }
    }

    private String computeString(JsonObject json) {
        if (json != null && json.has("data") && json.get("data").isJsonObject()) {
            JsonObject asJsonObject = json.getAsJsonObject("data");
            if (asJsonObject.has("ign") && asJsonObject.get("ign").isJsonPrimitive() && asJsonObject.has("latestWipeAt") && asJsonObject.get("latestWipeAt").isJsonPrimitive()) {
                String asString = asJsonObject.get("ign").getAsString();
                String asString2 = asJsonObject.get("latestWipeAt").getAsString();
                if (asString.matches("[A-Za-z0-9_]{1,16}") && !asString2.isEmpty()) {
                    String str = asString + ":" + asString2;
                    boolean flag = this.text != null && !this.text.equals(str);
                    this.text = str;
                    return flag ? asString : null;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.generation++;
        this.completableFuture = null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.generation++;
        this.timestamp = 0L;
        this.text = null;
        this.completableFuture = null;
    }

    public WipeAlert() {
        super("WipeAlert", 0, Category.PIT);
    }
}
