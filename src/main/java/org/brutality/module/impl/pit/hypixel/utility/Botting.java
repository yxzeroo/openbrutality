package org.brutality.module.impl.pit.hypixel.utility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.StringSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class Botting extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final BooleanSetting autoEscapeSetting = new BooleanSetting("Auto Escape", this, true);
    private final ModeSetting lobbySetting = new ModeSetting("Escape Mode", this, "Auto Leave", Arrays.asList("Auto Leave", "Limbo", "Lobby"))
        .computeModeSetting(() -> this.autoEscapeSetting.isEnabled());
    private final StringSetting pcLimboSetting = new StringSetting("Limbo Command", this, "/pc limbo")
        .computeStringSetting(() -> this.autoEscapeSetting.isEnabled() && this.lobbySetting.matchesString2("Limbo"));
    private final Set<String> itemSet = new HashSet<>();
    private boolean flag = false;
    private int count = -1;
    private boolean flag2 = false;

    private boolean checkState() {
        if (mc.getNetHandler() == null) {
            return false;
        }
        for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
            String str = playerInfo.getGameProfile().getName().toLowerCase();
            if (!this.itemSet.contains(str)) {
                mc.thePlayer.addChatMessage(this.computeIChatComponent("§c[Botting] §fUnknown player in tab: §e" + str));
                return true;
            }
        }

        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.itemSet.clear();
        this.flag = false;
        this.count = -1;
    }

    public Botting() {
        super("Botting", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.autoEscapeSetting, this.lobbySetting, this.pcLimboSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    if (event instanceof RenderEvent4) {
                        this.itemSet.clear();
                        this.flag = false;
                        this.flag2 = false;
                        this.count = -1;
                        this.handleString("/p list");
                    } else if (event instanceof EventUpdate) {
                        this.onUpdate();
                    } else if (event instanceof ChatEvent chatEvent) {
                        this.handleIChatComponent(chatEvent.message);
                    }
                }
            }
        }
    }

    private void handleIChatComponent(IChatComponent component) {
        if (component != null) {
            String text = component.getUnformattedText();
            if (text.contains("Party") && !this.flag) {
                if (mc.thePlayer != null) {
                    this.itemSet.add(mc.thePlayer.getName().toLowerCase());
                }

                String str = text.replaceAll("\\[.*?\\]", "").replaceAll("●", "").trim();
                int n = str.indexOf(58);
                if (n >= 0) {
                    str = str.substring(n + 1).trim();
                }

                String[] parts = str.split(",");

                for (String str2 : parts) {
                    String str3 = str2.trim();
                    if (!str3.isEmpty() && str3.matches("[A-Za-z0-9_]+")) {
                        this.itemSet.add(str3.toLowerCase());
                    }
                }

                if (!this.itemSet.isEmpty()) {
                    this.flag = true;
                    mc.thePlayer.addChatMessage(this.computeIChatComponent("§a[Botting] §fTracking " + this.itemSet.size() + " player(s): " + this.itemSet));
                }
            }
        }
    }

    private void handleString(String str) {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(str));
        }
    }

    private IChatComponent computeIChatComponent(String str) {
        return ChatComponentText.class.cast(new ChatComponentText(str));
    }

    private void process() {
        String text = this.lobbySetting.getText();
        if (text.equalsIgnoreCase("Auto Leave")) {
            switch (this.count) {
                case 0:
                    this.handleString("/lobby");
                    break;
                case 1:
                    this.handleString("/p warp");
                    break;
                case 2:
                    this.handleString("/p warp");
                    this.count = -1;
                    return;
            }
        } else if (text.equalsIgnoreCase("Limbo")) {
            if (this.count == 0) {
                String val = this.pcLimboSetting.getValue();
                if (val == null || val.isEmpty()) {
                    val = "/pc limbo";
                }

                this.handleString(val);
                this.count = -1;
                return;
            }
        } else if (text.equalsIgnoreCase("Lobby") && this.count == 0) {
            this.handleString("/pc lobby");
            this.count = -1;
            return;
        }

        this.count++;
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.itemSet.clear();
            this.flag = false;
            this.flag2 = false;
            this.count = -1;
        }
    }

    @Override
    public void onUpdate() {
        if (this.count >= 0) {
            this.process();
        } else if (this.flag && !this.flag2 && this.autoEscapeSetting.isEnabled()) {
            if (this.checkState()) {
                mc.thePlayer.addChatMessage(this.computeIChatComponent("§c[Botting] §fIntruder detected! Escaping..."));
                this.flag2 = true;
                this.count = 0;
            }
        }
    }
}
