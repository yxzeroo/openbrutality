package org.brutality.system.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.brutality.bootstrap.PinnedTlsHttpClient;
import org.brutality.proxy.ProxyManager;
import org.brutality.system.BackendAuthService;

public final class BrutalityCloudClient {
    public static final String text = "https://brutality-backend-production.up.railway.app/api";

    private BrutalityCloudClient() {
    }

    public static boolean checkState() {
        try {
            return BackendAuthService.getInstance().checkState4();
        } catch (Exception e) {
            return false;
        }
    }

    public static String b() {
        try {
            return BackendAuthService.getInstance().getUsername();
        } catch (Exception e) {
            return null;
        }
    }

    public static String computeString() {
        try {
            return BackendAuthService.getInstance().computeString4();
        } catch (Exception e) {
            return b();
        }
    }

    public static void listAsync(
        BrutalityCloudClient.ConfigMode kind, String query, BrutalityCloudClient.PopularMode sort, boolean mineOnly, Consumer<BrutalityCloudClient.Entry5> callback
    ) {
        CompletableFuture.runAsync(
            () -> {
                try {
                    BrutalityCloudClient.PopularMode popularMode = sort;
                    if (sort == BrutalityCloudClient.PopularMode.NAME_AZ || sort == BrutalityCloudClient.PopularMode.NAME_ZA) {
                        popularMode = BrutalityCloudClient.PopularMode.POPULAR;
                    }

                    StringBuilder sb = new StringBuilder("/cloud/items?type=")
                        .append(kind == BrutalityCloudClient.ConfigMode.CONFIG ? "config" : "script")
                        .append("&sort=")
                        .append(popularMode.api);
                    if (query != null && !query.trim().isEmpty()) {
                        sb.append("&q=").append(computeString(query.trim()));
                    }

                    if (mineOnly) {
                        sb.append("&mine=1");
                        String str = b();
                        if (str != null && !str.isEmpty()) {
                            sb.append("&author=").append(computeString(str));
                        }
                    }

                    BrutalityCloudClient.Entry2 entry2 = computeEntry2("GET", sb.toString(), null, mineOnly);
                    if (!entry2.flag) {
                        callback.accept(new BrutalityCloudClient.Entry5(false, entry2.text2, null));
                        return;
                    }

                    List<BrutalityCloudClient.Entry3> entry3s = new ArrayList<>(computeList(entry2.text));
                    if (mineOnly) {
                        entry3s.removeIf(entry3 -> !matchesEntry3(entry3));
                    }

                    if (sort == BrutalityCloudClient.PopularMode.NAME_AZ) {
                        entry3s.sort((entry32, entry33) -> String.CASE_INSENSITIVE_ORDER.compare(entry32.name, entry33.name));
                    } else if (sort == BrutalityCloudClient.PopularMode.NAME_ZA) {
                        entry3s.sort((entry34, entry35) -> String.CASE_INSENSITIVE_ORDER.compare(entry35.name, entry34.name));
                    }

                    callback.accept(new BrutalityCloudClient.Entry5(true, null, entry3s));
                } catch (Exception e) {
                    callback.accept(new BrutalityCloudClient.Entry5(false, e.getMessage(), null));
                }
            }
        );
    }

    public static boolean matchesEntry3(BrutalityCloudClient.Entry3 item) {
        String str = b();
        if (str != null && !str.isEmpty() && item != null && item.author != null) {
            String str2 = item.author.trim();
            int n = str2.indexOf(35);
            if (n > 0) {
                str2 = str2.substring(0, n).trim();
            }

            return str.equalsIgnoreCase(str2);
        }
        return false;
    }

    public static void updateAsync(String itemId, String displayName, String description, File replaceFile, Consumer<BrutalityCloudClient.Entry> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!checkState()) {
                    callback.accept(new BrutalityCloudClient.Entry(false, "Login required", null));
                    return;
                }

                if (displayName == null || displayName.trim().isEmpty()) {
                    callback.accept(new BrutalityCloudClient.Entry(false, "Name required", null));
                    return;
                }

                JsonObject json = new JsonObject();
                json.addProperty("name", displayName.trim());
                json.addProperty("description", description == null ? "" : description);
                if (replaceFile != null && replaceFile.exists()) {
                    String str = new String(Files.readAllBytes(replaceFile.toPath()), StandardCharsets.UTF_8);
                    json.addProperty("fileName", replaceFile.getName());
                    json.addProperty("content", str);
                }

                BrutalityCloudClient.Entry2 entry2 = computeEntry2("PUT", "/cloud/items/" + computeString(itemId), json.toString(), true);
                if (!entry2.flag) {
                    callback.accept(new BrutalityCloudClient.Entry(false, entry2.text2, null));
                    return;
                }

