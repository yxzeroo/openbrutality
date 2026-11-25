package org.brutality.alt.altmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AccountStatusStore {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File file = new File("Brutality");
    private static final File file2 = new File(file, "account_status.json");
    private static final Map<String, AccountStatusStore.Entry> lookupMap = new ConcurrentHashMap<>();
    private static volatile boolean loaded = false;
    private static final long timestamp = -1L;

    private static String computeString2(String str) {
        return str == null ? null : str.toLowerCase();
    }

    public static boolean matchesString2(String str) {
        AccountStatusStore.Entry entry = computeEntry(str);
        return entry != null && entry.flag;
    }

    public static long computeLong2(String str) {
        AccountStatusStore.Entry entry = computeEntry(str);
        return entry == null ? 0L : entry.timestamp2;
    }

    public static void handleString(String str, long l, String str2) {
        if (str != null && !str.isEmpty()) {
            AccountStatusStore.Entry orCreate = getOrCreate(str);
            boolean flag = matchesEntry(orCreate);
            boolean flag2 = l > 0L || str2 != null && !str2.isEmpty();
            if (!flag) {
                orCreate.timestamp2 = System.currentTimeMillis();
                orCreate.timestamp = l <= 0L ? timestamp : l;
                orCreate.text = str2;
            } else if (flag2) {
                if (l > 0L) {
                    orCreate.timestamp = l;
                }

                if (str2 != null && !str2.isEmpty()) {
                    orCreate.text = str2;
                }
            }

            orCreate.flag = false;
            save();
        }
    }

    private static boolean matchesEntry(AccountStatusStore.Entry entry) {
        if (entry == null || entry.timestamp == 0L) {
            return false;
        }
        return entry.timestamp < 0L ? true : System.currentTimeMillis() < entry.timestamp;
    }

    public static void handleString(String str) {
        if (str != null && !str.isEmpty()) {
            AccountStatusStore.Entry entry = computeEntry(str);
            if (entry != null && entry.flag) {
                entry.flag = false;
                save();
            }
        }
    }

    public static void handleString2(String str) {
        if (str != null && !str.isEmpty()) {
            AccountStatusStore.Entry orCreate = getOrCreate(str);
            boolean flag = false;
            if (!orCreate.flag) {
                orCreate.flag = true;
                flag = true;
            }

            if (orCreate.timestamp != 0L || orCreate.timestamp2 != 0L || orCreate.text != null) {
                orCreate.timestamp = 0L;
                orCreate.timestamp2 = 0L;
                orCreate.text = null;
                flag = true;
            }

            if (flag) {
                save();
            }
        }
    }

    public static boolean matchesString(String str) {
        return matchesEntry(computeEntry(str));
    }

    private static synchronized void process() {
        if (!loaded) {
            loaded = true;

            try {
                if (file2.exists()) {
                    try (FileReader fileReader = new FileReader(file2)) {
                        JsonElement element = new JsonParser().parse(fileReader);
                        if (element != null && element.isJsonObject()) {
                            JsonObject asJsonObject = element.getAsJsonObject();
                            JsonObject json = asJsonObject.has("users") && asJsonObject.get("users").isJsonObject() ? asJsonObject.getAsJsonObject("users") : asJsonObject;

                            for (Map.Entry entry : json.entrySet()) {
                                if (entry.getValue() != null && ((JsonElement)entry.getValue()).isJsonObject()) {
                                    JsonObject asJsonObject2 = ((JsonElement)entry.getValue()).getAsJsonObject();
                                    AccountStatusStore.Entry entry2 = new AccountStatusStore.Entry();
                                    if (asJsonObject2.has("onPit")) {
                                        entry2.flag = asJsonObject2.get("onPit").getAsBoolean();
                                    }

                                    if (asJsonObject2.has("compBanUntil")) {
                                        entry2.timestamp = asJsonObject2.get("compBanUntil").getAsLong();
                                    }

                                    if (asJsonObject2.has("compBannedAt")) {
                                        entry2.timestamp2 = asJsonObject2.get("compBannedAt").getAsLong();
                                    }

                                    if (asJsonObject2.has("compBanText") && !asJsonObject2.get("compBanText").isJsonNull()) {
                                        entry2.text = asJsonObject2.get("compBanText").getAsString();
                                    }

                                    lookupMap.put(((String)entry.getKey()).toLowerCase(), entry2);
                                }
                            }

                            return;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private AccountStatusStore() {
    }

    public static long computeLong(String str) {
        AccountStatusStore.Entry entry = computeEntry(str);
        return entry == null ? 0L : entry.timestamp;
    }

    private static AccountStatusStore.Entry computeEntry(String str) {
        process();
        String str2 = computeString2(str);
        return str2 == null ? null : lookupMap.get(str2);
    }

    private static AccountStatusStore.Entry getOrCreate(String str) {
        process();
        return lookupMap.computeIfAbsent(computeString2(str), str2 -> new AccountStatusStore.Entry());
    }

    private static synchronized void save() {
        try {
            if (!file.exists()) {
                file.mkdirs();
            }

            JsonObject json = new JsonObject();
            JsonObject json2 = new JsonObject();

            for (Map.Entry entry : lookupMap.entrySet()) {
                AccountStatusStore.Entry val = (AccountStatusStore.Entry)entry.getValue();
                JsonObject json3 = new JsonObject();
                json3.addProperty("onPit", val.flag);
                json3.addProperty("compBanUntil", val.timestamp);
                json3.addProperty("compBannedAt", val.timestamp2);
                if (val.text != null) {
                    json3.addProperty("compBanText", val.text);
                }

                json2.add((String)entry.getKey(), json3);
            }

            json.add("users", json2);

            try (FileWriter fileWriter = new FileWriter(file2)) {
                gson.toJson((JsonElement)json, fileWriter);
            }
        } catch (Exception ignored) {
        }
    }

    public static String computeString(String str) {
        AccountStatusStore.Entry entry = computeEntry(str);
        return entry == null ? null : entry.text;
    }

    public static final class Entry {
        public boolean flag = false;
        public long timestamp = 0L;
        public long timestamp2 = 0L;
        public String text = null;
    }
}
