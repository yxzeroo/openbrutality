package org.brutality.system.stafftracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.brutality.bootstrap.PinnedTlsHttpClient;
import org.brutality.proxy.ProxyManager;
import org.brutality.system.BackendAuthService;

public final class StaffTrackerService {
    private StaffTrackerService() {
    }

    public static void handleConsumer(Consumer<StaffTrackerService.Entry> callback) {
        fetchAsync(false, callback);
    }

    public static void fetchAsync(boolean classicPit, Consumer<StaffTrackerService.Entry> callback) {
        CompletableFuture.runAsync(() -> callback.accept(computeEntry(classicPit)));
    }

    public static StaffTrackerService.Entry computeEntry() {
        return computeEntry(false);
    }

    public static StaffTrackerService.Entry computeEntry(boolean classicPit) {
        try {
            String str = null;

            try {
                str = BackendAuthService.getInstance().getSessionToken();
            } catch (Exception ignored) {
            }

            HttpURLConnection conn = null;

            StaffTrackerService.Entry entry;
            try {
                URL url = new URL("https://brutality-backend-production.up.railway.app/api" + (classicPit ? "/staff-tracker/classic" : "/staff-tracker"));
                if (ProxyManager.checkState() && ProxyManager.computeProxy() != null) {
                    conn = (HttpURLConnection)url.openConnection(ProxyManager.computeProxy());
                } else {
                    conn = (HttpURLConnection)url.openConnection();
                }

                PinnedTlsHttpClient.handleHttpURLConnection(conn);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-API-Key", "brutality_client_api_key_2024_super_secret_32_chars_minimum");
                conn.setRequestProperty("User-Agent", "Brutality-Client/1.0");
                if (str != null && !str.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + str);
                }

                conn.setConnectTimeout(12000);
                conn.setReadTimeout(20000);
                int responseCode = conn.getResponseCode();
                InputStream in = responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream();
                String str2 = computeString(in);
                if (responseCode < 200 || responseCode >= 300) {
                    return new StaffTrackerService.Entry(false, computeString(str2, responseCode), 0L, null);
                }

                entry = parseSnapshot(str2);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return entry;
        } catch (Exception e) {
            return new StaffTrackerService.Entry(false, e.getMessage() != null ? e.getMessage() : "Request failed", 0L, null);
        }
    }

    private static StaffTrackerService.Entry parseSnapshot(String str) {
        try {
            JsonObject asJsonObject = new JsonParser().parse(str).getAsJsonObject();
            if (asJsonObject.has("success") && asJsonObject.get("success").isJsonPrimitive() && !asJsonObject.get("success").getAsBoolean()) {
                String str2 = asJsonObject.has("message")
                    ? asJsonObject.get("message").getAsString()
                    : (asJsonObject.has("error") ? asJsonObject.get("error").getAsString() : "Backend error");
                return new StaffTrackerService.Entry(false, str2, 0L, null);
            }
            long l = 0L;
            if (asJsonObject.has("updatedAt") && asJsonObject.get("updatedAt").isJsonPrimitive()) {
                l = asJsonObject.get("updatedAt").getAsLong();
            } else if (asJsonObject.has("updated_at") && asJsonObject.get("updated_at").isJsonPrimitive()) {
                l = asJsonObject.get("updated_at").getAsLong();
            }

            if (l > 0L && l < 10000000000L) {
                l *= 1000L;
            }

            JsonArray jsonArray = null;
            if (asJsonObject.has("staff") && asJsonObject.get("staff").isJsonArray()) {
                jsonArray = asJsonObject.getAsJsonArray("staff");
            } else if (asJsonObject.has("players") && asJsonObject.get("players").isJsonArray()) {
                jsonArray = asJsonObject.getAsJsonArray("players");
            }

            ArrayList list = new ArrayList();
            if (jsonArray != null) {
                for (JsonElement element : jsonArray) {
                    if (element.isJsonObject()) {
                        JsonObject asJsonObject2 = element.getAsJsonObject();
                        String str3 = computeString(asJsonObject2, "name", computeString(asJsonObject2, "username", "?"));
                        String str4 = computeString(asJsonObject2, "status", null);
                        String str5 = computeString(asJsonObject2, "detail", computeString(asJsonObject2, "note", computeString(asJsonObject2, "message", null)));
                        String str6 = computeString(asJsonObject2, "rank", computeString(asJsonObject2, "role", null));
                        StaffTrackerService.OnlineMode onlineMode = StaffTrackerService.OnlineMode.computeOnlineMode(str4);
                        if (str4 == null && str5 != null) {
                            onlineMode = StaffTrackerService.OnlineMode.computeOnlineMode(str5);
                        }

                        list.add(new StaffTrackerService.Entry2(str3, onlineMode, StaffTrackerService.AdminMode.computeAdminMode(str6), str5));
                    }
                }
            }

            list.sort(
                Comparator.<StaffTrackerService.Entry2>comparingInt(entry2 -> entry2.onlineMode == StaffTrackerService.OnlineMode.ONLINE ? 0 : 1)
                    .thenComparingInt(entry22 -> entry22.adminMode.computeInt())
                    .thenComparing(entry23 -> entry23.text, String.CASE_INSENSITIVE_ORDER)
            );
            return new StaffTrackerService.Entry(true, null, l, list);
        } catch (Exception e) {
            return new StaffTrackerService.Entry(false, "Bad response", 0L, null);
        }
    }

