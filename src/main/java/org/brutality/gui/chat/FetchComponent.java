package org.brutality.gui.chat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.brutality.utils.hypixel.pit.ParseHelper;
import org.brutality.utils.hypixel.pit.ParseHelper.Entry2;

public final class FetchComponent {
    private static final String text = "https://pitpanda.rocks/api/itemsearch/nonce";
    private static final String text2 = "617c9c76-e5a5-4213-a6b9-aded74ae64c8";
    private static final int count = 12;
    private static final Map<Long, CompletableFuture<FetchComponent.Entry>> lookupMap = new ConcurrentHashMap<>();
    private static final Map<String, String> lookupMap2 = new ConcurrentHashMap<>();

    public static String computeString(String str) {
        if (str != null && !str.isEmpty()) {
            String str2 = str.replace("-", "").toLowerCase(Locale.ROOT);
            String str3 = lookupMap2.get(str2);
            if (str3 != null) {
                return str3;
            }
            String str4 = computeString("https://api.minecraftservices.com/minecraft/profile/lookup/" + str2, "name", false);
            if (str4 == null) {
                str4 = computeString("https://sessionserver.mojang.com/session/minecraft/profile/" + str2, "name", false);
            }

            if (str4 == null) {
                str4 = computeString("https://pitpanda.rocks/api/players/" + str2 + "?key=617c9c76-e5a5-4213-a6b9-aded74ae64c8", "name", true);
            }

            if (str4 != null) {
                lookupMap2.put(str2, str4);
            }

            return str4;
        }
        return null;
    }

    private static String computeString(String str, String str2, boolean flag) {
        try {
            HttpURLConnection conn = (HttpURLConnection)new URL(str).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(8000);
            if (conn.getResponseCode() != 200) {
                return null;
            }
            String str3;
            try (InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject asJsonObject = new JsonParser().parse(inputStreamReader).getAsJsonObject();
                if (flag) {
                    asJsonObject = asJsonObject.has("data") && asJsonObject.get("data").isJsonObject() ? asJsonObject.getAsJsonObject("data") : null;
                }

                if (asJsonObject == null || !asJsonObject.has(str2) || asJsonObject.get(str2).isJsonNull()) {
                    return null;
                }

                String asString = asJsonObject.get(str2).getAsString();
                str3 = asString.matches("[A-Za-z0-9_]{1,16}") ? asString : null;
            }

            return str3;
        } catch (Exception e) {
            return null;
        }
    }

    private FetchComponent() {
    }

    private static FetchComponent.Entry computeEntry(long l) {
        FetchComponent.Entry entry = new FetchComponent.Entry();

        try {
            HttpURLConnection conn = (HttpURLConnection)new URL(
                    "https://pitpanda.rocks/api/itemsearch/nonce" + l + "?key=617c9c76-e5a5-4213-a6b9-aded74ae64c8"
                )
                .openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            int responseCode = conn.getResponseCode();
            if (responseCode == 429) {
                entry.error = "PitPanda rate limit - try again in a moment";
                lookupMap.remove(l);
                return entry;
            }

            if (responseCode != 200) {
                entry.error = "PitPanda returned " + responseCode;
                lookupMap.remove(l);
                return entry;
            }

            JsonObject json;
            try (InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                json = new JsonParser().parse(inputStreamReader).getAsJsonObject();
            }

            ParseHelper.Entry entry2 = ParseHelper.parse(json);
            int n = 0;

            for (Entry2 entry22 : entry2.entries) {
                if (entry.entries.size() >= 12) {
                    break;
                }

                String str = computeString(entry22.text);
                n |= str == null ? 1 : 0;
                entry.entries.add(new String[]{str != null ? str : computeString2(entry22.text), computeString(entry22.timestamp)});
            }

            if (entry2.entry2 != null) {
                String str2 = computeString(entry2.entry2.text);
                n = (str2 == null ? 1 : 0) + (n & ~(str2 == null ? 1 : 0));
                entry.stringArray = new String[]{str2 != null ? str2 : computeString2(entry2.entry2.text), computeString(entry2.entry2.timestamp)};
            }

            if (n != 0) {
                lookupMap.remove(l);
            }

            if (entry.entries.isEmpty() && entry.stringArray == null) {
                entry.error = "No history found for this item";
            }
        } catch (Exception e) {
            entry.error = "Could not reach PitPanda";
            lookupMap.remove(l);
        }

        return entry;
    }

    public static CompletableFuture<FetchComponent.Entry> fetch(long l) {
        return lookupMap.computeIfAbsent(l, val -> CompletableFuture.supplyAsync(() -> computeEntry(val)));
    }

    private static String computeString2(String str) {
        String str2 = computeString(str);
        return str2 != null ? str2 : (str == null ? "?" : str.substring(0, Math.min(8, str.length())));
    }

    private static String computeString(long l) {
        return l > 0L ? new SimpleDateFormat("dd MMM yyyy").format(new Date(l)) : "unknown date";
    }

    public static final class Entry {
        public final List<String[]> entries = new ArrayList<>();
        public String[] stringArray;
        public String error;
    }
}
