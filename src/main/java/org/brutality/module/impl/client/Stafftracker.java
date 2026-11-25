package org.brutality.module.impl.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.system.stafftracker.StaffTrackerService;
import org.brutality.system.stafftracker.StaffTrackerService.AdminMode;
import org.brutality.system.stafftracker.StaffTrackerService.Entry;
import org.brutality.system.stafftracker.StaffTrackerService.Entry2;
import org.brutality.system.stafftracker.StaffTrackerService.OnlineMode;
import org.brutality.utils.network.ServerUtils;

public class Stafftracker extends Module {
    private static final long timestamp = 60000L;
    private static final long timestamp2 = 15000L;
    private static final String text = "Mode";
    private static final String text2 = "Allow Notifications";
    private static final String text3 = "Notify Admins";
    private final ModeSetting classicPitSetting = new ModeSetting("Mode", this, "Hypixel", Arrays.asList("Hypixel", "Classic Pit"));
    private final BooleanSetting allowNotificationsSetting = new BooleanSetting("Allow Notifications", this, false);
    private final BooleanSetting notifyAdminsSetting = new BooleanSetting("Notify Admins", this, false)
        .computeBooleanSetting(() -> this.allowNotificationsSetting.isEnabled());
    private final Map<String, BooleanSetting> lookupMap = new LinkedHashMap<>();
    private final Map<String, Boolean> lookupMap2 = new ConcurrentHashMap<>();
    private final Map<String, OnlineMode> lookupMap3 = new ConcurrentHashMap<>();
    private final Map<String, AdminMode> lookupMap4 = new ConcurrentHashMap<>();
    private final Set<String> itemSet = ConcurrentHashMap.newKeySet();
    private long timestamp3;
    private long timestamp4;
    private volatile boolean flag;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;

    private Boolean computeBooleanType(String str) {
        try {
            if (Brutality.appdataHandler == null) {
                return null;
            }
            String text = Brutality.appdataHandler.getText();
            if (text == null || text.isEmpty()) {
                return null;
            }
            File file = new File(Brutality.appdataHandler.computeFile(), text + ".json");
            if (!file.isFile()) {
                return null;
            }
            Boolean val;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                JsonObject asJsonObject = new JsonParser().parse(reader).getAsJsonObject();
                if (asJsonObject == null || !asJsonObject.has(this.getName())) {
                    return null;
                }

                JsonObject asJsonObject2 = asJsonObject.getAsJsonObject(this.getName());
                if (asJsonObject2 == null || !asJsonObject2.has("settings")) {
                    return null;
                }

                JsonObject asJsonObject3 = asJsonObject2.getAsJsonObject("settings");
                if (asJsonObject3 == null || !asJsonObject3.has(str)) {
                    return null;
                }

                JsonElement element = asJsonObject3.get(str);
                if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
                    return null;
                }

                val = element.getAsBoolean();
            }

