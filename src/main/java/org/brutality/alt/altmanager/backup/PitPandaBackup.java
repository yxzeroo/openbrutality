package org.brutality.alt.altmanager.backup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PitPandaBackup {
    private static final File file = new File("brutality/pitpanda_backup");
    private static final File file2 = new File(file, "accounts_backup.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, PitPandaBackup.Entry> lookupMap = new HashMap<>();

    private static void handleEntry(PitPandaBackup.Entry entry, PitPandaBackup.Entry entry2) {
        if (entry != null) {
            entry2.beforeWipe = entry.beforeWipe;
            if (entry2.wipeDetectedDate == null) {
                entry2.wipeDetectedDate = entry.wipeDetectedDate;
            }

            if (entry2.wipeDetectedDate != null && entry.wipeDetectedDate == null && (entry.prestige > 0 || entry.level2 > 0 || entry.playtime > 0)) {
                entry2.beforeWipe = entry;
            }
        }
    }

    public synchronized void handleString(String str, PitPandaBackup.Entry entry) {
        String str2 = str.toLowerCase(Locale.ROOT);
        handleEntry(this.lookupMap.get(str2), entry);
        this.lookupMap.put(str2, entry);
        this.process2();
    }

    public synchronized void handleString(String str) {
        this.lookupMap.remove(str.toLowerCase(Locale.ROOT));
        this.process2();
    }

    public void init() {
        if (!file.exists()) {
            file.mkdirs();
        }

        this.process3();
    }

    public synchronized void process() {
        this.lookupMap.clear();
        this.process2();
    }

    public synchronized PitPandaBackup.Entry computeEntry(String str) {
        return this.lookupMap.get(str.toLowerCase(Locale.ROOT));
    }

    private void process2() {
        try {
            JsonObject json = new JsonObject();

            for (Map.Entry entry : this.lookupMap.entrySet()) {
                JsonObject asJsonObject = gson.toJsonTree(entry.getValue()).getAsJsonObject();
                json.add((String)entry.getKey(), asJsonObject);
            }

            json.addProperty("lastUpdated", System.currentTimeMillis());
            Path path = file2.toPath();
            Files.createDirectories(path.getParent());
            Path path2 = Files.createTempFile(path.getParent(), "accounts-", ".tmp");

            try {
                Files.writeString(path2, gson.toJson((JsonElement)json), StandardCharsets.UTF_8);

                try {
                    Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(path2);
            }
        } catch (Exception ignored) {
        }
    }

    private void process3() {
        if (file2.exists()) {
            try {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file2), StandardCharsets.UTF_8))) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    if (json != null && json.has("lastUpdated")) {
                        for (Map.Entry entry : json.entrySet()) {
                            String key = (String)entry.getKey();
                            if (!key.equals("lastUpdated")) {
                                try {
                                    JsonObject asJsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
                                    PitPandaBackup.Entry entry2 = gson.fromJson(asJsonObject, PitPandaBackup.Entry.class);
                                    this.lookupMap.put(key, entry2);
                                } catch (Exception ignored) {
                                }
                            }
                        }

                        return;
                    }
                }
            } catch (Exception ignored2) {
            }
        }
    }

    public synchronized boolean matchesString(String str) {
        return this.computeEntry(str) != null;
    }

    public static class Entry {
        private String username2;
        private String currentUsername;
        public int prestige;
        public int level2;
        private String formattedLevel;
        private String formattedName;
        private String formattedRank;
        private String rank;
        public int playtime;
        private String status;
        private String punishments;
        private boolean isScammerTagged;
        private String scammerReason;
        private long lastLogout;
        private double currentGold;
        private int currentRenown;
        private boolean isBanned;
        private boolean isHeresyUnlocked;
        public Long wipeDetectedDate;
        public PitPandaBackup.Entry beforeWipe;
        private long backupTimestamp;
        private static final long timestamp = 86400000L;

        public long getLastLogout() {
            return this.lastLogout;
        }

        public boolean isBanned() {
            return this.isBanned;
        }

        public double getCurrentGold() {
            return this.currentGold;
        }

        public Entry() {
        }

        public String getUsername() {
            return this.username2;
        }

        public String getStatus() {
            return this.status;
        }

        public Entry(
            String username2,
            String currentUsername,
            int prestige,
            int level2,
            String formattedLevel,
            String formattedName,
            String formattedRank,
            String rank,
            int playtime,
            String status,
            String punishments,
            boolean isScammerTagged,
            String scammerReason,
            long lastLogout,
            double currentGold,
            int currentRenown,
            boolean isBanned,
            boolean isHeresyUnlocked,
            Long wipeDetectedDate
        ) {
            this.username2 = username2;
            this.currentUsername = currentUsername;
            this.prestige = prestige;
            this.level2 = level2;
            this.formattedLevel = formattedLevel;
            this.formattedName = formattedName;
            this.formattedRank = formattedRank;
            this.rank = rank;
            this.playtime = playtime;
            this.status = status;
            this.punishments = punishments;
            this.isScammerTagged = isScammerTagged;
            this.scammerReason = scammerReason;
            this.lastLogout = lastLogout;
            this.currentGold = currentGold;
            this.currentRenown = currentRenown;
            this.isBanned = isBanned;
            this.isHeresyUnlocked = isHeresyUnlocked;
            this.wipeDetectedDate = wipeDetectedDate;
            this.backupTimestamp = System.currentTimeMillis();
        }

        public boolean checkState() {
            long l = System.currentTimeMillis() - this.backupTimestamp;
            return l > timestamp;
        }

        public String getFormattedRank() {
            return this.formattedRank;
        }

        public String getFormattedLevel() {
            return this.formattedLevel;
        }

        public int getLevel() {
            return this.level2;
        }

        public Long getWipeDetectedDate() {
            return this.wipeDetectedDate;
        }

        public int getPrestige() {
            return this.prestige;
        }

        public String getCurrentUsername() {
            return this.currentUsername;
        }

        public int getCurrentRenown() {
            return this.currentRenown;
        }

        public String getScammerReason() {
            return this.scammerReason;
        }

        public String getFormattedName() {
            return this.formattedName;
        }

        public String getRank() {
            return this.rank;
        }

        public boolean isHeresyUnlocked() {
            return this.isHeresyUnlocked;
        }

        public boolean isScammerTagged() {
            return this.isScammerTagged;
        }

        public int getPlaytime() {
            return this.playtime;
        }

        public String getPunishments() {
            return this.punishments;
        }

        public PitPandaBackup.Entry getBeforeWipe() {
            return this.beforeWipe;
        }

        public long getBackupTimestamp() {
            return this.backupTimestamp;
        }
    }
}
