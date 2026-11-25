package org.brutality.utils.hypixel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class RequestHelper {
    public static final String text = "https://api.plancke.io/hypixel/v1/punishmentStats";
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "Brutality-PunishmentStats");
        thread.setDaemon(true);
        return thread;
    });
    private static CompletableFuture<RequestHelper.Entry> completableFuture;
    private static volatile RequestHelper.Entry entry;
    private static long timestamp;
    private static final long timestamp2 = -2391863095254253568L;

    public static String computeString(RequestHelper.Entry entry, RequestHelper.Entry entry2) {
        if (entry2 == null) {
            return "Ban statistics unavailable.";
        }
        if (entry != null
            && entry2.timestamp4 > entry.timestamp4
            && entry.timestamp >= 0L
            && entry.timestamp2 >= 0L
            && entry2.timestamp >= entry.timestamp
            && entry2.timestamp2 >= entry.timestamp2) {
            long l = entry2.timestamp - entry.timestamp;
            long l2 = entry2.timestamp2 - entry.timestamp2;
            long l3 = l;
            return "Network-wide since previous sample: " + l3 + " Watchdog, " + l2 + " staff. These counts cannot identify individual bans.";
        }
        return "Latest network snapshot: Watchdog bans in the last minute: "
            + (entry2.timestamp3 >= 0L ? Long.toString(entry2.timestamp3) : "unavailable")
            + ". Staff interval unavailable; individual ban types are not published.";
    }

    private static RequestHelper.Entry computeEntry() throws IOException {
        HttpURLConnection conn = (HttpURLConnection)new URL("https://api.plancke.io/hypixel/v1/punishmentStats").openConnection();
        conn.setConnectTimeout(4000);
        conn.setReadTimeout(4000);
        conn.setRequestProperty("User-Agent", "Minecraft Brutality Client");

        RequestHelper.Entry entry;
        try {
            if (conn.getResponseCode() != 200) {
                throw new IOException("Punishment API unavailable");
            }

            try (InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                char[] chars = new char[2048];
                StringBuilder sb = new StringBuilder();

                int n;
                while ((n = inputStreamReader.read(chars)) != -1) {
                    if (sb.length() + n > (int)timestamp2) {
                        throw new IOException("Oversized punishment response");
                    }

                    sb.append(chars, 0, n);
                }

                entry = computeEntry(sb.toString(), System.currentTimeMillis());
            }
        } finally {
            conn.disconnect();
        }

        return entry;
    }

    private static long computeLong(JsonObject json, String str) {
        if (json.has(str) && !json.get(str).isJsonNull()) {
            JsonElement element = json.get(str);
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                return Math.max(-1L, element.getAsLong());
            }
            throw new IllegalArgumentException("Invalid punishment counter");
        }
        return -1L;
    }

    public static synchronized CompletableFuture<RequestHelper.Entry> request() {
        if (completableFuture != null && !completableFuture.isDone()) {
            return completableFuture;
        }
        if (completableFuture != null && System.currentTimeMillis() - timestamp < 10000L) {
            return completableFuture;
        }
        timestamp = System.currentTimeMillis();
        completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                RequestHelper.Entry entry2 = computeEntry();
                entry = entry2;
                return entry2;
            } catch (RuntimeException | IOException e) {
                throw new CompletionException(e);
            }
        }, executorService);
        return completableFuture;
    }

    private RequestHelper() {
    }

    public static RequestHelper.Entry computeEntry(String str, long l) {
        JsonObject asJsonObject = new JsonParser().parse(str).getAsJsonObject();
        if (asJsonObject.has("success") && !asJsonObject.get("success").getAsBoolean()) {
            throw new IllegalArgumentException("API reported failure");
        }
        if (asJsonObject.has("record") && asJsonObject.get("record").isJsonObject()) {
            JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("record");
            long l2 = computeLong(asJsonObject2, "watchdog_total");
            long l3 = computeLong(asJsonObject2, "staff_total");
            long l4 = computeLong(asJsonObject2, "watchdog_lastMinute");
            if (l2 < 0L && l3 < 0L && l4 < 0L) {
                throw new IllegalArgumentException("Missing punishment counters");
            }
            return new RequestHelper.Entry(l2, l3, l4, l);
        }
        throw new IllegalArgumentException("Missing punishment record");
    }

    public static RequestHelper.Entry getEntry() {
        return entry;
    }

    public static final class Entry {
        public final long timestamp;
        public final long timestamp2;
        public final long timestamp3;
        public final long timestamp4;

        public Entry(long timestamp, long l, long l2, long l3) {
            this.timestamp = timestamp;
            this.timestamp2 = l;
            this.timestamp3 = l2;
            this.timestamp4 = l3;
        }
    }
}
