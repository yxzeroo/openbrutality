package org.brutality.module.impl.client;

import lombok.Generated;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.extensions.DiscordRP;
// VENDOR-DISABLED: import org.brutality.extensions.LauncherPresenceBridge;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.StringSetting;
import org.brutality.utils.hypixel.BanManager;

public class DiscordRPC extends Module {
    private static DiscordRPC instance;
    private final BooleanSetting enabledSetting = new BooleanSetting("Enabled", this, true);
    private final BooleanSetting showBanStatusSetting = new BooleanSetting("Show Ban Status", this, false);
    private final StringSetting autoSetting = new StringSetting("Status Line", this, "Auto");
    private final StringSetting inTheBrutalityLauncherSetting = new StringSetting("Detail Line", this, "In the brutality launcher");
    private DiscordRP discordRP;
    private boolean flag = true;
    private long timestamp;
    private boolean r;
    private long timestamp2;

    public DiscordRPC() {
        super("DiscordRPC", 0, Category.CLIENT);
        this.setRequiresInGame(false);
        this.handleSettingArray(new Setting[]{this.enabledSetting, this.showBanStatusSetting, this.autoSetting, this.inTheBrutalityLauncherSetting});
        instance = this;
        this.process29();
        this.handleFlag3(true);
    }

    public static void process3() {
        // VENDOR-DISABLED: LauncherPresenceBridge.matchesString(...) launcher IPC presence file sync
        if (instance != null) {
            instance.handleFlag(false);
        }
    }

    public boolean isActive() {
        return this.discordRP != null && this.enabledSetting.isEnabled();
    }

    public static boolean checkState() {
        return instance != null && instance.showBanStatusSetting.isEnabled();
    }

    public static String getStatusLine() {
        String str = instance != null ? instance.autoSetting.getValue() : "Auto";
        if (str != null && !str.isBlank() && !"Auto".equalsIgnoreCase(str) && !"Playing on brutality.lol".equals(str)) {
            return str;
        }
        // VENDOR-DISABLED: LauncherPresenceBridge.computeString(...)
        Minecraft mc = Minecraft.getMinecraft();
        String serverIp = mc != null && mc.theWorld != null && !mc.isSingleplayer() && mc.getCurrentServerData() != null
            ? mc.getCurrentServerData().serverIP
            : null;
        return serverIp != null && !serverIp.isBlank() ? "Playing (" + serverIp + ")" : "Idle";
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.process();
    }

    public static String computeString() {
        String str = instance != null ? instance.inTheBrutalityLauncherSetting.getValue() : null;
        return str != null && !str.isBlank() && !"Best Pit Client".equals(str) ? str : "In the brutality launcher";
    }

    @Override
    public void onDisable() {
        this.handleFlag3(true);
    }

    private void process4() {
        if (this.discordRP != null) {
            try {
                this.discordRP.shutdown();
            } catch (Exception ignored) {
            }

            this.discordRP = null;
        }
    }

    @Override
    public void onEvent(Event event) {
    }

    public static boolean checkState2() {
        return instance != null && instance.enabledSetting.isEnabled();
    }

    private void handleFlag(boolean flag) {
        this.r = flag;
        long now = System.currentTimeMillis();
        if (now >= this.timestamp) {
            this.timestamp = now + 1000L;
            if (flag) {
                this.process4();
            } else {
                if (this.enabledSetting.isEnabled() != this.flag) {
                    this.flag = this.enabledSetting.isEnabled();
                    if (this.flag) {
                        this.process();
                    } else {
                        this.process4();
                    }
                }

                if (this.enabledSetting.isEnabled() && this.discordRP == null) {
                    this.process();
                }

                if (this.discordRP != null && this.flag) {
                    try {
                        if (this.showBanStatusSetting.isEnabled()) {
                            String[] parts = this.computeStringArray();
                            if (parts != null) {
                                this.discordRP.handleString(parts[0], parts[1]);
                                return;
                            }
                        }

                        this.discordRP.handleString(getStatusLine(), computeString());
                    } catch (Exception e) {
                        this.process();
                    }
                }
            }
        }
    }

    @Generated
    public static DiscordRPC getInstance() {
        return instance;
    }

    @Override
    public boolean checkState3() {
        return true;
    }

    private String[] computeStringArray() {
        try {
            if (Brutality.getBanManager() == null) {
                return null;
            }
            String username = Minecraft.getMinecraft().getSession().getUsername();
            BanManager.Entry entry = Brutality.getBanManager().computeEntry(username);
            if (entry != null && entry.isActive()) {
                String str = entry.z();
                String str2;
                if ("temporary".equals(str)) {
                    str2 = "Temporarily Banned";
                } else if ("block".equals(str)) {
                    str2 = "Account Blocked";
                } else if ("recovery".equals(str)) {
                    str2 = "Recovery Phase";
                } else {
                    str2 = "Permanently Banned";
                }

                String str3;
                if (entry.getTimestamp2() == 0L) {
                    str3 = "Permanent";
                } else {
                    long l = entry.getTimestamp2() - System.currentTimeMillis();
                    if (l <= 0L) {
                        str3 = "Expired";
                    } else {
                        long l2 = l / 3600000L;
                        long l3 = l % 3600000L / 60000L;
                        str3 = l2 > 0L ? l2 + "h " + l3 + "m left" : l3 + "m left";
                    }
                }

                return new String[]{str2, str3};
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void handleString(String str, String str2) {
        if (this.discordRP != null && this.enabledSetting.isEnabled()) {
            try {
                this.discordRP.handleString(str, str2);
            } catch (Exception ignored) {
            }
        }
    }

    private void process() {
        // VENDOR-DISABLED: LauncherPresenceBridge.matchesString(null)

        if (this.enabledSetting.isEnabled() && !this.r) {
            if (System.currentTimeMillis() >= this.timestamp2) {
                if (this.discordRP != null) {
                    try {
                        this.discordRP.handleString(getStatusLine(), computeString());
                        return;
                    } catch (Exception e) {
                        this.process4();
                    }
                }

                try {
                    Class.forName("net.arikia.dev.drpc.callbacks.ReadyCallback");
                    this.discordRP = new DiscordRP();
                    this.discordRP.start();
                    this.discordRP.handleString(getStatusLine(), computeString());
                } catch (ClassNotFoundException e2) {
                    this.discordRP = null;
                    this.timestamp2 = System.currentTimeMillis() + 30000L;
                } catch (LinkageError | Exception e3) {
                    this.discordRP = null;
                    this.timestamp2 = System.currentTimeMillis() + 30000L;
                }
            }
        } else {
            this.process4();
        }
    }
}
