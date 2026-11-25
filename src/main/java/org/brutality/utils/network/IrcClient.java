package org.brutality.utils.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.alt.altmanager.AccountManager;
import org.brutality.alt.altmanager.auth.AccountSession;
import org.brutality.module.impl.client.Cape;
import org.brutality.system.BackendAuthService;
import org.brutality.utils.modules.CapeModeHelper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class IrcClient {
    private static final String text = "wss://brutality-irc-production.up.railway.app";
    private static final String text2 = "https://brutality-irc-production.up.railway.app";
    public static volatile WebSocketClient webSocketClient;
    public static volatile boolean connected = false;
    public static volatile boolean flag = false;
    private static volatile boolean flag2 = false;
    public static volatile String currentUsername;
    public static volatile String text3;
    private static final int count = 10000;
    public static final int count2 = 5000;
    private static final long timestamp = 60000L;
    private static long timestamp2 = 0L;
    private static long timestamp3 = 0L;
    public static int count3 = 0;
    public static long timestamp4 = 5000L;
    public static final BlockingQueue<JsonObject> blockingQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<String> blockingQueue2 = new LinkedBlockingQueue<>();
    private static final Gson gson = new Gson();
    private static final Map<String, String> lookupMap = new ConcurrentHashMap<>();
    private static volatile long timestamp5 = 0L;
    private static final long timestamp6 = 60000L;
    private static final String text4 = "https://brutality-backend-production.up.railway.app/api/irc/config";

    private static String computeString(JsonObject json) {
        JsonElement element = json.get("rank");
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString() ? element.getAsString() : null;
    }

    private static void handleJsonObject6(JsonObject json) {
        Minecraft mc = Minecraft.getMinecraft();
        String str = computeString3();
        if (str != null && !str.trim().isEmpty()) {
            json.addProperty("ingame_username", str);
        }

        UUID uuid = mc != null && mc.thePlayer != null
            ? mc.thePlayer.getUniqueID()
            : (mc != null && mc.getSession() != null ? mc.getSession().getProfile().getId() : null);
        if (uuid != null) {
            json.addProperty("minecraft_uuid", uuid.toString());
        }

        json.addProperty("cape_mode", Cape.computeString());
    }

    public static boolean checkState() {
        if (isConnected() && flag2) {
            JsonObject json = new JsonObject();
            handleJsonObject6(json);
            String str = json.toString();
            if (str.equals(text3)) {
                return false;
            }
            text3 = str;
            sendPing();
            return true;
        }
        return false;
    }

    public static void handleJsonObject11(JsonObject json) {
        if (json.has("ranks") && json.get("ranks").isJsonObject()) {
            handleJsonObject5(json.getAsJsonObject("ranks"));
        }
    }

    public static void handleString2(String str, String str2) {
        if (connected && webSocketClient != null && webSocketClient.isOpen()) {
            try {
                if (!flag2) {
                    blockingQueue2.offer(computeString(str, str2));
                    process5();
                    return;
                }

                JsonObject json = new JsonObject();
                json.addProperty("type", "message");
                json.addProperty("username", str);
                json.addProperty("message", str2);
                json.addProperty("timestamp", System.currentTimeMillis());
                handleJsonObject10(json);
                webSocketClient.send(gson.toJson((JsonElement)json));
            } catch (Exception ignored) {
            }
        } else {
            try {
                JsonObject json2 = new JsonObject();
                json2.addProperty("type", "message");
                json2.addProperty("username", str);
                json2.addProperty("message", str2);
                json2.addProperty("timestamp", System.currentTimeMillis());
                handleJsonObject10(json2);
                blockingQueue2.offer(gson.toJson((JsonElement)json2));
                process();
            } catch (Exception ignored2) {
            }
        }
    }

    public static void handleJsonObject3(JsonObject json) {
    }

    public static void process3() {
        if (connected && webSocketClient != null && webSocketClient.isOpen()) {
            try {
                JsonObject json = new JsonObject();
                json.addProperty("type", "user_count");
                webSocketClient.send(gson.toJson((JsonElement)json));
            } catch (Exception ignored) {
            }
        }
    }

    public static void process2() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            String str;
            while ((str = blockingQueue2.poll()) != null) {
                try {
                    if (!flag2) {
                        blockingQueue2.offer(str);
                        process5();
                        break;
                    }

                    webSocketClient.send(str);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    public static void handleJsonObject8(JsonObject json) {
        JsonElement element = json.get("capes");
        if (element != null && element.isJsonArray()) {
            CapeModeHelper.instance.handleJsonArray(element.getAsJsonArray());
        }
    }

    public static void handleString(String str, String str2) {
        if (str != null) {
            String str3 = computeString2(str2);
            if ("none".equals(str3)) {
                lookupMap.remove(str.toLowerCase());
            } else {
                lookupMap.put(str.toLowerCase(), str3);
            }
        }
    }

    public static void handleJsonObject4(JsonObject json) {
        String str = json.has("username") ? json.get("username").getAsString() : "Unknown";
        if (json.has("rank")) {
            handleString(str, computeString(json));
        }

        if (str.equals(currentUsername)) {
            flag2 = true;
        }
    }

    private static void fetchRanksIfStale() {
        long now = System.currentTimeMillis();
        if (now - timestamp5 >= 60000L) {
            timestamp5 = now;
            CompletableFuture.runAsync(() -> {
                ConcurrentHashMap map = new ConcurrentHashMap();
                handleString("https://brutality-backend-production.up.railway.app/api/irc/config", map);
                ConcurrentHashMap map2 = new ConcurrentHashMap();
                handleString("https://brutality-irc-production.up.railway.app/api/ranks", map2);

                for (Entry entry : ((Iterable<Entry>)(Object)(map2.entrySet()))) {
                    map.putIfAbsent((String)entry.getKey(), (String)entry.getValue());
                }

                if (!map.isEmpty()) {
                    lookupMap.clear();
                    lookupMap.putAll(map);
                }
            });
        }
    }

    public static void process() {
        long now = System.currentTimeMillis();
        if (!connected && !flag && currentUsername != null && now - timestamp2 >= timestamp4) {
            timestamp2 = now;
            connect(currentUsername);
        }
    }

    public static void sendPing() {
        if (connected && webSocketClient != null && webSocketClient.isOpen()) {
            try {
                JsonObject json = new JsonObject();
                json.addProperty("type", "ping");
                json.addProperty("username", currentUsername);
                handleJsonObject10(json);
                webSocketClient.send(gson.toJson((JsonElement)json));
                fetchRanksIfStale();
            } catch (Exception ignored) {
            }
        }
    }

    private static String computeString3() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.thePlayer != null) {
                return mc.thePlayer.getName();
            }

            if (mc != null && mc.getSession() != null && mc.getSession().getUsername() != null) {
                return mc.getSession().getUsername();
            }

            if (mc != null && mc.thePlayer != null) {
                return mc.thePlayer.getName();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static void handleJsonObject9(JsonObject json) {
        if (json != null) {
            String str = computeString();
            if (str != null) {
                JsonObject json2 = new JsonObject();
                json2.addProperty("type", "party");
                json2.addProperty("username", str);
                json2.addProperty("timestamp", System.currentTimeMillis());
                json2.add("party", json);
                handleJsonObject10(json2);
                if (connected && webSocketClient != null && webSocketClient.isOpen()) {
                    try {
                        if (!flag2) {
                            blockingQueue2.offer(gson.toJson((JsonElement)json2));
                            process5();
                            return;
                        }

                        webSocketClient.send(gson.toJson((JsonElement)json2));
                    } catch (Exception ignored) {
                    }
                } else {
                    blockingQueue2.offer(gson.toJson((JsonElement)json2));
                    process();
                }
            }
        }
    }

    public static void process5() {
        long now = System.currentTimeMillis();
        if (connected && webSocketClient != null && webSocketClient.isOpen()) {
            if (now - timestamp3 >= 2000L) {
                timestamp3 = now;

                try {
                    JsonObject json = new JsonObject();
                    json.addProperty("type", "join");
                    json.addProperty("username", currentUsername);
                    handleJsonObject10(json);
                    String sessionToken = BackendAuthService.getInstance().getSessionToken();
                    if (sessionToken != null) {
                        json.addProperty("auth_token", sessionToken);
                    }

                    webSocketClient.send(gson.toJson((JsonElement)json));
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void handleJsonObject7(JsonObject json) {
        String str = json.has("username") ? json.get("username").getAsString() : "Unknown";
        if (json.has("rank")) {
            handleString(str, computeString(json));
        }

        if (!str.equals(currentUsername)) {
            blockingQueue.offer(json);
        }
    }

    private static String computeString(String str, String str2) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "message");
        json.addProperty("username", str);
        json.addProperty("message", str2);
        json.addProperty("timestamp", System.currentTimeMillis());
        handleJsonObject10(json);
        return gson.toJson((JsonElement)json);
    }

    private static String computeString2() {
        try {
            AccountSession accountSession = AccountManager.getAccountSession();
            if (accountSession != null && accountSession.getUsername() != null) {
                return accountSession.getUsername();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static void handleJsonObject(JsonObject json) {
        handleJsonObject8(json);
        String str = json.has("message") ? json.get("message").getAsString() : "";
        if (str.toLowerCase().contains("joined") || str.toLowerCase().contains("successfully joined")) {
            flag2 = true;
        }

        if (json.has("ranks") && json.get("ranks").isJsonObject()) {
            handleJsonObject5(json.getAsJsonObject("ranks"));
        }

        if (json.has("username") && json.has("rank")) {
            handleString(json.get("username").getAsString(), computeString(json));
        }

        JsonObject json2 = new JsonObject();
        json2.addProperty("type", "system");
        json2.addProperty("message", str);
        blockingQueue.offer(json2);
    }

    public static void connect(String str) {
        try {
            if (isConnected() || flag) {
                return;
            }

            flag = true;
            flag2 = false;
            text3 = null;
            CapeModeHelper.instance.clear();
            String str2 = null;

            try {
                // VENDOR-DISABLED: Brutality.getAuthHandler().getUsername()
            } catch (Exception ignored) {
            }

            if (str2 == null) {
                BackendAuthService instance = BackendAuthService.getInstance();
                str2 = instance.getUsername();
            }

            if (str2 == null || str2.trim().isEmpty()) {
                str2 = str;
            }

            currentUsername = str2;
            timestamp5 = 0L;
            fetchRanksIfStale();
            URI uri = new URI("wss://brutality-irc-production.up.railway.app");
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onClose(int n, String str3, boolean flag3) {
                    if (IrcClient.webSocketClient == this) {
                        CapeModeHelper.instance.clear();
                        IrcClient.text3 = null;
                        IrcClient.connected = false;
                        IrcClient.flag = false;
                        IrcClient.count3++;
                        IrcClient.timestamp4 = Math.min(60000L, 5000L * (1L << Math.min(IrcClient.count3, 4)));
                        if (n != 1000 && IrcClient.currentUsername != null) {
                            IrcClient.process();
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (IrcClient.webSocketClient == this) {
                        CapeModeHelper.instance.clear();
                        IrcClient.text3 = null;
                        IrcClient.connected = false;
                        IrcClient.flag = false;
                        IrcClient.count3++;
                        IrcClient.timestamp4 = Math.min(60000L, 5000L * (1L << Math.min(IrcClient.count3, 4)));
                    }
                }

                @Override
                public void onMessage(String str4) {
                    if (IrcClient.webSocketClient == this) {
                        try {
                            JsonObject asJsonObject = new JsonParser().parse(str4).getAsJsonObject();
                            String str5 = asJsonObject.has("type") ? asJsonObject.get("type").getAsString() : "unknown";
                            switch (str5) {
                                case "message":
                                    IrcClient.handleJsonObject7(asJsonObject);
                                    break;
                                case "join":
                                    IrcClient.handleJsonObject4(asJsonObject);
                                    break;
                                case "leave":
                                    IrcClient.handleJsonObject3(asJsonObject);
                                    break;
                                case "system":
                                    IrcClient.handleJsonObject(asJsonObject);
                                    break;
                                case "ranks":
                                    IrcClient.handleJsonObject11(asJsonObject);
                                    break;
                                case "error":
                                    IrcClient.handleJsonObject2(asJsonObject);
                                    break;
                                case "capes":
                                    IrcClient.handleJsonObject8(asJsonObject);
                                    break;
                                case "pong":
                                    IrcClient.handleJsonObject8(asJsonObject);
                            }
                        } catch (Exception e2) {
                            JsonObject json = new JsonObject();
                            json.addProperty("type", "raw");
                            json.addProperty("message", str4);
                            IrcClient.blockingQueue.offer(json);
                        }
                    }
                }

                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    if (IrcClient.webSocketClient != this) {
                        this.close();
                    } else {
                        IrcClient.connected = true;
                        IrcClient.flag = false;
                        IrcClient.count3 = 0;
                        IrcClient.timestamp4 = 5000L;

                        try {
                            IrcClient.process5();
                            IrcClient.process2();
                        } catch (Exception ignored2) {
                        }
                    }
                }
            };
            CompletableFuture.runAsync(() -> {
                try {
                    webSocketClient.connect();
                } catch (Exception e3) {
                    connected = false;
                    flag = false;
                }
            });
        } catch (Exception e4) {
            connected = false;
            flag = false;
        }
    }

    public static String computeString2(String str) {
        if (str != null && !str.trim().isEmpty()) {
            String str2 = str.trim().toLowerCase();
            if ("developer".equals(str2) || "dev".equals(str2) || "admin".equals(str2)) {
                return "admin";
            }
            if ("media".equals(str2)) {
                return "media";
            }
            if ("friend".equals(str2)) {
                return "friend";
            }
            return "none".equals(str2) ? "none" : "none";
        }
        return "none";
    }

    public static boolean isConnected() {
        return connected && webSocketClient != null && webSocketClient.isOpen();
    }

    private static void handleJsonObject10(JsonObject json) {
        handleJsonObject6(json);
        String str = computeString3();
        if (str != null && !str.trim().isEmpty()) {
            json.addProperty("ingame_username", str);
        }

        String str2 = computeString2();
        if (str2 != null && !str2.trim().isEmpty()) {
            json.addProperty("alt_username", str2);
        }

        Integer integer = computeInteger();
        if (integer != null) {
            json.addProperty("buyer_count", integer);
        }
    }

    public static String computeString(String str) {
        return str == null ? "none" : computeString2(lookupMap.getOrDefault(str.toLowerCase(), "none"));
    }

    public static JsonObject computeJsonObject() {
        return blockingQueue.poll();
    }

    public static Integer computeInteger() {
        try {
            return BackendAuthService.getInstance().getInteger();
        } catch (Exception e) {
            return null;
        }
    }

    private static void handleString(String str, Map<String, String> map) {
        try {
            URL url = new URL(str);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                return;
            }

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String str2;
            try {
                while ((str2 = reader.readLine()) != null) {
                    sb.append(str2);
                }
            } catch (Throwable e) {
                try {
                    reader.close();
                } catch (Throwable e2) {
                    e.addSuppressed(e2);
                }

                throw e;
            }

            reader.close();
            JsonObject asJsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
            if (!asJsonObject.has("ranks") || !asJsonObject.get("ranks").isJsonObject()) {
                return;
            }

            JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("ranks");

            for (Entry entry : asJsonObject2.entrySet()) {
                String str3 = computeString2(((JsonElement)entry.getValue()).getAsString());
                if (!"none".equals(str3)) {
                    map.put(((String)entry.getKey()).toLowerCase(), str3);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void handleJsonObject5(JsonObject json) {
        if (json != null) {
            for (Entry entry : json.entrySet()) {
                try {
                    String str = computeString2(((JsonElement)entry.getValue()).getAsString());
                    if ("none".equals(str)) {
                        lookupMap.remove(((String)entry.getKey()).toLowerCase());
                    } else {
                        lookupMap.put(((String)entry.getKey()).toLowerCase(), str);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void handleJsonObject2(JsonObject json) {
        String str = json.has("error") ? json.get("error").getAsString() : "Unknown error";
        JsonObject json2 = new JsonObject();
        json2.addProperty("type", "error");
        json2.addProperty("error", str);
        blockingQueue.offer(json2);
    }

    public static void process4() {
        try {
            if (webSocketClient != null && webSocketClient.isOpen()) {
                JsonObject json = new JsonObject();
                json.addProperty("type", "disconnect");
                json.addProperty("username", currentUsername);
                handleJsonObject10(json);
                webSocketClient.send(gson.toJson((JsonElement)json));
                webSocketClient.close();
            }
        } catch (Exception ignored) {
        }

        connected = false;
        flag = false;
        flag2 = false;
        currentUsername = null;
        text3 = null;
        CapeModeHelper.instance.clear();
        blockingQueue.clear();
    }

    public static String computeString() {
        try {
            // VENDOR-DISABLED: Brutality.getAuthHandler().getUsername()
        } catch (Exception ignored) {
        }

        return currentUsername;
    }
}
