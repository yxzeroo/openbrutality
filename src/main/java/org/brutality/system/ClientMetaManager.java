package org.brutality.system;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.brutality.gui.ChangeLog;

public final class ClientMetaManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File file = new File("Brutality");
    private static final File file2 = new File(file, "client_meta.json");
    private static boolean initialized = false;
    private static long timestamp = 0L;
    private static long timestamp2 = 0L;
    private static String text = null;
    private static List<ChangeLog.Entry> entries = Collections.emptyList();

    private ClientMetaManager() {
    }

    public static void init() {
        if (!initialized) {
            initialized = true;
            long now = System.currentTimeMillis();
            JsonObject json = computeJsonObject();
            timestamp = json.has("last_launch") ? json.get("last_launch").getAsLong() : 0L;
            text = json.has("last_seen_version") ? json.get("last_seen_version").getAsString() : null;
            if (timestamp > 0L && now > timestamp) {
                timestamp2 = now - timestamp;
            }

            entries = computeList(text);
            Integer integer = Integer.getInteger("brutality.forceCount");
            boolean flag = Boolean.getBoolean("brutality.forceWelcome") || integer != null;
            if (flag) {
                int n = integer != null ? Math.max(1, integer) : 5;
                entries = ChangeLog.computeList(n);
                if (timestamp2 <= 0L) {
                    timestamp2 = 1814400000L;
                }
            }

            ChangeLog.Entry entry = ChangeLog.computeEntry();
            if (entry != null) {
                json.addProperty("last_seen_version", entry.text);
            }

            json.addProperty("last_launch", now);
            handleJsonObject(json);
        }
    }

    private static List<ChangeLog.Entry> computeList(String str) {
        List list = ChangeLog.computeList();
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        if (str != null && !str.isEmpty()) {
            int n = -1;

            for (int i = 0; i < list.size(); i++) {
                if (((ChangeLog.Entry)list.get(i)).text.equals(str)) {
                    n = i;
                    break;
                }
            }

            if (n == 0) {
                return Collections.emptyList();
            }
            return n < 0 ? new ArrayList<>(list.subList(0, 1)) : new ArrayList<>(list.subList(0, n));
        }
        return Collections.emptyList();
    }

    public static boolean checkState() {
        return !entries.isEmpty();
    }

    public static List<ChangeLog.Entry> getEntries() {
        return entries;
    }

    public static int computeInt() {
        return entries.size();
    }

    public static long getTimestamp() {
        return timestamp;
    }

    public static long getTimestamp2() {
        return timestamp2;
    }

    public static long computeLong() {
        return Math.max(0L, timestamp2 / 86400000L);
    }

    public static String computeString() {
        long l = computeLong();
        if (l <= 0L) {
            return "a little while";
        }
        if (l < 7L) {
            return l + (l == 1L ? " day" : " days");
        }
        if (l < 30L) {
            long l2 = l / 7L;
            return l2 + (l2 == 1L ? " week" : " weeks");
        }
        if (l < 365L) {
            long l3 = l / 30L;
            return l3 + (l3 == 1L ? " month" : " months");
        }
        long l4 = l / 365L;
        return l4 + (l4 == 1L ? " year" : " years");
    }

    private static JsonObject computeJsonObject() {
        if (!file2.exists()) {
            return new JsonObject();
        }
        try {
            JsonObject json;
            try (FileReader fileReader = new FileReader(file2)) {
                json = new JsonParser().parse(fileReader).getAsJsonObject();
            }

            return json;
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    private static void handleJsonObject(JsonObject json) {
        try {
            if (!file.exists()) {
                file.mkdirs();
            }

            try (FileWriter fileWriter = new FileWriter(file2, StandardCharsets.UTF_8)) {
                gson.toJson((JsonElement)json, fileWriter);
            }
        } catch (Exception ignored) {
        }
    }
}