                callback.accept(new BrutalityCloudClient.Entry(true, null, "Updated"));
            } catch (Exception e) {
                callback.accept(new BrutalityCloudClient.Entry(false, e.getMessage(), null));
            }
        });
    }

    public static void deleteAsync(String itemId, Consumer<BrutalityCloudClient.Entry> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!checkState()) {
                    callback.accept(new BrutalityCloudClient.Entry(false, "Login required", null));
                    return;
                }

                BrutalityCloudClient.Entry2 entry2 = computeEntry2("DELETE", "/cloud/items/" + computeString(itemId), null, true);
                if (!entry2.flag) {
                    callback.accept(new BrutalityCloudClient.Entry(false, entry2.text2, null));
                    return;
                }

                callback.accept(new BrutalityCloudClient.Entry(true, null, "Deleted"));
            } catch (Exception e) {
                callback.accept(new BrutalityCloudClient.Entry(false, e.getMessage(), null));
            }
        });
    }

    public static void uploadAsync(BrutalityCloudClient.ConfigMode kind, String displayName, String description, File localFile, Consumer<BrutalityCloudClient.Entry> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!checkState()) {
                    callback.accept(new BrutalityCloudClient.Entry(false, "Login required", null));
                    return;
                }

                if (localFile == null || !localFile.exists()) {
                    callback.accept(new BrutalityCloudClient.Entry(false, "File not found", null));
                    return;
                }

                String str = new String(Files.readAllBytes(localFile.toPath()), StandardCharsets.UTF_8);
                String str2 = computeString2();
                JsonObject json = new JsonObject();
                json.addProperty("type", kind == BrutalityCloudClient.ConfigMode.CONFIG ? "config" : "script");
                json.addProperty("name", displayName);
                json.addProperty("description", description == null ? "" : description);
                json.addProperty("fileName", localFile.getName());
                json.addProperty("content", str);
                json.addProperty("code", str2);
                json.addProperty("id", str2);
                BrutalityCloudClient.Entry2 entry2 = computeEntry2("POST", "/cloud/items", json.toString(), true);
                if (!entry2.flag) {
                    callback.accept(new BrutalityCloudClient.Entry(false, entry2.text2, null));
                    return;
                }

                String str3 = str2;

                try {
                    JsonObject asJsonObject = new JsonParser().parse(entry2.text).getAsJsonObject();
                    String str4 = computeString(computeString(asJsonObject, "code"), computeString(asJsonObject, "id"), computeString(asJsonObject, "shortId"));
                    if (!str4.isEmpty()) {
                        str3 = str4;
                    }
                } catch (Exception ignored) {
                }

                callback.accept(new BrutalityCloudClient.Entry(true, null, str3));
            } catch (Exception e) {
                callback.accept(new BrutalityCloudClient.Entry(false, e.getMessage(), null));
            }
        });
    }

    public static void handleEntry3(BrutalityCloudClient.Entry3 item, File destFile, Consumer<BrutalityCloudClient.Entry> callback) {
        if (item == null) {
            callback.accept(new BrutalityCloudClient.Entry(false, "Missing item", null));
        } else {
            downloadByIdAsync(item.computeString().isEmpty() ? item.id : item.computeString(), item.kind, destFile, callback);
        }
    }

    public static void downloadByIdAsync(String idOrCode, BrutalityCloudClient.ConfigMode kindHint, File destFile, Consumer<BrutalityCloudClient.Entry> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                if (idOrCode == null || idOrCode.trim().isEmpty()) {
                    callback.accept(new BrutalityCloudClient.Entry(false, "Missing id", null));
                    return;
                }

                String str = idOrCode.trim();
                BrutalityCloudClient.Entry2 entry2 = computeEntry2("GET", "/cloud/items/" + computeString(str) + "/download", null, false);
                if (!entry2.flag) {
                    BrutalityCloudClient.Entry2 entry22 = computeEntry2("GET", "/cloud/items?code=" + computeString(str), null, false);
                    if (entry22.flag) {
                        var entry3s = computeList(entry22.text);
                        if (!entry3s.isEmpty()) {
                            str = ((BrutalityCloudClient.Entry3)entry3s.get(0)).id;
                            entry2 = computeEntry2("GET", "/cloud/items/" + computeString(str) + "/download", null, false);
                        }
                    }
                }

                if (!entry2.flag) {
                    callback.accept(new BrutalityCloudClient.Entry(false, entry2.text2, null));
                    return;
                }

                JsonObject asJsonObject = new JsonParser().parse(entry2.text).getAsJsonObject();
                String str2 = asJsonObject.has("content") ? asJsonObject.get("content").getAsString() : null;
                if (str2 == null) {
                    callback.accept(new BrutalityCloudClient.Entry(false, "Missing content in response", null));
                    return;
                }

                File parentFile = destFile.getParentFile();
                if (parentFile != null && !parentFile.exists()) {
                    parentFile.mkdirs();
                }

                Files.write(destFile.toPath(), str2.getBytes(StandardCharsets.UTF_8));

                try {
                    computeEntry2("POST", "/cloud/items/" + computeString(str) + "/download", "{}", false);
                } catch (Exception ignored) {
                }

                callback.accept(new BrutalityCloudClient.Entry(true, null, "Saved to " + destFile.getName()));
            } catch (Exception e) {
                callback.accept(new BrutalityCloudClient.Entry(false, e.getMessage(), null));
            }
        });
    }

    public static String computeString2() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(14);

        for (int i = 0; i < 3; i++) {
            if (i > 0) {
                sb.append('-');
            }

            for (int j = 0; j < 4; j++) {
                sb.append("ABCDEFGHJKLMNPQRSTUVWXYZ23456789".charAt(random.nextInt("ABCDEFGHJKLMNPQRSTUVWXYZ23456789".length())));
            }
        }

        return sb.toString();
    }

    public static String computeString2(String id) {
        if (id == null) {
            return "cloud";
        }
        String str = id.trim().replace('-', '_').replaceAll("[^a-zA-Z0-9_]", "_");
        if (!str.isEmpty() && Character.isDigit(str.charAt(0))) {
            str = "c_" + str;
        }

        return str.isEmpty() ? "cloud" : str;
    }

    public static void rateAsync(String itemId, int stars, String comment, Consumer<BrutalityCloudClient.Entry> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!checkState()) {
                    callback.accept(new BrutalityCloudClient.Entry(false, "Login required to rate", null));
                    return;
                }

                int n = Math.max(1, Math.min(5, stars));
                JsonObject json = new JsonObject();
                json.addProperty("stars", n);
                json.addProperty("comment", comment == null ? "" : comment);
                String str = b();
                if (str != null) {
                    json.addProperty("author", str);
                    json.addProperty("username", str);
                    json.addProperty("brutality_username", str);
                }

                BrutalityCloudClient.Entry2 entry2 = computeEntry2("POST", "/cloud/items/" + computeString(itemId) + "/rate", json.toString(), true);
                if (!entry2.flag) {
                    callback.accept(new BrutalityCloudClient.Entry(false, entry2.text2, null));
                    return;
                }

                callback.accept(new BrutalityCloudClient.Entry(true, null, "Rated " + n + "/5"));
            } catch (Exception e) {
                callback.accept(new BrutalityCloudClient.Entry(false, e.getMessage(), null));
            }
        });
    }

    public static void reviewsAsync(String itemId, Consumer<List<BrutalityCloudClient.Entry4>> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                BrutalityCloudClient.Entry2 entry2 = computeEntry2("GET", "/cloud/items/" + computeString(itemId) + "/reviews", null, false);
                if (!entry2.flag) {
                    callback.accept(Collections.emptyList());
                    return;
                }

                callback.accept(computeList2(entry2.text));
            } catch (Exception e) {
                callback.accept(Collections.emptyList());
            }
        });
    }

    private static BrutalityCloudClient.Entry2 computeEntry2(String str, String str2, String str3, boolean flag) throws IOException {
        String str4 = null;

        try {
            str4 = BackendAuthService.getInstance().getSessionToken();
        } catch (Exception ignored) {
        }

        if (!flag || str4 != null && !str4.isEmpty()) {
            HttpURLConnection conn = null;

            BrutalityCloudClient.Entry2 entry2;
            try {
                URL url = new URL("https://brutality-backend-production.up.railway.app/api" + str2);
                if (ProxyManager.checkState() && ProxyManager.computeProxy() != null) {
                    conn = (HttpURLConnection)url.openConnection(ProxyManager.computeProxy());
                } else {
                    conn = (HttpURLConnection)url.openConnection();
                }

                PinnedTlsHttpClient.handleHttpURLConnection(conn);
                conn.setRequestMethod(str);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-API-Key", "brutality_client_api_key_2024_super_secret_32_chars_minimum");
                conn.setRequestProperty("User-Agent", "Brutality-Client/1.0");
                if (str4 != null && !str4.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + str4);
                }

                conn.setConnectTimeout(12000);
                conn.setReadTimeout(25000);
                if (str3 != null && !str3.isEmpty() && ("POST".equals(str) || "PUT".equals(str) || "PATCH".equals(str))) {
                    conn.setDoOutput(true);
                    byte[] bytes = str3.getBytes(StandardCharsets.UTF_8);

                    try (OutputStream outputStream = conn.getOutputStream()) {
                        outputStream.write(bytes);
                    }
                } else if ("DELETE".equals(str)) {
                    conn.setDoOutput(true);
                }

                int responseCode = conn.getResponseCode();
                InputStream in = responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream();
                String str5 = computeString(in);
                if (responseCode < 200 || responseCode >= 300) {
                    String str6 = computeString(str5, responseCode);
                    return new BrutalityCloudClient.Entry2(false, str5, str6);
                }

                entry2 = new BrutalityCloudClient.Entry2(true, str5, null);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return entry2;
        }
        return new BrutalityCloudClient.Entry2(false, null, "Not authenticated");
    }

    private static String computeString(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        String str;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();

            String str2;
            while ((str2 = reader.readLine()) != null) {
                sb.append(str2);
            }

            str = sb.toString();
        }

        return str;
    }

    private static String computeString(String str, int n) {
        try {
            JsonObject asJsonObject = new JsonParser().parse(str).getAsJsonObject();
            if (asJsonObject.has("message")) {
                return asJsonObject.get("message").getAsString();
            }

            if (asJsonObject.has("error")) {
                return asJsonObject.get("error").getAsString();
            }
        } catch (Exception ignored) {
        }

        return "HTTP " + n;
    }

    private static List<BrutalityCloudClient.Entry3> computeList(String str) {
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();

        try {
            JsonElement element = new JsonParser().parse(str);
            JsonArray jsonArray;
            if (element.isJsonArray()) {
                jsonArray = element.getAsJsonArray();
            } else {
                JsonObject asJsonObject = element.getAsJsonObject();
                jsonArray = asJsonObject.has("items") ? asJsonObject.getAsJsonArray("items") : new JsonArray();
            }

            for (JsonElement element2 : jsonArray) {
                JsonObject asJsonObject2 = element2.getAsJsonObject();
                BrutalityCloudClient.ConfigMode configMode = "script".equalsIgnoreCase(computeString(asJsonObject2, "type"))
                    ? BrutalityCloudClient.ConfigMode.SCRIPT
                    : BrutalityCloudClient.ConfigMode.CONFIG;
                String str2 = computeString(
                    computeString(asJsonObject2, "brutality_username"),
                    computeString(asJsonObject2, "brutalityUsername"),
                    computeString(asJsonObject2, "username"),
                    computeString(asJsonObject2, "author"),
                    computeString(asJsonObject2, "user")
                );
                String str3 = computeString(computeString(asJsonObject2, "id"), computeString(asJsonObject2, "code"), computeString(asJsonObject2, "shortId"));
                String str4 = computeString(computeString(asJsonObject2, "code"), computeString(asJsonObject2, "shortId"), computeString(asJsonObject2, "shareCode"), str3);
                copyOnWriteArrayList.add(
                    new BrutalityCloudClient.Entry3(
                        str3,
                        str4,
                        configMode,
                        computeString(asJsonObject2, "name"),
                        computeString(asJsonObject2, "description"),
                        str2.isEmpty() ? "?" : str2,
                        asJsonObject2.has("avgRating") ? asJsonObject2.get("avgRating").getAsDouble() : 0.0,
                        asJsonObject2.has("ratingCount") ? asJsonObject2.get("ratingCount").getAsInt() : 0,
                        asJsonObject2.has("downloads") ? asJsonObject2.get("downloads").getAsInt() : 0,
                        asJsonObject2.has("createdAt") ? asJsonObject2.get("createdAt").getAsLong() : 0L,
                        computeString(asJsonObject2, "fileName")
                    )
                );
            }
        } catch (Exception ignored) {
        }

        return copyOnWriteArrayList;
    }

    private static List<BrutalityCloudClient.Entry4> computeList2(String str) {
        List list = new ArrayList();

        try {
            JsonElement element = new JsonParser().parse(str);
            JsonArray jsonArray;
            if (element.isJsonArray()) {
                jsonArray = element.getAsJsonArray();
            } else {
                JsonObject asJsonObject = element.getAsJsonObject();
                jsonArray = asJsonObject.has("reviews") ? asJsonObject.getAsJsonArray("reviews") : new JsonArray();
            }

            for (JsonElement element2 : jsonArray) {
                JsonObject asJsonObject2 = element2.getAsJsonObject();
                String str2 = computeString(
                    computeString(asJsonObject2, "brutality_username"),
                    computeString(asJsonObject2, "brutalityUsername"),
                    computeString(asJsonObject2, "username"),
                    computeString(asJsonObject2, "author"),
                    computeString(asJsonObject2, "user")
                );
                list.add(
                    new BrutalityCloudClient.Entry4(
                        str2.isEmpty() ? "Unknown" : str2,
                        asJsonObject2.has("stars") ? asJsonObject2.get("stars").getAsInt() : 0,
                        computeString(asJsonObject2, "comment"),
                        asJsonObject2.has("createdAt") ? asJsonObject2.get("createdAt").getAsLong() : 0L
                    )
                );
            }
        } catch (Exception ignored) {
        }

        return list;
    }

    private static String computeString(JsonObject json, String str) {
        return json.has(str) && !json.get(str).isJsonNull() ? json.get(str).getAsString() : "";
    }

    private static String computeString(String... parts) {
        if (parts == null) {
            return "";
        }
        for (String str : parts) {
            if (str != null && !str.trim().isEmpty()) {
                return str.trim();
            }
        }

        return "";
    }

    private static String computeString(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }

    public static enum ConfigMode {
        CONFIG,
        SCRIPT;

        private static BrutalityCloudClient.ConfigMode[] computeConfigModeArray() {
            return new BrutalityCloudClient.ConfigMode[]{CONFIG, SCRIPT};
        }
    }

    public static final class Entry {
        public final boolean ok;
        public final String error;
        public final String message;

        public Entry(boolean ok, String error, String message) {
            this.ok = ok;
            this.error = error;
            this.message = message;
        }
    }

    public static final class Entry2 {
        public final boolean flag;
        public final String text;
        public final String text2;

        public Entry2(boolean flag, String text, String str) {
            this.flag = flag;
            this.text = text;
            this.text2 = str;
        }
    }

    public static final class Entry3 {
        public final String id;
        public final String code;
        public final BrutalityCloudClient.ConfigMode kind;
        public final String name;
        public final String description;
        public final String author;
        public final double avgRating;
        public final int ratingCount;
        public final int downloads;
        public final long createdAt;
        public final String fileName;

        public Entry3(
            String id, BrutalityCloudClient.ConfigMode kind, String name, String description, String author, double avgRating, int ratingCount, int downloads, long createdAt, String fileName
        ) {
            this(id, null, kind, name, description, author, avgRating, ratingCount, downloads, createdAt, fileName);
        }

        public Entry3(
            String id,
            String code,
            BrutalityCloudClient.ConfigMode kind,
            String name,
            String description,
            String author,
            double avgRating,
            int ratingCount,
            int downloads,
            long createdAt,
            String fileName
        ) {
            this.id = id;
            this.code = code;
            this.kind = kind;
            this.name = name;
            this.description = description;
            this.author = author;
            this.avgRating = avgRating;
            this.ratingCount = ratingCount;
            this.downloads = downloads;
            this.createdAt = createdAt;
            this.fileName = fileName;
        }

        public String computeString() {
            if (this.code != null && !this.code.trim().isEmpty()) {
                return this.code.trim();
            }
            return this.id != null ? this.id : "";
        }
    }

    public static final class Entry4 {
        public final String author;
        public final int stars;
        public final String comment;
        public final long createdAt;

        public Entry4(String author, int stars, String comment, long createdAt) {
            this.author = author;
            this.stars = stars;
            this.comment = comment;
            this.createdAt = createdAt;
        }
    }

    public static final class Entry5 {
        public final boolean ok;
        public final String error;
        public final List<BrutalityCloudClient.Entry3> entries;

        public Entry5(boolean ok, String error, List<BrutalityCloudClient.Entry3> items) {
            this.ok = ok;
            this.error = error;
            this.entries = items != null ? items : Collections.emptyList();
        }
    }

    public static enum PopularMode {
        POPULAR("popular", "Popular"),
        NEWEST("newest", "Newest"),
        TOP_RATED("top_rated", "Top Rated"),
        DOWNLOADS("downloads", "Downloads"),
        NAME_AZ("name_az", "A-Z"),
        NAME_ZA("name_za", "Z-A");

        public final String api;
        public final String label;

        private PopularMode(String api, String label) {
            this.api = api;
            this.label = label;
        }

        private static BrutalityCloudClient.PopularMode[] computePopularModeArray() {
            return new BrutalityCloudClient.PopularMode[]{POPULAR, NEWEST, TOP_RATED, DOWNLOADS, NAME_AZ, NAME_ZA};
        }
    }
}