            return val;
        } catch (Exception e) {
            return null;
        }
    }

    private void handleFlag(boolean flag) {
        for (String str : this.lookupMap.keySet()) {
            OnlineMode onlineMode = this.matchesString(str) ? OnlineMode.ONLINE : OnlineMode.OFFLINE;
            OnlineMode onlineMode2 = this.lookupMap3.put(str, onlineMode);
            if (flag && onlineMode2 != null && onlineMode2 != onlineMode && matchesOnlineMode(onlineMode2, onlineMode)) {
                this.handleString(str, onlineMode, this.lookupMap4.get(str));
            }
        }
    }

    private boolean matchesString(String str) {
        for (String str2 : this.itemSet) {
            if (str2.equalsIgnoreCase(str)) {
                return true;
            }
        }

        return false;
    }

    private static OnlineMode computeOnlineMode(OnlineMode onlineMode) {
        return onlineMode == OnlineMode.ONLINE ? OnlineMode.ONLINE : OnlineMode.OFFLINE;
    }

    private static boolean matchesString3(String str) {
        return "Mode".equalsIgnoreCase(str) || "Allow Notifications".equalsIgnoreCase(str) || "Notify Admins".equalsIgnoreCase(str);
    }

    private boolean checkState() {
        return this.classicPitSetting.matchesString2("Classic Pit");
    }

    private static long computeLong() {
        return System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        this.handleFlag3(true);
    }

    private void handleString(String str, OnlineMode onlineMode, AdminMode adminMode) {
        if (this.allowNotificationsSetting.isEnabled()) {
            boolean flag = false;
            BooleanSetting boolSetting = this.lookupMap.get(str);
            if (boolSetting != null && boolSetting.isEnabled()) {
                flag = true;
            }

            if (this.notifyAdminsSetting.isEnabled() && adminMode == AdminMode.ADMIN) {
                flag = true;
            }

            if (flag) {
                String str2 = onlineMode == OnlineMode.ONLINE ? "online" : "offline";
                NotificationFeedHandler.handleString(str + " is now " + str2);
            }
        }
    }

    private static boolean matchesOnlineMode(OnlineMode onlineMode, OnlineMode onlineMode2) {
        boolean flag = onlineMode == OnlineMode.ONLINE;
        boolean flag2 = onlineMode2 == OnlineMode.ONLINE;
        return flag != flag2;
    }

    public Stafftracker() {
        super("Stafftracker", 0, Category.CLIENT);
        this.setRequiresInGame(false);
        this.handleSettingArray(new Setting[]{this.classicPitSetting, this.allowNotificationsSetting, this.notifyAdminsSetting});
        this.process29();
        this.handleFlag3(true);
    }

    private boolean matchesString2(String str) {
        if (this.lookupMap2.containsKey(str)) {
            return Boolean.TRUE.equals(this.lookupMap2.get(str));
        }
        Boolean val = this.computeBooleanType(str);
        if (val != null) {
            this.lookupMap2.put(str, val);
            return val;
        }
        return false;
    }

    @Override
    public boolean checkState3() {
        return true;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventPacketReceive) {
            this.onEventPacketReceive((EventPacketReceive)event);
        } else if (event instanceof EventUpdate) {
            if (!this.checkState44()) {
                this.handleFlag3(true);
            }

            long now = System.currentTimeMillis();
            boolean flag = this.classicPitSetting.matchesString2("Classic Pit");
            if (flag != this.flag4) {
                this.flag4 = flag;
                this.flag3 = false;
                this.itemSet.clear();
                this.timestamp4 = 0L;
            }

            if (flag && ServerUtils.checkState5() && now - this.timestamp4 >= 15000L) {
                this.process();
                this.timestamp4 = now;
            }

            if (!this.flag && now - this.timestamp3 >= 60000L) {
                this.timestamp3 = now;
                this.flag = true;
                StaffTrackerService.fetchAsync(flag, entry -> {
                    Minecraft mc = Minecraft.getMinecraft();
                    if (mc != null) {
                        mc.addScheduledTask(() -> {
                            try {
                                this.handleEntry(entry);
                            } finally {
                                this.flag = false;
                            }
                        });
                    } else {
                        try {
                            this.handleEntry(entry);
                        } finally {
                            this.flag = false;
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timestamp3 = 0L;
        this.timestamp4 = 0L;
        this.flag4 = false;
        this.flag3 = false;
    }

    private void onEventPacketReceive(EventPacketReceive event) {
        if (this.checkState() && ServerUtils.checkState5() && event.getPacket() instanceof S3APacketTabComplete) {
            String[] parts = ((S3APacketTabComplete)event.getPacket()).func_149630_c();
            if (parts != null) {
                this.itemSet.clear();

                for (String str : parts) {
                    if (str != null) {
                        String str2 = str.trim();
                        if (str2.startsWith("/")) {
                            str2 = str2.substring(1);
                        }

                        if (!str2.isEmpty()) {
                            this.itemSet.add(str2);
                        }
                    }
                }

                boolean flag = this.flag3;
                this.flag3 = true;
                this.handleFlag(flag);
            }
        }
    }

    private void handleEntry(Entry entry) {
        if (entry != null && entry.ok && entry.entries != null) {
            for (java.util.Map.Entry entry2 : this.lookupMap.entrySet()) {
                this.lookupMap2.put((String)entry2.getKey(), ((BooleanSetting)entry2.getValue()).isEnabled());
            }

            HashSet set = new HashSet();

            for (Entry2 entry22 : entry.entries) {
                if (entry22 != null && entry22.text != null && !entry22.text.trim().isEmpty()) {
                    String str = entry22.text.trim();
                    if (!matchesString3(str)) {
                        set.add(str);
                        this.lookupMap4.put(str, entry22.adminMode);
                        BooleanSetting boolSetting = this.lookupMap.get(str);
                        if (boolSetting == null) {
                            boolean flag = this.matchesString2(str);
                            boolSetting = new BooleanSetting(str, this, flag).computeBooleanSetting(() -> this.allowNotificationsSetting.isEnabled());
                            Brutality.settingsManager.addSetting(boolSetting);
                            this.lookupMap.put(str, boolSetting);
                            this.lookupMap2.put(str, flag);
                        }

                        OnlineMode onlineMode;
                        if (this.checkState()) {
                            onlineMode = this.flag3 && this.matchesString(str) ? OnlineMode.ONLINE : OnlineMode.OFFLINE;
                        } else {
                            onlineMode = computeOnlineMode(entry22.onlineMode);
                        }

                        OnlineMode onlineMode2 = this.lookupMap3.put(str, onlineMode);
                        if (this.flag2 && onlineMode2 != null && onlineMode2 != onlineMode && matchesOnlineMode(onlineMode2, onlineMode)) {
                            this.handleString(str, onlineMode, entry22.adminMode);
                        }
                    }
                }
            }

            ArrayList<String> list = new ArrayList<>();

            for (String str2 : this.lookupMap.keySet()) {
                if (!set.contains(str2)) {
                    list.add(str2);
                }
            }

            for (String str3 : list) {
                BooleanSetting boolSetting2 = this.lookupMap.remove(str3);
                if (boolSetting2 != null) {
                    this.lookupMap2.put(str3, boolSetting2.isEnabled());
                    Brutality.settingsManager.handleSetting(boolSetting2);
                }

                this.lookupMap3.remove(str3);
                this.lookupMap4.remove(str3);
            }

            if (!this.flag2) {
                this.flag2 = true;
            }

            if (this.checkState() && ServerUtils.checkState5() && computeLong() - this.timestamp4 >= 15000L) {
                this.process();
                this.timestamp4 = computeLong();
            }
        }
    }

    private void process() {
        if (mc.thePlayer != null && mc.getNetHandler() != null) {
            mc.getNetHandler().addToSendQueue(new C14PacketTabComplete("/view "));
        }
    }
}
