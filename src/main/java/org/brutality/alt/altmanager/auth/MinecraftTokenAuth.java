package org.brutality.alt.altmanager.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import net.minecraft.util.Session;

public final class MinecraftTokenAuth {
    private static final String text = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:127.0) Gecko/20100101 Firefox/127.0";
    private static final int count = 15000;
    private static final int count2 = 20000;
    private static volatile Consumer<String> consumer = null;

    public static MinecraftTokenAuth.Entry computeEntry(String str) {
        String str2 = computeString("https://api.minecraftservices.com/minecraft/profile", str);
        if (str2 == null) {
            return MinecraftTokenAuth.Entry.computeEntry("Not a Minecraft access token");
        }
        JsonObject json;
        try {
            json = new JsonParser().parse(str2).getAsJsonObject();
        } catch (Exception e) {
            return MinecraftTokenAuth.Entry.computeEntry("Bad profile response");
        }

        if (json.has("id") && json.has("name")) {
            String asString = json.get("id").getAsString();
            String asString2 = json.get("name").getAsString();
            SessionHolder.setSession(new Session(asString2, asString, str, "mojang"));
            return MinecraftTokenAuth.Entry.computeEntry(asString2, asString, str);
        }
        return MinecraftTokenAuth.Entry.computeEntry("Account owns no Minecraft profile");
    }

    private static void process() {
        try {
            SSLContext instance = SSLContext.getInstance("TLSv1.2");
            instance.init(null, null, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(instance.getSocketFactory());
        } catch (Exception ignored) {
        }
    }

    public static MinecraftTokenAuth.Entry computeEntry3(String str) {
        String str2 = "client_id=00000000402b5328&grant_type=refresh_token&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf&refresh_token="
            + str
            + "&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL";
        String str3 = computeString("https://login.live.com/oauth20_token.srf", str2, false);
        if (str3 == null) {
            return MinecraftTokenAuth.Entry.computeEntry("Xbox Live auth failed");
        }
        String str4;
        try {
            str4 = new JsonParser().parse(str3).getAsJsonObject().get("access_token").getAsString();
        } catch (Exception e) {
            return MinecraftTokenAuth.Entry.computeEntry("Xbox Live auth failed");
        }

        return computeEntry(str4, "t");
    }

    public static MinecraftTokenAuth.Entry computeEntry2(String str) {
        if (str != null && !str.trim().isEmpty()) {
            String str2 = str.trim();
            handleString("Trying access token...");
            MinecraftTokenAuth.Entry entry = computeEntry(str2);
            if (entry.flag) {
                return entry;
            }
            handleString("Trying Xbox Live session token...");
            MinecraftTokenAuth.Entry entry2 = computeEntry4(str2);
            if (entry2.flag) {
                return entry2;
            }
            handleString("Trying refresh token...");
            MinecraftTokenAuth.Entry entry3 = computeEntry3(str2);
            if (entry3.flag) {
                return entry3;
            }
            return entry3.error != null ? entry3 : MinecraftTokenAuth.Entry.computeEntry(entry2.error != null ? entry2.error : entry.error);
        }
        return MinecraftTokenAuth.Entry.computeEntry("Empty token");
    }

    public static void setConsumer(Consumer<String> consumer2) {
        consumer = consumer2;
    }

    private static boolean matchesString(String str) {
        String str2 = computeString("https://api.minecraftservices.com/entitlements/mcstore", str);
        if (str2 == null) {
            return false;
        }
        try {
            JsonObject asJsonObject = new JsonParser().parse(str2).getAsJsonObject();
            if (!asJsonObject.has("items")) {
                return false;
            }
            boolean flag = false;
            boolean flag2 = false;

            for (JsonElement element : asJsonObject.getAsJsonArray("items")) {
                String asString = element.getAsJsonObject().get("name").getAsString();
                if ("product_minecraft".equals(asString)) {
                    flag = true;
                } else if ("game_minecraft".equals(asString)) {
                    flag2 = true;
                }
            }

            return flag && flag2;
        } catch (Exception e) {
            return false;
        }
    }

    public static MinecraftTokenAuth.Entry computeEntry4(String str) {
        MinecraftTokenAuth.Entry entry = computeEntry(str, "d");
        return entry.flag ? entry : computeEntry(str, "t");
    }

    private static MinecraftTokenAuth.Entry computeEntry(String str, String str2) {
        String str3 = "{\"Properties\":{\"AuthMethod\":\"RPS\",\"SiteName\":\"user.auth.xboxlive.com\",\"RpsTicket\":\""
            + str2
            + "="
            + str
            + "\"},\"RelyingParty\":\"http://auth.xboxlive.com\",\"TokenType\":\"JWT\"}";
        handleString("Authenticating with Xbox Live...");
        String str4 = computeString("https://user.auth.xboxlive.com/user/authenticate", str3, true);
        if (str4 == null) {
            return MinecraftTokenAuth.Entry.computeEntry("Xbox Live auth failed");
        }
        String str5;
        String str6;
        try {
            JsonObject asJsonObject = new JsonParser().parse(str4).getAsJsonObject();
            str5 = asJsonObject.get("Token").getAsString();
            str6 = asJsonObject.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString();
        } catch (Exception e) {
            return MinecraftTokenAuth.Entry.computeEntry("Xbox Live auth failed");
        }

        String str7 = "{\"Properties\":{\"SandboxId\":\"RETAIL\",\"UserTokens\":[\""
            + str5
            + "\"]},\"RelyingParty\":\"rp://api.minecraftservices.com/\",\"TokenType\":\"JWT\"}";
        handleString("Requesting XSTS token...");
        String str8 = computeString("https://xsts.auth.xboxlive.com/xsts/authorize", str7, true);
        if (str8 == null) {
            return MinecraftTokenAuth.Entry.computeEntry("XSTS auth failed");
        }
        String str9;
        try {
            str9 = new JsonParser().parse(str8).getAsJsonObject().get("Token").getAsString();
        } catch (Exception e2) {
            return MinecraftTokenAuth.Entry.computeEntry("XSTS auth failed");
        }

        String str10 = "{\"identityToken\":\"XBL3.0 x=" + str6 + ";" + str9 + "\"}";
        handleString("Logging into Minecraft...");
        String str11 = computeString("https://api.minecraftservices.com/authentication/login_with_xbox", str10, true);
        if (str11 == null) {
            return MinecraftTokenAuth.Entry.computeEntry("Minecraft auth failed");
        }
        String str12;
        try {
            str12 = new JsonParser().parse(str11).getAsJsonObject().get("access_token").getAsString();
        } catch (Exception e3) {
            return MinecraftTokenAuth.Entry.computeEntry("Minecraft auth failed");
        }

        return !matchesString(str12) ? MinecraftTokenAuth.Entry.computeEntry("Account does not own Minecraft") : computeEntry(str12);
    }

    private static String computeString(String str, String str2, boolean flag) {
        try {
            process();
            HttpURLConnection conn = (HttpURLConnection)new URL(str).openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(20000);
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:127.0) Gecko/20100101 Firefox/127.0");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            byte[] bytes = str2.getBytes(StandardCharsets.UTF_8);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.addRequestProperty("Content-Type", flag ? "application/json" : "application/x-www-form-urlencoded; charset=UTF-8");
            conn.addRequestProperty("Accept", "application/json");
            conn.connect();

            try (OutputStream outputStream = conn.getOutputStream()) {
                outputStream.write(bytes);
            }

            if (conn.getResponseCode() != 200) {
                conn.disconnect();
                return null;
            }
            return computeString(conn.getInputStream());
        } catch (Exception e) {
            return null;
        }
    }

    private MinecraftTokenAuth() {
    }

    private static String computeString(InputStream in) throws Exception {
        if (in == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        String str;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
        }

        return sb.toString();
    }

    private static void handleString(String str) {
        Consumer callback = consumer;
        if (callback != null) {
            try {
                callback.accept(str);
            } catch (Exception ignored) {
            }
        }
    }

    private static String computeString(String str, String str2) {
        try {
            process();
            HttpURLConnection conn = (HttpURLConnection)new URL(str).openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(20000);
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:127.0) Gecko/20100101 Firefox/127.0");
            conn.addRequestProperty("Accept", "application/json");
            conn.addRequestProperty("Authorization", "Bearer " + str2);
            conn.connect();
            if (conn.getResponseCode() != 200) {
                conn.disconnect();
                return null;
            }
            return computeString(conn.getInputStream());
        } catch (Exception e) {
            return null;
        }
    }

    public static class Entry {
        public boolean flag;
        public String username;
        public String text;
        public String text2;
        public String error;

        public static MinecraftTokenAuth.Entry computeEntry(String str) {
            MinecraftTokenAuth.Entry entry = new MinecraftTokenAuth.Entry();
            entry.flag = false;
            entry.error = str;
            return entry;
        }

        public static MinecraftTokenAuth.Entry computeEntry(String str, String str2, String str3) {
            MinecraftTokenAuth.Entry entry = new MinecraftTokenAuth.Entry();
            entry.flag = true;
            entry.username = str;
            entry.text = str2;
            entry.text2 = str3;
            return entry;
        }
    }
}
