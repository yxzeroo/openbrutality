package org.brutality.gui.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public final class PitPandaPlayerProfile {
    private static final String text = "https://pitpanda.rocks/api/players/";
    private static final String text2 = "617c9c76-e5a5-4213-a6b9-aded74ae64c8";
    private static final long timestamp = 300000L;
    private static final Map<String, PitPandaPlayerProfile.Entry> lookupMap = new ConcurrentHashMap<>();
    public String name = "";
    public String formattedName = "";
    public String formattedLevel = "";
    public String text3 = "";
    public boolean flag;
    public double amount;
    public long timestamp2;
    public long timestamp3;
    public long timestamp4;
    public long timestamp5;
    public long timestamp6;
    public long timestamp7;
    public long timestamp8;
    public long timestamp9;
    public double amount2;
    public float factor;
    public float factor2;
    public float factor3;
    public String text4 = "";
    public String text5 = "";
    public String text6 = "";
    public List<PitPandaPlayerProfile.Entry2> entries = Collections.emptyList();
    public List<PitPandaPlayerProfile.Entry2> entries2 = Collections.emptyList();
    public List<PitPandaPlayerProfile.Entry2> entries3 = Collections.emptyList();
    public List<PitPandaPlayerProfile.Entry2> entries4 = Collections.emptyList();
    public List<PitPandaPlayerProfile.Entry2> entries5 = Collections.emptyList();
    public List<PitPandaPlayerProfile.Entry2> entries6 = Collections.emptyList();
    public List<PitPandaPlayerProfile.Entry2> generalStats = Collections.emptyList();
    private static final long timestamp10 = -2197172674386460673L;

    public static PitPandaPlayerProfile computeFetchComponent2(JsonObject json) {
        PitPandaPlayerProfile fetchComponent2 = new PitPandaPlayerProfile();
        fetchComponent2.name = computeString(json, "name");
        fetchComponent2.formattedName = computeString(json, "formattedName");
        fetchComponent2.formattedLevel = computeString(json, "formattedLevel");
        fetchComponent2.flag = json.has("online") && json.get("online").getAsBoolean();
        fetchComponent2.amount = computeDouble(json, "currentGold");
        fetchComponent2.timestamp2 = (long)computeDouble(json, "playtime");
        fetchComponent2.timestamp3 = (long)Math.max(computeDouble(json, "lastLogout"), computeDouble(json, "lastSave"));
        JsonObject json2 = json.has("doc") && json.get("doc").isJsonObject() ? json.getAsJsonObject("doc") : new JsonObject();
        fetchComponent2.timestamp4 = (long)computeDouble(json2, "kills");
        fetchComponent2.timestamp5 = (long)computeDouble(json2, "deaths");
        fetchComponent2.timestamp9 = (long)computeDouble(json2, "assists");
        fetchComponent2.amount2 = computeDouble(json2, "kdr");
        fetchComponent2.timestamp6 = (long)computeDouble(json2, "bounty");
        fetchComponent2.timestamp7 = (long)computeDouble(json2, "renown");
        fetchComponent2.timestamp8 = (long)computeDouble(json2, "highestStreak");
        fetchComponent2.text3 = computeString(json2, "allegiance");
        JsonObject json3 = computeJsonObject(json, "xpProgress");
        JsonObject json4 = computeJsonObject(json, "goldProgress");
        JsonObject json5 = computeJsonObject(json, "renownProgress");
        fetchComponent2.factor = (float)computeDouble(json3, "percent");
        fetchComponent2.text4 = computeString(json3, "description");
        fetchComponent2.factor2 = (float)computeDouble(json4, "percent");
        fetchComponent2.text5 = computeString(json4, "description");
        fetchComponent2.factor3 = (float)computeDouble(json5, "percent");
        fetchComponent2.text6 = computeString(json5, "description");
        JsonObject json6 = computeJsonObject(json, "inventories");
        fetchComponent2.entries = computeList(json6, "main");
        fetchComponent2.entries2 = computeList(json6, "armor");
        fetchComponent2.entries3 = computeList(json6, "enderchest");
        fetchComponent2.entries4 = computeList(json6, "perks");
        fetchComponent2.entries5 = computeList(json6, "killstreaks");
        fetchComponent2.entries6 = computeList(json6, "upgrades");
        fetchComponent2.generalStats = computeList(json6, "generalStats");
        return fetchComponent2;
    }

    private static String computeString(JsonObject json, String str) {
        return json.has(str) && json.get(str).isJsonPrimitive() ? json.get(str).getAsString() : "";
    }

    private static double computeDouble(JsonObject json, String str) {
        try {
            return json.has(str) && json.get(str).isJsonPrimitive() ? json.get(str).getAsDouble() : 0.0;
        } catch (RuntimeException e) {
            return 0.0;
        }
    }

    public static String computeString(long l) {
        if (l <= 0L) {
            return "unknown";
        }
        long l2 = Math.max(0L, (System.currentTimeMillis() - l) / 60000L);
        if (l2 < 60L) {
            return l2 + "m ago";
        }
        return l2 < 1440L ? l2 / 60L + "h ago" : l2 / 1440L + "d ago";
    }

    private static PitPandaPlayerProfile computeFetchComponent2(String str) {
        try {
            URL url = new URL("https://pitpanda.rocks/api/players/" + URLEncoder.encode(str, "UTF-8") + "?key=617c9c76-e5a5-4213-a6b9-aded74ae64c8");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("User-Agent", "Brutality-Client");
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(10000);
            if (conn.getResponseCode() != 200) {
                return null;
            }
            PitPandaPlayerProfile fetchComponent2;
            try (InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject asJsonObject = new JsonParser().parse(inputStreamReader).getAsJsonObject();
                if (!asJsonObject.has("data") || !asJsonObject.get("data").isJsonObject()) {
                    return null;
                }

                fetchComponent2 = computeFetchComponent2(asJsonObject.getAsJsonObject("data"));
            }

            return fetchComponent2;
        } catch (Exception e) {
            return null;
        }
    }

    private static List<PitPandaPlayerProfile.Entry2> computeList(JsonObject json, String str) {
        if (json.has(str) && json.get(str).isJsonArray()) {
            JsonArray asJsonArray = json.getAsJsonArray(str);
            ArrayList list = new ArrayList(asJsonArray.size());

            for (JsonElement element : asJsonArray) {
                if (element != null && element.isJsonObject() && element.getAsJsonObject().has("id")) {
                    JsonObject asJsonObject = element.getAsJsonObject();
                    ArrayList list2 = new ArrayList();
                    if (asJsonObject.has("desc") && asJsonObject.get("desc").isJsonArray()) {
                        for (JsonElement element2 : asJsonObject.getAsJsonArray("desc")) {
                            list2.add(element2.isJsonNull() ? "" : element2.getAsString());
                        }
                    }

                    int asInt = asJsonObject.get("id").getAsInt();
                    int n = 0;
                    int n2 = -1;
                    String str2 = computeString(asJsonObject, "meta");
                    if (asInt >= 298 && asInt <= 301) {
                        try {
                            n2 = Integer.parseInt(str2.trim(), 16) & (int)timestamp10;
                        } catch (NumberFormatException ignored) {
                        }
                    } else {
                        n = (int)computeDouble(asJsonObject, "meta");
                        Item itemById = Item.getItemById(asInt);
                        if (itemById != null && itemById.isDamageable()) {
                            n = 0;
                        }
                    }

                    PitPandaPlayerProfile.Entry2 entry2 = new PitPandaPlayerProfile.Entry2(
                        asInt, n, (int)Math.max(1.0, computeDouble(asJsonObject, "count")), computeString(asJsonObject, "name"), list2, n2
                    );
                    entry2.timestamp = (long)computeDouble(asJsonObject, "nonce");
                    list.add(entry2);
                } else {
                    list.add(null);
                }
            }

            return list;
        }
        return Collections.emptyList();
    }

    public static String computeString2(double d) {
        if (d >= 1000000.0) {
            return computeString(d / 1000000.0) + "m";
        }
        return d >= 1000.0 ? computeString(d / 1000.0) + "k" : computeString(d);
    }

    public static CompletableFuture<PitPandaPlayerProfile> fetch(String str) {
        String str2 = str.toLowerCase(Locale.ROOT);
        PitPandaPlayerProfile.Entry entry = lookupMap.get(str2);
        long now = System.currentTimeMillis();
        if (entry == null || now - entry.timestamp >= 300000L && entry.completableFuture.isDone()) {
            CompletableFuture future = CompletableFuture.supplyAsync(() -> computeFetchComponent2(str));
            lookupMap.put(str2, new PitPandaPlayerProfile.Entry(future, now));
            return future;
        }
        return entry.completableFuture;
    }

    private static JsonObject computeJsonObject(JsonObject json, String str) {
        return json.has(str) && json.get(str).isJsonObject() ? json.getAsJsonObject(str) : new JsonObject();
    }

    private static String computeString(double d) {
        String str = String.format(Locale.ROOT, "%.1f", d);
        return str.endsWith(".0") ? str.substring(0, str.length() - 2) : str;
    }

    public static final class Entry {
        public final CompletableFuture<PitPandaPlayerProfile> completableFuture;
        public final long timestamp;

        public Entry(CompletableFuture<PitPandaPlayerProfile> completableFuture, long timestamp) {
            this.completableFuture = completableFuture;
            this.timestamp = timestamp;
        }
    }

    public static final class Entry2 {
        public final int count;
        public final int count2;
        public final int count3;
        public final int count4;
        public long timestamp;
        public final String text;
        public final List<String> entries;
        private ItemStack stack;
        private boolean flag;

        public ItemStack computeItemStack() {
            if (this.flag) {
                return this.stack;
            }
            this.flag = true;
            Item itemById = Item.getItemById(this.count);
            if (itemById == null) {
                return null;
            }
            this.stack = new ItemStack(itemById, Math.max(1, this.count3), this.count2);
            NBTTagCompound nbt = new NBTTagCompound();
            if (this.text != null && !this.text.isEmpty()) {
                nbt.setString("Name", this.text);
            }

            NBTTagList tagList = new NBTTagList();

            for (String str : this.entries) {
                tagList.appendTag(new NBTTagString(str));
            }

            nbt.setTag("Lore", tagList);
            if (this.count4 >= 0) {
                nbt.setInteger("color", this.count4);
            }

            NBTTagCompound nbt2 = new NBTTagCompound();
            nbt2.setTag("display", nbt);
            this.stack.setTagCompound(nbt2);
            return this.stack;
        }

        public Entry2(int count, int n, int n2, String text, List<String> entries, int n3) {
            this.count = count;
            this.count2 = n;
            this.count3 = n2;
            this.text = text;
            this.entries = entries;
            this.count4 = n3;
        }

        public List<String> computeList() {
            ArrayList list = new ArrayList();
            list.add(this.text != null && !this.text.isEmpty() ? this.text : "§fItem");
            list.addAll(this.entries);
            if (this.timestamp > 0L) {
                list.add("");
                list.add("§eClick to view item history");
            }

            return list;
        }
    }
}
