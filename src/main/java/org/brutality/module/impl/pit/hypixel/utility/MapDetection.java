package org.brutality.module.impl.pit.hypixel.utility;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class MapDetection extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final BooleanSetting autoDetectSetting = new BooleanSetting("Auto Detect", this, true);
    private final BooleanSetting showRotationSetting = new BooleanSetting("Show Rotation", this, true);
    private final BooleanSetting showCountdownSetting = new BooleanSetting("Show Countdown", this, true);
    private final BooleanSetting announceOnWorldChangeSetting = new BooleanSetting("Announce On World Change", this, true);
    private String text = null;
    private long timestamp = 0L;
    private World world;
    private long timestamp2;
    private static final long timestamp3 = 5000L;
    private final List<String> entries = Arrays.asList("Elements", "Castle", "Corals", "Genesis", "Four Seasons");
    private static final long timestamp4 = 604800000L;
    private static final long timestamp5 = 1729008000000L;

    @Override
    public void onDisable() {
        this.world = null;
        this.timestamp2 = 0L;
        this.text = null;
        super.onDisable();
    }

    private void handleString(String str) {
        MessageUtils.x("§a§l✓ Map Changed to: §f§l" + str);
        if (this.showRotationSetting.isEnabled()) {
            String str2 = this.computeString2(str);
            MessageUtils.x("§6§lNext Map: §f" + str2);
        }

        if (this.showCountdownSetting.isEnabled()) {
            String str3 = this.computeString();
            MessageUtils.x("§6§lNext Change In: §f" + str3);
        }
    }

    public String computeString2() {
        return this.computeString4();
    }

    private String computeString(String str) {
        if (str == null) {
            return null;
        }
        String str2 = str.toLowerCase();
        if (str2.contains("element") || str2.contains("fire") || str2.contains("water") || str2.contains("earth") || str2.contains("air")) {
            return "Elements";
        }
        if (str2.contains("season") || str2.contains("spring") || str2.contains("summer") || str2.contains("autumn") || str2.contains("winter")) {
            return "Four Seasons";
        }
        if (str2.contains("coral") || str2.contains("ocean") || str2.contains("sea")) {
            return "Corals";
        }
        if (str2.contains("genesis") || str2.contains("void") || str2.contains("space")) {
            return "Genesis";
        }
        if (!str2.contains("king") && !str2.contains("castle") && !str2.contains("medieval")) {
            switch (str2) {
                case "elemental":
                    return "Elements";
                case "seasons":
                    return "Four Seasons";
                case "corals":
                    return "Corals";
                case "genesis":
                    return "Genesis";
                case "kings":
                    return "Castle";
                default:
                    return null;
            }
        } else {
            return "Castle";
        }
    }

    private void process3() {
        MessageUtils.x("§6§lMap Rotation Order:");

        for (int i = 0; i < this.entries.size(); i++) {
            String str = this.entries.get(i);
            String str2 = str.equals(this.text) ? "§a§l→ " : "§7  ";
            String str3 = String.valueOf(i + 1);
            MessageUtils.x(str2 + "§6" + str3 + ". §f" + str);
        }
    }

    private String computeString5() {
        if (mc.theWorld == null) {
            return null;
        }
        try {
            String str = this.computeString4();
            String str2 = FreshHelper.computeString(mc.theWorld);
            String str3 = this.computeString(str2);
            if (str3 != null && str3.equals(str)) {
                return str3;
            }
            if (mc.isSingleplayer()) {
                String worldName = mc.theWorld.getWorldInfo().getWorldName();
                str3 = this.computeString(worldName);
                if (str3 != null) {
                    return str3;
                }
            }

            return str;
        } catch (Exception e) {
            return this.computeString4();
        }
    }

    public MapDetection() {
        super("Map Detection", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.autoDetectSetting, this.showRotationSetting, this.showCountdownSetting, this.announceOnWorldChangeSetting});
    }

    public void process5() {
        this.process6();
    }

    public String computeString3() {
        return this.text != null ? this.text : this.computeString4();
    }

    public void process4() {
        MessageUtils.x("§6§l=== Hypixel Pit Map Information ===");
        MessageUtils.x("§fMaps change every §6Tuesday evening §f(6 PM UTC / 18:00)");
        MessageUtils.x("§fRotation: §7Elements → Castle → Corals → Genesis → Four Seasons");
        String str = this.computeString4();
        if (str != null) {
            MessageUtils.x("§fCurrent: §a" + str);
            MessageUtils.x("§fNext: §6" + this.computeString2(str));
            MessageUtils.x("§fTime until change: §e" + this.computeString());
        } else {
            MessageUtils.x("§cNo map detected yet. Try using force detection.");
        }
    }

    private String computeString2(String str) {
        int n = this.entries.indexOf(str);
        if (n == -1) {
            return "Unknown";
        }
        int n2 = (n + 1) % this.entries.size();
        return this.entries.get(n2);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof RenderEvent4 renderEvent4) {
                    this.world = null;
                    this.timestamp2 = 0L;
                    this.text = null;
                    long now = System.currentTimeMillis();
                    if (this.announceOnWorldChangeSetting.isEnabled() && renderEvent4.world != null && renderEvent4.world != renderEvent4.lastWorld && now - this.timestamp > 5000L) {
                        this.timestamp = now;
                        this.world = renderEvent4.world;
                        this.timestamp2 = System.nanoTime() / 1000000L + 2000L;
                    }
                } else if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE && this.world != null) {
                    if (mc.theWorld != this.world) {
                        this.world = null;
                        this.timestamp2 = 0L;
                        return;
                    }

                    if (System.nanoTime() / 1000000L < this.timestamp2) {
                        return;
                    }

                    this.world = null;
                    this.timestamp2 = 0L;
                    if (mc.thePlayer != null) {
                        this.process6();
                    }
                }
            }
        }
    }

    private String computeString4() {
        long now = System.currentTimeMillis();
        long l = now - 1729008000000L;
        int n = (int)(l / 604800000L % this.entries.size());
        if (n < 0) {
            n = 0;
        }

        return this.entries.get(n);
    }

    private void process6() {
        if (!mc.isCallingFromMinecraftThread()) {
            WorldClient world = mc.theWorld;
            mc.addScheduledTask(() -> {
                if (world != null && mc.theWorld == world) {
                    this.process6();
                }
            });
        } else if (mc.theWorld != null && mc.thePlayer != null) {
            if (FreshHelper.computeString(mc.theWorld) != null) {
                String str = this.computeString5();
                if (str == null) {
                    MessageUtils.x("§c§lMap Detection: §fUnable to detect current map!");
                } else {
                    this.text = str;
                    MessageUtils.x("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    MessageUtils.x("§6§l           MAP DETECTION");
                    MessageUtils.x("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    MessageUtils.x("§6§lCurrent Map: §f§l" + str);
                    if (this.showRotationSetting.isEnabled()) {
                        String str2 = this.computeString2(str);
                        MessageUtils.x("§6§lNext Map: §f" + str2);
                    }

                    if (this.showCountdownSetting.isEnabled()) {
                        String str3 = this.computeString();
                        MessageUtils.x("§6§lMap Changes In: §f" + str3);
                    }

                    if (this.showRotationSetting.isEnabled()) {
                        this.process3();
                    }

                    MessageUtils.x("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                }
            }
        }
    }

    private String computeString() {
        try {
            Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            Calendar instance2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            instance2.setTime(instance.getTime());
            int n = instance.get(7);
            int n2 = (3 - n + 7) % 7;
            if (n2 == 0) {
                n2 = 7;
            }

            instance2.add(5, n2);
            instance2.set(11, 18);
            instance2.set(12, 0);
            instance2.set(13, 0);
            instance2.set(14, 0);
            if (n == 3 && instance.get(11) >= 18) {
                instance2.add(3, 1);
            } else if (n <= 3 && n == 3 && instance.get(11) >= 18) {
            }

            long l = instance2.getTimeInMillis() - instance.getTimeInMillis();
            if (l <= 0L) {
                return "Map should change soon!";
            }
            long l2 = l / 1000L;
            long l3 = l2 / 86400L;
            long l4 = l2 % 86400L / 3600L;
            long l5 = l2 % 3600L / 60L;
            long l6 = l2 % 60L;
            StringBuilder sb = new StringBuilder();
            if (l3 > 0L) {
                sb.append(l3).append("d ");
            }

            if (l4 > 0L) {
                sb.append(l4).append("h ");
            }

            if (l5 > 0L) {
                sb.append(l5).append("m ");
            }

            if (l6 > 0L) {
                sb.append(l6).append("s");
            }

            return sb.toString().trim();
        } catch (Exception e) {
            return "Unable to calculate";
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.world = mc.theWorld;
            this.timestamp2 = System.nanoTime() / 1000000L + 1000L;
        }
    }

    public void process() {
        this.text = null;
        MessageUtils.x("§e§lMap Detection: §fDetection state reset.");
    }
}
