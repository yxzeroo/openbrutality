package org.brutality.utils.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AttemptDenickWithRetriesHelper {
    private static final String text = "https://pitpanda.rocks/api/itemsearch/nonce";
    private static final String text2 = "https://pitpanda.rocks/api/players/";
    private static final String text3 = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String text4 = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String text5 = "https://api.ashcon.app/mojang/v2/user/";
    private static final String text6 = "617c9c76-e5a5-4213-a6b9-aded74ae64c8";
    public static final ConcurrentHashMap<String, AttemptDenickWithRetriesHelper.Entry> lookupMap = new ConcurrentHashMap<>();
    public static final Executor executor = Executors.newFixedThreadPool(3, task -> {
        Thread thread = new Thread(task, "DenickerUtils-HTTP");
        thread.setDaemon(true);
        return thread;
    });
    private static final long timestamp = 2000L;

    public static String computeString(int n) {
        try {
            URL url = new URL("https://pitpanda.rocks/api/itemsearch/nonce" + n + "?key=617c9c76-e5a5-4213-a6b9-aded74ae64c8");
            HttpURLConnection conn = computeHttpURLConnection(url);
            if (conn.getResponseCode() != 200) {
                return null;
            }
            JsonObject json = computeJsonObject(conn);
            if (json != null && json.get("success").getAsBoolean()) {
                JsonArray asJsonArray = json.getAsJsonArray("items");
                return asJsonArray != null && asJsonArray.size() != 0 ? asJsonArray.get(0).getAsJsonObject().get("owner").getAsString() : null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String computeString2(String str) {
        if (str == null) {
            return null;
        }
        AttemptDenickWithRetriesHelper.Entry entry = lookupMap.get(str.toLowerCase());
        return entry != null ? entry.text2 : null;
    }

    public static String computeString3(String str) {
        try {
            HttpURLConnection conn = computeHttpURLConnection(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + str));
            if (conn.getResponseCode() == 200) {
                JsonObject json = computeJsonObject(conn);
                if (json != null && json.has("name")) {
                    return json.get("name").getAsString();
                }
            }
        } catch (Exception ignored) {
        }

        try {
            HttpURLConnection conn2 = computeHttpURLConnection(new URL("https://api.ashcon.app/mojang/v2/user/" + str));
            if (conn2.getResponseCode() == 200) {
                JsonObject json2 = computeJsonObject(conn2);
                if (json2 != null && json2.has("name")) {
                    return json2.get("name").getAsString();
                }
            }
        } catch (Exception ignored2) {
        }

        return null;
    }

    private static HttpURLConnection computeHttpURLConnection(URL url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);
        return conn;
    }

    public static void attemptDenickWithRetries(EntityPlayer player, String str, int n, int n2, Consumer<String> callback) {
        Integer integer = computeInteger(player);
        if (integer == null) {
            if (n < n2) {
                handleEntityPlayer(player, str, n, n2, callback);
            } else {
                callback.accept("§8[§c§8] - §cNo nonce found after " + n2 + " attempts for §f" + str);
            }
        } else {
            CompletableFuture.<String>supplyAsync(() -> computeString(integer), executor)
                .thenCompose(
                    str2 -> str2 == null
                        ? CompletableFuture.completedFuture(new String[]{null, null})
                        : CompletableFuture.<String>supplyAsync(() -> computeString3(str2), executor).thenApply(str3 -> new String[]{str3, str2})
                )
                .thenAccept(parts -> Minecraft.getMinecraft().addScheduledTask(() -> {
                    if (parts[0] != null && parts[1] != null) {
                        handleString(str, parts[0], parts[1], callback);
                    } else if (n < n2) {
                        handleEntityPlayer(player, str, n, n2, callback);
                    } else {
                        callback.accept("§8[§c§8] - §cDenick failed after " + n2 + " attempts for §f" + str);
                    }
                }))
                .exceptionally(t -> {
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        if (n < n2) {
                            handleEntityPlayer(player, str, n, n2, callback);
                        } else {
                            callback.accept("§8[§c§8] - §cError: " + t.getMessage());
                        }
                    });
                    return null;
                });
        }
    }

    public static Integer computeInteger(EntityPlayer player) {
        ItemStack[] stacks = new ItemStack[]{
            player.getCurrentEquippedItem(), player.getEquipmentInSlot(4), player.getEquipmentInSlot(3), player.getEquipmentInSlot(2), player.getEquipmentInSlot(1)
        };

        for (ItemStack stack : stacks) {
            if (stack != null && stack.hasTagCompound()) {
                Integer integer = computeInteger(stack.getTagCompound());
                if (integer != null) {
                    return integer;
                }
            }
        }

        return null;
    }

    private static void handleEntityPlayer(final EntityPlayer player, final String str, final int n, final int n2, final Consumer<String> callback) {
        int n3 = n + 1;
        callback.accept("§8[§c§8] - §eRetrying §f" + str + " §e(" + n3 + "/" + n2 + ")...");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Minecraft.getMinecraft().addScheduledTask(() -> AttemptDenickWithRetriesHelper.attemptDenickWithRetries(player, str, n + 1, n2, callback));
            }
        }, timestamp);
    }

    public static void process() {
        lookupMap.clear();
    }

    public static boolean matchesString2(String str) {
        try {
            HttpURLConnection conn = computeHttpURLConnection(
                new URL("https://pitpanda.rocks/api/players/" + str + "?key=617c9c76-e5a5-4213-a6b9-aded74ae64c8")
            );
            if (conn.getResponseCode() == 200) {
                JsonObject json = computeJsonObject(conn);
                if (json != null && json.has("success")) {
                    if (json.get("success").getAsBoolean()) {
                        return true;
                    }

                    if (json.has("error") && json.get("error").getAsString().contains("429")) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    public static void tryDenickPlayerWithValidation(String str, EntityPlayer player, Consumer<String> callback) {
        if (lookupMap.containsKey(str.toLowerCase())) {
            AttemptDenickWithRetriesHelper.Entry entry = lookupMap.get(str.toLowerCase());
            callback.accept("§8[§c§8] - §7Already resolved: §f" + str + " §7→ §a" + entry.text);
        } else if (player == null) {
            callback.accept("§8[§c§8] - §cPlayer §f" + str + " §cnot found in world.");
        } else {
            callback.accept("§8[§c§8] - §7Validating §f" + str + "§7...");
            CompletableFuture.<String>supplyAsync(() -> computeString4(str), executor)
                .thenCompose(
                    str2 -> str2 == null
                        ? CompletableFuture.completedFuture("FAKE")
                        : CompletableFuture.<Boolean>supplyAsync(() -> matchesString2(str2), executor)
                            .thenApply(val -> val ? "REAL_PITPANDA" : "REAL_MOJANG_ONLY")
                )
                .thenAccept(str3 -> Minecraft.getMinecraft().addScheduledTask(() -> {
                    switch (str3) {
                        case "FAKE":
                            callback.accept("§8[§c§8] - §a" + str + " §7has no Mojang account — proceeding with denick.");
                            attemptDenickWithRetries(player, str, 1, 5, callback);
                            break;
                        case "REAL_PITPANDA":
                            callback.accept("§8[§c§8] - §c" + str + " §7is a real PitPanda player — skipping denick.");
                            break;
                        case "REAL_MOJANG_ONLY":
                            callback.accept("§8[§c§8] - §e" + str + " §7exists on Mojang but not PitPanda — proceeding with caution...");
                            if (!matchesEntityPlayer(player)) {
                                callback.accept("§8[§c§8] - §c" + str + " §7has no denickable items.");
                                return;
                            }

                            attemptDenickWithRetries(player, str, 1, 5, callback);
                    }
                }))
                .exceptionally(t -> {
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        callback.accept("§8[§c§8] - §cValidation error: " + t.getMessage() + " — proceeding anyway.");
                        if (matchesEntityPlayer(player)) {
                            attemptDenickWithRetries(player, str, 1, 5, callback);
                        }
                    });
                    return null;
                });
        }
    }

    public static void handleString(String str, EntityPlayer player, Consumer<String> callback) {
        if (!lookupMap.containsKey(str.toLowerCase())) {
            if (player == null) {
                callback.accept("§8[§c§8] - §e" + str + " §7is nicked but is not loaded in the world.");
            } else if (!matchesEntityPlayer(player)) {
                callback.accept(
                    "§8[§c§8] - §e"
                        + str
                        + " §7is §cnicked §7but has no denickable items yet. §8(needs a mystic, ghelm, archangel/armageddon, or tier 1–3 pants)"
                );
            } else {
                callback.accept("§8[§c§8] - §9Attempting to denick §f" + str + "§9...");
                attemptDenickWithRetries(player, str, 1, 5, callback);
            }
        }
    }

    public static Collection<AttemptDenickWithRetriesHelper.Entry> computeCollection() {
        Set set = Collections.newSetFromMap(new IdentityHashMap());
        set.addAll(lookupMap.values());
        return set;
    }

    public static String computeString(String str) {
        if (str == null) {
            return null;
        }
        AttemptDenickWithRetriesHelper.Entry entry = lookupMap.get(str.toLowerCase());
        return entry != null ? entry.text : null;
    }

    private static void handleString(String str, String str2, String str3, Consumer<String> callback) {
        if (str2 != null && !str2.trim().isEmpty() && str3 != null && !str3.trim().isEmpty()) {
            AttemptDenickWithRetriesHelper.Entry entry = new AttemptDenickWithRetriesHelper.Entry(str, str2, str3);
            String str4 = str.toLowerCase();
            String str5 = str2.toLowerCase();
            AttemptDenickWithRetriesHelper.Entry entry2 = lookupMap.get(str4);
            if (entry2 != null) {
                lookupMap.remove(entry2.text2.toLowerCase());
                lookupMap.remove(entry2.text.toLowerCase());
            }

            lookupMap.put(str4, entry);
            lookupMap.put(str5, entry);
            callback.accept("§8[§c§8] - §a✔ §f" + str + " §7is actually §a" + str2 + " §8(§7" + str3 + "§8)");
        } else {
            callback.accept("§8[§c§8] - §cReceived invalid data for §f" + str);
        }
    }

    public static boolean matchesString(String str) {
        return str == null ? false : lookupMap.containsKey(str.toLowerCase());
    }

    private static JsonObject computeJsonObject(HttpURLConnection conn) {
        try {
            JsonObject json;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                JsonElement element = new JsonParser().parse(reader);
                json = element.isJsonObject() ? element.getAsJsonObject() : null;
            }

            return json;
        } catch (Exception e) {
            return null;
        }
    }

    public static AttemptDenickWithRetriesHelper.Entry computeEntry(String str) {
        return str == null ? null : lookupMap.get(str.toLowerCase());
    }

    public static int computeInt() {
        return computeCollection().size();
    }

    public static Integer computeInteger(NBTTagCompound nbt) {
        if (nbt.hasKey("Nonce")) {
            return nbt.getInteger("Nonce");
        }
        if (nbt.hasKey("ExtraAttributes")) {
            NBTTagCompound compoundTag = nbt.getCompoundTag("ExtraAttributes");
            if (compoundTag.hasKey("Nonce")) {
                return compoundTag.getInteger("Nonce");
            }
        }

        if (nbt.hasKey("tag")) {
            NBTTagCompound compoundTag2 = nbt.getCompoundTag("tag");
            if (compoundTag2.hasKey("ExtraAttributes")) {
                NBTTagCompound compoundTag3 = compoundTag2.getCompoundTag("ExtraAttributes");
                if (compoundTag3.hasKey("Nonce")) {
                    return compoundTag3.getInteger("Nonce");
                }
            }
        }

        for (String str : nbt.getKeySet()) {
            NBTBase tag = nbt.getTag(str);
            if (tag instanceof NBTTagCompound) {
                Integer integer = computeInteger((NBTTagCompound)tag);
                if (integer != null) {
                    return integer;
                }
            } else if (tag instanceof NBTTagList tagList) {
                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                    if (compoundTagAt.hasKey("Nonce")) {
                        return compoundTagAt.getInteger("Nonce");
                    }
                }
            }
        }

        return null;
    }

    public static boolean matchesString3(String str) {
        if (str == null) {
            return false;
        }
        AttemptDenickWithRetriesHelper.Entry entry = lookupMap.get(str.toLowerCase());
        if (entry != null) {
            lookupMap.remove(entry.text2.toLowerCase());
            lookupMap.remove(entry.text.toLowerCase());
            return true;
        }
        return false;
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && stack.hasTagCompound() && computeInteger(stack.getTagCompound()) != null) {
                return true;
            }
        }

        for (int j = 1; j <= 4; j++) {
            ItemStack equipmentInSlot = player.getEquipmentInSlot(j);
            if (equipmentInSlot != null && equipmentInSlot.hasTagCompound() && computeInteger(equipmentInSlot.getTagCompound()) != null) {
                return true;
            }
        }

        return false;
    }

    public static String computeString4(String str) {
        try {
            HttpURLConnection conn = computeHttpURLConnection(new URL("https://api.mojang.com/users/profiles/minecraft/" + str));
            if (conn.getResponseCode() == 200) {
                JsonObject json = computeJsonObject(conn);
                if (json != null && json.has("id")) {
                    return json.get("id").getAsString();
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static class Entry {
        public final String text;
        public final String text2;
        public final long timestamp;
        public final String text3;

        @Override
        public String toString() {
            return this.text2 + " -> " + this.text + " (" + this.text3 + ")";
        }

        public Entry(String str, String text, String str2) {
            this.text2 = str;
            this.text = text;
            this.text3 = str2;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
