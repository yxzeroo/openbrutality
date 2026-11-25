package org.brutality.utils.hypixel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BanManager {
    public static File file = new File("brutality/Bans");
    public static File file2 = new File(file, "bans.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, List<BanManager.Entry>> lookupMap = new ConcurrentHashMap<>();

    private long computeLong(String str) {
        if (str != null && !str.trim().isEmpty()) {
            try {
                long now = System.currentTimeMillis();
                long l = 0L;
                String[] parts = str.trim().split("\\s+");

                for (String str2 : parts) {
                    if (str2.endsWith("d")) {
                        int n = Integer.parseInt(str2.substring(0, str2.length() - 1));
                        l += n * 24 * 60 * 60 * 1000L;
                    } else if (str2.endsWith("h")) {
                        int n2 = Integer.parseInt(str2.substring(0, str2.length() - 1));
                        l += n2 * 60 * 60 * 1000L;
                    } else if (str2.endsWith("m")) {
                        int n3 = Integer.parseInt(str2.substring(0, str2.length() - 1));
                        l += n3 * 60 * 1000L;
                    } else if (str2.endsWith("s")) {
                        int n4 = Integer.parseInt(str2.substring(0, str2.length() - 1));
                        l += n4 * 1000L;
                    }
                }

                return now + l;
            } catch (Exception e) {
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    public void handleString(String str) {
        this.lookupMap.remove(str);
        this.process();
    }

    private boolean matchesJsonObject(JsonObject json, String str, boolean flag) {
        return json.has(str) && !json.get(str).isJsonNull() ? json.get(str).getAsBoolean() : flag;
    }

    private String computeString(JsonObject json, String str, String str2) {
        return json.has(str) && !json.get(str).isJsonNull() ? json.get(str).getAsString() : str2;
    }

    public void handleString(String str, HypixelBan hypixelBan, String str2, String str3) {
        long now = System.currentTimeMillis();
        String str4 = hypixelBan.getUnbanDate() == 0L ? "permanent" : "temporary";
        BanManager.Entry entry = new BanManager.Entry(str, str2, null, str3, str4, now, hypixelBan.getUnbanDate(), this.computeString(hypixelBan));
        this.addBanRecordWithoutDuplicate(str, entry);
        this.process();
    }

    public BanManager.Entry computeEntry(String str) {
        var list = this.computeList(str);
        return list.isEmpty() ? null : (BanManager.Entry)list.get(0);
    }

    private long computeLong(JsonObject json, String str, long l) {
        return json.has(str) && !json.get(str).isJsonNull() ? json.get(str).getAsLong() : l;
    }

    public boolean matchesString(String str) {
        if (str == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        var list = this.computeList(str);
        if (list.isEmpty()) {
            for (Map.Entry entry : this.lookupMap.entrySet()) {
                if (entry.getKey() != null && ((String)entry.getKey()).equalsIgnoreCase(str)) {
                    list = (List)entry.getValue();
                    break;
                }
            }
        }

        for (BanManager.Entry entry2 : list) {
            long timestamp2 = entry2.getTimestamp2();
            if (timestamp2 > 0L && timestamp2 <= now) {
                return true;
            }
        }

        return false;
    }

    private void addBanRecordWithoutDuplicate(String str, BanManager.Entry entry) {
        List<BanManager.Entry> list = this.lookupMap.computeIfAbsent(str, str2 -> new ArrayList<>());

        for (BanManager.Entry entry2 : list) {
            if (entry.x() != null && entry.x().equals(entry2.x())) {
                return;
            }

            if (entry.w() != null && entry.w().equals(entry2.w())) {
                return;
            }
        }

        if (!"recovery".equals(entry.z())) {
            for (BanManager.Entry entry3 : ((Iterable<BanManager.Entry>)(Object)(list))) {
                if (entry3.isActive() && !"recovery".equals(entry3.z())) {
                    return;
                }
            }
        } else {
            for (BanManager.Entry entry4 : ((Iterable<BanManager.Entry>)(Object)(list))) {
                if ("recovery".equals(entry4.z()) && entry4.isActive()) {
                    return;
                }
            }
        }

        list.add(entry);
        list.sort((entry5, entry6) -> Long.compare(entry6.getTimestamp(), entry5.getTimestamp()));
        if (list.size() > 50) {
            list.subList(50, list.size()).clear();
        }
    }

    public void handleString(String str, String str2, String str3) {
        long now = System.currentTimeMillis();
        long l = this.computeLong(str2);
        BanManager.Entry entry = new BanManager.Entry(str, "RECOVERY", null, str3, "recovery", now, l, str2);
        this.addBanRecordWithoutDuplicate(str, entry);
        this.process();
    }

    public List<BanManager.Entry> getAllBans() {
        List<BanManager.Entry> list = new ArrayList<>();

        for (List list2 : this.lookupMap.values()) {
            list.addAll(list2);
        }

        list.sort((entry, entry2) -> Long.compare(entry2.getTimestamp(), entry.getTimestamp()));
        return list;
    }

    public void init() {
        if (!file.exists()) {
            file.mkdirs();
        }

        this.loadBanHistory();
    }

    public Map<String, Object> getBanStats(String str) {
        HashMap map = new HashMap();
        List<BanManager.Entry> list = this.computeList(str);
        map.put("totalBans", list.size());
        map.put("permanentBans", list.stream().mapToInt(entry -> entry.z().equals("permanent") ? 1 : 0).sum());
        map.put("temporaryBans", list.stream().mapToInt(entry2 -> entry2.z().equals("temporary") ? 1 : 0).sum());
        map.put("accountBlocks", list.stream().mapToInt(entry3 -> entry3.z().equals("block") ? 1 : 0).sum());
        map.put("recoveryPhases", list.stream().mapToInt(entry4 -> entry4.z().equals("recovery") ? 1 : 0).sum());
        map.put("activeBans", list.stream().mapToInt(entry5 -> entry5.isActive() ? 1 : 0).sum());
        if (!list.isEmpty()) {
            BanManager.Entry entry6 = (BanManager.Entry)list.get(0);
            map.put("lastBanDate", new Date(entry6.getTimestamp()));
            map.put("lastBanReason", entry6.getReason());
            map.put("lastBanType", entry6.z());
        }

        return map;
    }

    public void process2() {
        this.lookupMap.clear();
        this.process();
    }

    private void process() {
        try {
            JsonObject json = new JsonObject();
            JsonObject json2 = new JsonObject();

            for (Map.Entry entry : this.lookupMap.entrySet()) {
                String key = (String)entry.getKey();
                List<BanManager.Entry> list = (List<BanManager.Entry>)(Object)((List)entry.getValue());
                JsonArray jsonArray = new JsonArray();

                for (BanManager.Entry entry2 : list) {
                    JsonObject json3 = new JsonObject();
                    json3.addProperty("username", entry2.getUsername());
                    if (entry2.x() != null) {
                        json3.addProperty("banId", entry2.x());
                    }

                    if (entry2.w() != null) {
                        json3.addProperty("blockId", entry2.w());
                    }

                    json3.addProperty("reason", entry2.getReason());
                    json3.addProperty("banType", entry2.z());
                    json3.addProperty("banTimestamp", entry2.getTimestamp());
                    json3.addProperty("unbanTimestamp", entry2.getTimestamp2());
                    json3.addProperty("duration", entry2.y());
                    json3.addProperty("isActive", entry2.isActive());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    json3.addProperty("banDate", simpleDateFormat.format(new Date(entry2.getTimestamp())));
                    if (entry2.getTimestamp2() > 0L) {
                        json3.addProperty("unbanDate", simpleDateFormat.format(new Date(entry2.getTimestamp2())));
                    }

                    jsonArray.add(json3);
                }

                json2.add(key, jsonArray);
            }

            json.add("users", json2);
            json.addProperty("lastUpdated", System.currentTimeMillis());
            json.addProperty("totalUsers", this.lookupMap.size());
            json.addProperty("totalBans", this.getAllBans().size());

            try (PrintWriter writer = new PrintWriter(new FileWriter(file2))) {
                writer.println(gson.toJson((JsonElement)json));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String computeString(JsonObject json, String str) {
        return json.has(str) && !json.get(str).isJsonNull() ? json.get(str).getAsString() : null;
    }

    public void cleanupExpiredBans() {
        long now = System.currentTimeMillis();
        int n = 0;

        for (Map.Entry entry : this.lookupMap.entrySet()) {
            List<BanManager.Entry> val = (List<BanManager.Entry>)entry.getValue();
            int size = val.size();
            val.removeIf(entry2 -> entry2.getTimestamp2() != 0L && entry2.getTimestamp2() < now);
            n += size - val.size();
        }

        this.lookupMap.entrySet().removeIf(entry3 -> entry3.getValue().isEmpty());
        if (n > 0) {
            this.process();
        }
    }

    public List<BanManager.Entry> computeList(String str) {
        var orDefault = this.lookupMap.getOrDefault(str, new ArrayList<>());
        return new ArrayList<>(orDefault);
    }

    private String computeString(HypixelBan hypixelBan) {
        if (hypixelBan.getUnbanDate() == 0L) {
            return "Permanent";
        }
        long l = hypixelBan.getUnbanDate() - System.currentTimeMillis();
        if (l <= 0L) {
            return "Expired";
        }
        long l2 = l / 86400000L;
        long l3 = l % 86400000L / 3600000L;
        long l4 = l % 3600000L / 60000L;
        long l5 = l % 60000L / 1000L;
        StringBuilder sb = new StringBuilder();
        if (l2 > 0L) {
            sb.append(l2).append("d ");
        }

        if (l3 > 0L) {
            sb.append(l3).append("h ");
        }

        if (l4 > 0L) {
            sb.append(l4).append("m ");
        }

        if (l5 > 0L) {
            sb.append(l5).append("s");
        }

        return sb.toString().trim();
    }

    public void handleString(String str, String str2) {
        long now = System.currentTimeMillis();
        BanManager.Entry entry = new BanManager.Entry(str, null, str2, "Account Security Block", "block", now, 0L, "Permanent");
        this.addBanRecordWithoutDuplicate(str, entry);
        this.process();
    }

    public boolean matchesString2(String str) {
        return this.computeList(str).stream().anyMatch(BanManager.Entry::isActive);
    }

    public void exportBanHistoryToText(File file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("=== BRUTALITY BAN HISTORY EXPORT ===");
            writer.println("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            writer.println();

            for (Map.Entry entry : this.lookupMap.entrySet()) {
                String key = (String)entry.getKey();
                List<BanManager.Entry> list = (List<BanManager.Entry>)(Object)((List)entry.getValue());
                writer.println("Username: " + key);
                writer.println("Total Bans: " + list.size());
                writer.println("Active Bans: " + list.stream().mapToInt(entry2 -> entry2.isActive() ? 1 : 0).sum());
                writer.println();

                for (BanManager.Entry entry3 : list) {
                    writer.println("  Ban Type: " + entry3.z().toUpperCase());
                    writer.println("  Reason: " + entry3.getReason());
                    writer.println("  Ban Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(entry3.getTimestamp())));
                    if (entry3.x() != null) {
                        writer.println("  Ban ID: #" + entry3.x());
                    }

                    if (entry3.w() != null) {
                        writer.println("  Block ID: #" + entry3.w());
                    }

                    if (entry3.getTimestamp2() > 0L) {
                        writer.println("  Unban Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(entry3.getTimestamp2())));
                    }

                    writer.println("  Duration: " + entry3.y());
                    writer.println("  Status: " + (entry3.isActive() ? "ACTIVE" : "EXPIRED"));
                    writer.println();
                }

                writer.println("----------------------------------------");
                writer.println();
            }
        } catch (Exception ignored) {
        }
    }

    public List<BanManager.Entry> computeList() {
        List list = new ArrayList();
        long now = System.currentTimeMillis();

        for (List list2 : this.lookupMap.values()) {
            for (BanManager.Entry entry : ((Iterable<BanManager.Entry>)(Object)(list2))) {
                if (entry.getTimestamp2() == 0L || entry.getTimestamp2() > now) {
                    list.add(entry);
                }
            }
        }

        return list;
    }

    private void loadBanHistory() {
        this.lookupMap.clear();
        if (file2.exists()) {
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(file2))) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    if (json != null) {
                        JsonObject json2 = null;
                        if (json.has("users")) {
                            json2 = json.getAsJsonObject("users");
                        } else if (json.isJsonArray() || json.entrySet().size() > 0) {
                            json2 = json;
                        }

                        if (json2 == null) {
                            return;
                        }

                        for (Map.Entry entry : json2.entrySet()) {
                            String key = (String)entry.getKey();
                            JsonElement val = (JsonElement)entry.getValue();
                            List<BanManager.Entry> list = new ArrayList<>();
                            if (val.isJsonArray()) {
                                for (JsonElement element : val.getAsJsonArray()) {
                                    try {
                                        JsonObject asJsonObject = element.getAsJsonObject();
                                        BanManager.Entry entry2 = new BanManager.Entry();
                                        entry2.setUsername(this.computeString(asJsonObject, "username", key));
                                        entry2.setText(this.computeString(asJsonObject, "banId"));
                                        entry2.setText2(this.computeString(asJsonObject, "blockId"));
                                        entry2.setReason(this.computeString(asJsonObject, "reason", "Unknown"));
                                        entry2.setText3(this.computeString(asJsonObject, "banType", "unknown"));
                                        entry2.setTimestamp(this.computeLong(asJsonObject, "banTimestamp", System.currentTimeMillis()));
                                        entry2.setTimestamp2(this.computeLong(asJsonObject, "unbanTimestamp", 0L));
                                        entry2.setText4(this.computeString(asJsonObject, "duration", "Unknown"));
                                        entry2.setActive(this.matchesJsonObject(asJsonObject, "isActive", true));
                                        list.add(entry2);
                                    } catch (Exception ignored) {
                                    }
                                }
                            }

                            if (!list.isEmpty()) {
                                list.sort((entry3, entry4) -> Long.compare(entry4.getTimestamp(), entry3.getTimestamp()));
                                this.lookupMap.put(key, list);
                            }
                        }

                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.lookupMap.clear();
            }
        }
    }

    public static class Entry {
        private String username2;
        private String text;
        private String text2;
        private String reason2;
        private String text3;
        private long timestamp;
        private long timestamp2;
        private String text4;
        private boolean active;

        public void setText(String text) {
            this.text = text;
        }

        public Entry() {
        }

        public void setText3(String str) {
            this.text3 = str;
        }

        public void setReason(String reason2) {
            this.reason2 = reason2;
        }

        public long getTimestamp2() {
            return this.timestamp2;
        }

        public String w() {
            return this.text2;
        }

        public String getUsername() {
            return this.username2;
        }

        public String x() {
            return this.text;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Entry(String username2, String text, String str, String reason2, String str2, long timestamp, long l, String str3) {
            this.username2 = username2;
            this.text = text;
            this.text2 = str;
            this.reason2 = reason2;
            this.text3 = str2;
            this.timestamp = timestamp;
            this.timestamp2 = l;
            this.text4 = str3;
            this.active = l == 0L || System.currentTimeMillis() < l;
        }

        public void setText4(String str) {
            this.text4 = str;
        }

        public void setTimestamp2(long l) {
            this.timestamp2 = l;
        }

        public String y() {
            return this.text4;
        }

        public void setText2(String str) {
            this.text2 = str;
        }

        public boolean isActive() {
            return this.timestamp2 == 0L || System.currentTimeMillis() < this.timestamp2;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String z() {
            return this.text3;
        }

        public String getReason() {
            return this.reason2;
        }

        public void setUsername(String username2) {
            this.username2 = username2;
        }
    }
}