    private static String computeString(JsonObject json, String str, String str2) {
        if (json != null && json.has(str) && !json.get(str).isJsonNull()) {
            try {
                return json.get(str).getAsString();
            } catch (Exception e) {
                return str2;
            }
        } else {
            return str2;
        }
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

    private static String computeString(InputStream in) throws Exception {
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

    public static enum AdminMode {
        ADMIN,
        GM,
        NONE;

        public static StaffTrackerService.AdminMode computeAdminMode(String raw) {
            if (raw != null && !raw.trim().isEmpty()) {
                String str = raw.trim().toLowerCase();
                if (str.equals("admin") || str.equals("administrator")) {
                    return ADMIN;
                }
                return !str.equals("gm") && !str.equals("general_manager") && !str.equals("general-manager") ? NONE : GM;
            }
            return NONE;
        }

        public int computeInt() {
            switch (this) {
                case ADMIN:
                    return 0;
                case GM:
                    return 1;
                default:
                    return 2;
            }
        }

        private static StaffTrackerService.AdminMode[] computeAdminModeArray() {
            return new StaffTrackerService.AdminMode[]{ADMIN, GM, NONE};
        }
    }

    public static final class Entry {
        public final boolean ok;
        public final String error;
        public final long updatedAtMs;
        public final List<StaffTrackerService.Entry2> entries;

        public Entry(boolean ok, String error, long updatedAtMs, List<StaffTrackerService.Entry2> staff) {
            this.ok = ok;
            this.error = error;
            this.updatedAtMs = updatedAtMs;
            this.entries = staff != null ? staff : Collections.emptyList();
        }
    }

    public static final class Entry2 {
        public final String text;
        public final StaffTrackerService.OnlineMode onlineMode;
        public final StaffTrackerService.AdminMode adminMode;
        public final String detail;

        public Entry2(String name, StaffTrackerService.OnlineMode status, StaffTrackerService.AdminMode rank, String detail) {
            this.text = name != null ? name : "?";
            this.onlineMode = status != null ? status : StaffTrackerService.OnlineMode.UNKNOWN;
            this.adminMode = rank != null ? rank : StaffTrackerService.AdminMode.NONE;
            this.detail = detail;
        }

        public String computeString() {
            if (this.onlineMode == StaffTrackerService.OnlineMode.ERROR) {
                return "API FAILED";
            }
            if (this.onlineMode == StaffTrackerService.OnlineMode.NOT_FOUND) {
                return "Offline";
            }
            return this.onlineMode == StaffTrackerService.OnlineMode.UNKNOWN ? "API FAILED" : this.onlineMode.computeString();
        }
    }

    public static enum OnlineMode {
        ONLINE,
        OFFLINE,
        NOT_FOUND,
        ERROR,
        UNKNOWN;

        public static StaffTrackerService.OnlineMode computeOnlineMode(String raw) {
            if (raw == null) {
                return UNKNOWN;
            }
            String str = raw.trim().toUpperCase().replace(' ', '_');
            if (str.contains("ONLINE") && !str.contains("OFF")) {
                return ONLINE;
            }
            if (str.contains("OFFLINE")) {
                return OFFLINE;
            }
            if (str.contains("NOT_FOUND") || str.contains("NOT FOUND")) {
                return NOT_FOUND;
            }
            if (str.contains("ERROR")) {
                return ERROR;
            }
            try {
                return valueOf(str);
            } catch (Exception e) {
                return UNKNOWN;
            }
        }

        public String computeString() {
            switch (this) {
                case ONLINE:
                    return "Online";
                case OFFLINE:
                    return "Offline";
                case NOT_FOUND:
                    return "Not Found";
                case ERROR:
                    return "Error";
                default:
                    return "Unknown";
            }
        }

        private static StaffTrackerService.OnlineMode[] computeOnlineModeArray() {
            return new StaffTrackerService.OnlineMode[]{ONLINE, OFFLINE, NOT_FOUND, ERROR, UNKNOWN};
        }
    }
}
