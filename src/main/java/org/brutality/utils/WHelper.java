package org.brutality.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.notification.old.ModuleEnabledHandler;
import org.brutality.utils.render.RenderHelper3;

public class WHelper {
    private static final File file = new File(Minecraft.getMinecraft().mcDataDir, "Brutality/Players/kos.json");
    private static final File file2 = new File(Minecraft.getMinecraft().mcDataDir, "Brutality/Players/kos.txt");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, WHelper.Entry> lookupMap = new HashMap<>();
    private static Map<String, String> lookupMap2 = new HashMap<>();
    private static final Map<String, JsonElement> lookupMap3 = new HashMap<>();
    private static long timestamp;
    private static boolean enabled = false;
    private static volatile String text;
    private static final long timestamp2 = 1000L;

    public static synchronized Set<String> computeSet() {
        process2();
        var treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (WHelper.Entry entry : lookupMap.values()) {
            treeSet.add(entry.group);
        }

        return treeSet;
    }

    public static synchronized void handleString(String str, String str2) {
        if (str != null && !str.trim().isEmpty()) {
            process2();
            String str3 = computeString4(str);
            WHelper.Entry entry = lookupMap.put(str3, new WHelper.Entry(str.trim(), computeString3(str2)));
            if (entry != null) {
                lookupMap2.remove(computeString2(entry.name));
            }

            lookupMap3.remove(str3);
            lookupMap2.put(computeString2(str), str3);
            save();
            ModuleEnabledHandler.handleString5("KOS", str.trim() + " added to KOS.");
        }
    }

    public static synchronized void process() {
        lookupMap.clear();
        lookupMap2.clear();
        lookupMap3.clear();
        save();
    }

    public static synchronized String computeString(String str) {
        if (str == null) {
            return "default";
        }
        process2();
        String str2 = lookupMap2.get(computeString2(str));
        if (str2 != null) {
            WHelper.Entry entry = lookupMap.get(str2);
            if (entry != null) {
                return entry.group;
            }
        }

        return "default";
    }

    public static void handleString2(String str) {
        text = str != null && !str.trim().isEmpty() ? computeString2(str) : null;
    }

    public static boolean matchesString2(String str) {
        return enabled && matchesString(str);
    }

    public static synchronized void handleString2(String str, String str2) {
        if (str != null) {
            process2();
            String str3 = lookupMap2.get(computeString2(str));
            if (str3 != null) {
                WHelper.Entry entry = lookupMap.get(str3);
                if (entry != null) {
                    entry.group = computeString3(str2);
                    save();
                }
            }
        }
    }

    public static synchronized Map<String, String> computeMap() {
        process2();
        HashMap map = new HashMap();

        for (Map.Entry entry : lookupMap2.entrySet()) {
            WHelper.Entry entry2 = lookupMap.get(entry.getValue());
            if (entry2 != null) {
                map.put((String)entry.getKey(), entry2.name);
            }
        }

        return map;
    }

    public static Color computeColor() {
        return new Color(255, 0, 40, 95);
    }

    public static synchronized void handleString(String str) {
        if (str != null) {
            process2();
            String str2 = lookupMap2.remove(computeString2(str));
            if (str2 != null) {
                lookupMap.remove(str2);
            }

            save();
            if (str2 != null) {
                ModuleEnabledHandler.handleString("KOS", str.trim() + " removed from KOS.");
            }
        }
    }

    private static boolean matchesUUID(UUID uuid, String str) {
        if (uuid != null && str != null && !str.trim().isEmpty()) {
            String str2 = uuid.toString();
            if (!lookupMap3.containsKey(str2)) {
                return false;
            }
            JsonElement element = lookupMap3.remove(str2);
            String str3 = computeString(element.isJsonObject() ? element.getAsJsonObject() : null, "group");
            lookupMap.put(str2, new WHelper.Entry(str.trim(), computeString3(str3)));
            String str4 = lookupMap2.put(computeString2(str), str2);
            if (str4 != null && !str4.equals(str2)) {
                lookupMap.remove(str4);
            }

            return true;
        }
        return false;
    }

    public static synchronized boolean matchesString(String str) {
        if (str == null) {
            return false;
        }
        process2();
        String str2 = text;
        return str2 != null && str.trim().equalsIgnoreCase(str2) || lookupMap2.containsKey(computeString2(str));
    }

    private static String computeString2(String str) {
        return str.trim().toLowerCase(Locale.ROOT);
    }

    private static String computeString(JsonObject json, String str) {
        if (json == null) {
            return null;
        }
        JsonElement element = json.get(str);
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString() ? element.getAsString() : null;
    }

