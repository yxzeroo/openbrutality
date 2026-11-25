package org.brutality.module.impl.pit.orion.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;

public class OrionAntiVanish extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final NumberSetting xSetting = new NumberSetting("X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 120.0, 0.0, 1080.0, 1.0);
    private final NumberSetting checkIntervalSecondsSetting = new NumberSetting("Check Interval (seconds)", this, 30.0, 30.0, 300.0, 1.0);
    private int count;
    private boolean flag;
    private final List<String> entries = new ArrayList<>();

    @Override
    public void onDisable() {
        HudDragHelper.handleObject(this);
        this.flag = false;
        this.entries.clear();
        super.onDisable();
    }

    private void process() {
        if (mc.thePlayer != null && mc.getNetHandler() != null) {
            mc.getNetHandler().addToSendQueue(new C14PacketTabComplete("/minecraft:tell "));
            this.flag = true;
        }
    }

    @Override
    public void onRender() {
        if (this.checkState44()) {
            var entries = this.entries;
            int n = (int)this.xSetting.computeDouble();
            int n2 = (int)this.ySetting.computeDouble();
            short s = 150;
            int n3 = 18 + Math.max(1, entries.size()) * 12;
            if (mc.currentScreen instanceof GuiChat) {
                int[] ints = HudDragHelper.computeIntArray();
                if (Mouse.isButtonDown(0) && !HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(ints[0], ints[1], n, n2, s, n3)) {
                    HudDragHelper.matchesObject(this, ints[0], ints[1], n, n2);
                } else if (!Mouse.isButtonDown(0)) {
                    HudDragHelper.handleObject(this);
                }

                if (HudDragHelper.matchesObject(this)) {
                    int[] ints2 = HudDragHelper.computeIntArray(this);
                    if (ints2 != null) {
                        this.xSetting.handleDouble2(ints2[0]);
                        this.ySetting.handleDouble2(ints2[1]);
                        n = ints2[0];
                        n2 = ints2[1];
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            Gui.drawRect(n, n2, n + s, n2 + n3, -1072689128);
            Gui.drawRect(n, n2, n + s, n2 + 1, -2080704);
            computeFontRenderer(OrionAntiVanish.class).drawStringWithShadow("§cVanished Players", n + 6, n2 + 4, -1);
            if (entries.isEmpty()) {
                computeFontRenderer(OrionAntiVanish.class).drawStringWithShadow("§7None detected", n + 6, n2 + 16, -1);
            } else {
                int n4 = n2 + 16;

                for (String str : entries) {
                    computeFontRenderer(OrionAntiVanish.class).drawStringWithShadow("§e" + str, n + 6, n4, -1);
                    n4 += 12;
                }
            }
        }
    }

    public OrionAntiVanish() {
        super("Orion AntiVanish", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xSetting, this.ySetting, this.checkIntervalSecondsSetting});
    }

    public boolean checkState() {
        return !this.entries.isEmpty();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.count = 0;
        this.entries.clear();
        this.process();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPacketEvent && ((GetPacketEvent)event).getPacket() instanceof S3APacketTabComplete) {
                this.handleS3APacketTabComplete(((GetPacketEvent)event).getPacket());
            } else {
                if (event instanceof EventTick
                    && ((EventTick)event).getState() == EventState.PRE
                    && !this.flag
                    && ++this.count >= this.checkIntervalSecondsSetting.computeDouble() * 20.0) {
                    this.count = 0;
                    this.process();
                }
            }
        }
    }

    private void handleS3APacketTabComplete(S3APacketTabComplete packet) {
        String[] parts = packet.func_149630_c();
        ArrayList<String> list = new ArrayList<>();

        for (String str : parts) {
            if (str.toLowerCase().startsWith("/minecraft:tell ")) {
                String str2 = str.substring("/minecraft:tell ".length()).trim();
                if (!str2.isEmpty()) {
                    list.add(str2);
                }
            } else if (!str.contains(" ") && !str.startsWith("/")) {
                list.add(str);
            }
        }

        List list2 = new ArrayList();
        var playerInfoMap = mc.getNetHandler().getPlayerInfoMap();
        if (playerInfoMap != null) {
            for (NetworkPlayerInfo playerInfo : playerInfoMap) {
                list2.add(playerInfo.getGameProfile().getName());
            }
        }

        this.entries.clear();

        for (String str3 : list) {
            if (!list2.contains(str3)) {
                this.entries.add(str3);
            }
        }

        this.flag = false;
    }
}
