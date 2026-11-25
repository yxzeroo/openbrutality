package org.brutality.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;

public class ProxyAccountStore {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final HashMap<String, ProxyConfig> lookupMap = new HashMap<>();
    private static String text = "";

    public static void load() {
        File file = new File(Minecraft.getMinecraft().mcDataDir + "/config", "ProxyAccounts.json");
        if (file.exists()) {
            try {
                String str = new String(Files.readAllBytes(file.toPath()), "UTF-8");
                if (str.trim().isEmpty()) {
                    return;
                }

                JsonObject asJsonObject = new JsonParser().parse(str).getAsJsonObject();
                lookupMap.clear();

                for (Entry entry : asJsonObject.entrySet()) {
                    JsonObject asJsonObject2 = ((JsonElement)entry.getValue()).getAsJsonObject();
                    String str2 = asJsonObject2.has("type") ? asJsonObject2.get("type").getAsString() : "SOCKS5";
                    ProxyConfig.Socks4Mode socks4Mode = "SOCKS4".equals(str2) ? ProxyConfig.Socks4Mode.SOCKS4 : ProxyConfig.Socks4Mode.SOCKS5;
                    String str3 = asJsonObject2.has("ip") ? asJsonObject2.get("ip").getAsString() : "";
                    int n = asJsonObject2.has("port") ? asJsonObject2.get("port").getAsInt() : 1080;
                    String str4 = asJsonObject2.has("username") ? asJsonObject2.get("username").getAsString() : "";
                    String str5 = asJsonObject2.has("password") ? asJsonObject2.get("password").getAsString() : "";
                    String str6 = asJsonObject2.has("userid") ? asJsonObject2.get("userid").getAsString() : "";
                    String str7 = asJsonObject2.has("name") ? asJsonObject2.get("name").getAsString() : (String)entry.getKey();
                    ProxyConfig proxyConfig = new ProxyConfig(str7, str3, n, socks4Mode, str4, str5, str6);
                    lookupMap.put((String)entry.getKey(), proxyConfig);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void save() {
        File file = new File(Minecraft.getMinecraft().mcDataDir + "/config");
        if (!file.exists()) {
            file.mkdirs();
        }

        JsonObject json = new JsonObject();

        for (Entry entry : lookupMap.entrySet()) {
            ProxyConfig val = (ProxyConfig)entry.getValue();
            JsonObject json2 = new JsonObject();
            json2.addProperty("name", val.getName());
            json2.addProperty("type", val.computeSocks4Mode().name());
            json2.addProperty("ip", val.computeString());
            json2.addProperty("port", val.getPort());
            json2.addProperty("username", val.getUsername());
            json2.addProperty("password", val.getPassword());
            json2.addProperty("userid", val.computeString2());
            json.add((String)entry.getKey(), json2);
        }

        File file2 = new File(file, "ProxyAccounts.json");

        try (FileWriter fileWriter = new FileWriter(file2)) {
            gson.toJson((JsonElement)json, fileWriter);
        } catch (IOException ignored) {
        }
    }

    public static void handleString(String username, ProxyConfig proxy) {
        if (username != null && !username.isEmpty() && proxy != null) {
            lookupMap.put(username, proxy);
            save();
        }
    }

    public static void handleString(String username) {
        ProxyConfig proxyConfig = ProxyManager.getProxyConfig();
        if (proxyConfig != null) {
            handleString(username, proxyConfig);
        }
    }

    public static void handleString4(String username) {
        if (lookupMap.remove(username) != null) {
            save();
        }
    }

    public static ProxyConfig computeProxyConfig(String username) {
        return username == null ? null : lookupMap.get(username);
    }

    public static boolean matchesString(String username) {
        return username != null && lookupMap.containsKey(username);
    }

    public static void handleString3(String username) {
        ProxyConfig proxyConfig = lookupMap.get(username);
        if (proxyConfig != null) {
            ProxyManager.handleProxyConfig(proxyConfig);
        }
    }

    public static void process() {
        String playerName = getPlayerName();
        if (!playerName.isEmpty() && !playerName.equals(text)) {
            handleString2(playerName);
        }
    }

    public static void handleString2(String username) {
        if (username != null && !username.isEmpty()) {
            if (!username.equals(text)) {
                String str = text;
                text = username;
                if (!str.isEmpty() && lookupMap.containsKey(str) && ProxyManager.checkState()) {
                    ProxyManager.handleString2("account switched to " + username);
                }

                handleString3(username);
            }
        }
    }

    public static ProxyAccountStore.NoneMode computeNoneMode() {
        String playerName = getPlayerName();
        return playerName.isEmpty() ? ProxyAccountStore.NoneMode.NONE : computeNoneMode(playerName);
    }

    public static ProxyAccountStore.NoneMode computeNoneMode(String username) {
        ProxyConfig proxyConfig = lookupMap.get(username);
        if (proxyConfig == null) {
            return ProxyManager.checkState() ? ProxyAccountStore.NoneMode.NONE : ProxyAccountStore.NoneMode.NO_PROXY;
        }
        for (Entry entry : lookupMap.entrySet()) {
            if (!((String)entry.getKey()).equalsIgnoreCase(username)) {
                ProxyConfig val = (ProxyConfig)entry.getValue();
                if (val.computeString().equals(proxyConfig.computeString()) && val.getPort() == proxyConfig.getPort()) {
                    return ProxyAccountStore.NoneMode.SHARED_PROXY;
                }
            }
        }

        return ProxyAccountStore.NoneMode.NONE;
    }

    public static String computeString(String username) {
        ProxyConfig proxyConfig = lookupMap.get(username);
        if (proxyConfig == null) {
            return null;
        }
        for (Entry entry : lookupMap.entrySet()) {
            if (!((String)entry.getKey()).equalsIgnoreCase(username)) {
                ProxyConfig val = (ProxyConfig)entry.getValue();
                if (val.computeString().equals(proxyConfig.computeString()) && val.getPort() == proxyConfig.getPort()) {
                    return (String)entry.getKey();
                }
            }
        }

        return null;
    }

    public static List<String> computeList(ProxyConfig proxy) {
        ArrayList list = new ArrayList();
        if (proxy == null) {
            return list;
        }
        for (Entry entry : lookupMap.entrySet()) {
            ProxyConfig val = (ProxyConfig)entry.getValue();
            if (val != null && val.computeString().equals(proxy.computeString()) && val.getPort() == proxy.getPort()) {
                list.add((String)entry.getKey());
            }
        }

        list.sort(String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    public static HashMap<String, ProxyConfig> getLookupMap() {
        return lookupMap;
    }

    private static String getPlayerName() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.getSession() != null) {
                String username = mc.getSession().getUsername();
                return username != null ? username : "";
            }
        } catch (Exception ignored) {
        }

        return "";
    }

    public static enum NoneMode {
        NONE,
        NO_PROXY,
        SHARED_PROXY;

        private static ProxyAccountStore.NoneMode[] computeNoneModeArray() {
            return new ProxyAccountStore.NoneMode[]{NONE, NO_PROXY, SHARED_PROXY};
        }
    }
}