    private static void process2() {
        if (!lookupMap3.isEmpty()) {
            long now = System.currentTimeMillis();
            if (now >= timestamp) {
                timestamp = now + timestamp2;
                Minecraft mc = Minecraft.getMinecraft();
                boolean flag = false;
                if (mc.theWorld != null) {
                    for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                        flag |= matchesUUID(playerEntity.getUniqueID(), playerEntity.getName());
                    }
                }

                if (mc.getNetHandler() != null) {
                    for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                        flag |= matchesUUID(playerInfo.getGameProfile().getId(), playerInfo.getGameProfile().getName());
                    }
                }

                if (flag) {
                    save();
                }
            }
        }
    }

    static {
        load();
    }

    private static void handleMap(Map<String, WHelper.Entry> map) {
        if (file2.exists()) {
            try {
                String str;
                try (BufferedReader reader = Files.newBufferedReader(file2.toPath(), StandardCharsets.UTF_8)) {
                    while ((str = reader.readLine()) != null) {
                        String str2 = str.trim();
                        if (!str2.isEmpty()) {
                            map.put("name:" + computeString2(str2), new WHelper.Entry(str2, "default"));
                        }
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException("Cannot load legacy KOS list", e);
            }
        }
    }

    public static void w(String str) {
        handleString(str, "default");
    }

    public static void setEnabled(boolean enabled2) {
        enabled = enabled2;
    }

    private static String computeString4(String str) {
        String str2 = lookupMap2.get(computeString2(str));
        if (str2 != null) {
            return str2;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null) {
            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity.getName().equalsIgnoreCase(str.trim())) {
                    return playerEntity.getUniqueID().toString();
                }
            }
        }

        if (mc.getNetHandler() != null) {
            for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                if (playerInfo.getGameProfile().getName().equalsIgnoreCase(str.trim()) && playerInfo.getGameProfile().getId() != null) {
                    return playerInfo.getGameProfile().getId().toString();
                }
            }
        }

        return "name:" + computeString2(str);
    }

    private static String computeString3(String str) {
        return str != null && !str.trim().isEmpty() ? str.trim() : "default";
    }

    private static synchronized void save() {
        JsonObject json = new JsonObject();

        for (Map.Entry entry : lookupMap3.entrySet()) {
            json.add((String)entry.getKey(), RenderHelper3.computeJsonElement((JsonElement)entry.getValue()));
        }

        for (Map.Entry entry2 : lookupMap.entrySet()) {
            JsonObject json2 = new JsonObject();
            json2.addProperty("name", ((WHelper.Entry)entry2.getValue()).name);
            json2.addProperty("group", computeString3(((WHelper.Entry)entry2.getValue()).group));
            json.add((String)entry2.getKey(), json2);
        }

        Path path = null;

        try {
            Path path2 = file.toPath();
            Files.createDirectories(path2.getParent());
            path = Files.createTempFile(path2.getParent(), "kos-", ".tmp");

            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                gson.toJson((JsonElement)json, writer);
            }

            try {
                Files.move(path, path2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(path, path2, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e2) {
            throw new IllegalStateException("Could not save KOS list to " + file, e2);
        } finally {
            if (path != null) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static synchronized List<String> computeList(String str) {
        process2();
        List list = new ArrayList();

        for (WHelper.Entry entry : lookupMap.values()) {
            if (entry.group.equalsIgnoreCase(str)) {
                list.add(entry.name);
            }
        }

        return list;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    private static synchronized void load() {
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();
        if (file.exists()) {
            try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                JsonElement element = new JsonParser().parse(reader);
                if (!element.isJsonObject()) {
                    throw new IOException("KOS file must contain a JSON object");
                }

                for (Map.Entry entry : element.getAsJsonObject().entrySet()) {
                    String key = (String)entry.getKey();
                    JsonElement val = (JsonElement)entry.getValue();
                    JsonObject json = val.isJsonObject() ? val.getAsJsonObject() : null;
                    String str = computeString(json, "name");
                    if (str == null && val.isJsonPrimitive() && val.getAsJsonPrimitive().isString()) {
                        str = val.getAsString();
                    }

                    if ((str == null || str.trim().isEmpty()) && key.startsWith("name:")) {
                        str = key.substring(5);
                    }

                    if (str != null && !str.trim().isEmpty()) {
                        map.put(key, new WHelper.Entry(str.trim(), computeString3(computeString(json, "group"))));
                    } else {
                        map2.put(key, RenderHelper3.computeJsonElement(val));
                    }
                }
            } catch (RuntimeException | IOException e) {
                try {
                    Files.copy(file.toPath(), file.toPath().resolveSibling("kos-unreadable-" + System.currentTimeMillis() + ".json"));
                } catch (IOException e2) {
                    throw new IllegalStateException("Cannot back up unreadable KOS file", e2);
                }

                System.err.println("[KOS] Could not load kos.json; preserved a backup: " + e.getMessage());
                handleMap(map);
            }
        } else {
            handleMap(map);
        }

        lookupMap = map;
        lookupMap3.clear();
        lookupMap3.putAll(map2);
        lookupMap2.clear();

        for (Map.Entry entry2 : lookupMap.entrySet()) {
            lookupMap2.put(computeString2(((WHelper.Entry)entry2.getValue()).name), (String)entry2.getKey());
        }

        timestamp = 0L;
        if (!file.exists() && !lookupMap.isEmpty()) {
            save();
        }
    }

    public static class Entry {
        public String name;
        public String group;

        public Entry(String name, String group) {
            this.name = name;
            this.group = group;
        }
    }
}
