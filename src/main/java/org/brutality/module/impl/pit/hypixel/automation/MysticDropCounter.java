package org.brutality.module.impl.pit.hypixel.automation;

import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.lwjgl.input.Mouse;

public class MysticDropCounter extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xSetting = new NumberSetting("X", this, 2.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 2.0, 0.0, 1080.0, 1.0);
    private final NumberSetting colorRGBSetting = new NumberSetting("Color (RGB)", this, 65535.0, 0.0, 1.6777215E7, 1.0);
    private final StringSetting leftSetting = new StringSetting("Align", "left");
    private final BooleanSetting toggledSetting = new BooleanSetting("Toggled", this, true);
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private int count = 0;
    private int count2 = 0;
    private int count3 = 0;
    private static final long timestamp = 1513838425529122815L;

    @Override
    public void onRender() {
        if (this.mc.thePlayer != null && this.toggledSetting.isEnabled()) {
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
            FontRenderer fontRenderer = computeFontRenderer(MysticDropCounter.class);
            float f = (float)this.xSetting.computeDouble();
            float f2 = (float)this.ySetting.computeDouble();
            if (this.mc.currentScreen instanceof GuiChat) {
                int[] ints = HudDragHelper.computeIntArray();
                int n = ints[0];
                int n2 = ints[1];
                String str = "Mystic Drop Counter";
                String str2 = "Kills/Mystic: " + (this.count2 == 0 ? "0" : new DecimalFormat("#.##").format(this.count / (this.count2 * 1.0)));
                String str3 = "Kills: " + this.count;
                String str4 = "Mystic Drops: " + this.count2;
                String str5 = "Kills since last Mystic Drop: " + this.count3;
                int n3 = Math.max(
                        fontRenderer.getStringWidth(str),
                        Math.max(
                            fontRenderer.getStringWidth(str4), Math.max(fontRenderer.getStringWidth(str3), Math.max(fontRenderer.getStringWidth(str2), fontRenderer.getStringWidth(str5)))
                        )
                    )
                    + 8;
                int n4 = 5 * fontRenderer.FONT_HEIGHT + 7;
                if (Mouse.isButtonDown(0)) {
                    if (!HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(n, n2, f, f2, n3, n4)) {
                        HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                if (HudDragHelper.matchesObject(this)) {
                    int[] ints2 = HudDragHelper.computeIntArray(this);
                    if (ints2 != null) {
                        this.xSetting.handleDouble2(ints2[0]);
                        this.ySetting.handleDouble2(ints2[1]);
                        f = ints2[0];
                        f2 = ints2[1];
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            int n5 = (int)this.colorRGBSetting.computeDouble();
            String str6 = "Kills/Mystic: " + (this.count2 == 0 ? "0" : new DecimalFormat("#.##").format(this.count / (this.count2 * 1.0)));
            String str7 = "Kills: " + this.count;
            String str8 = "Mystic Drops: " + this.count2;
            String str9 = "Kills since last Mystic Drop: " + this.count3;
            String str10 = "Mystic Drop Counter";
            int textWidth = fontRenderer.getStringWidth(str9);
            String str11 = this.leftSetting.getValue().equalsIgnoreCase("right") ? "right" : "left";
            fontRenderer.drawStringWithShadow(str10, f, f2, (int)timestamp);
            float f3 = f2 + 12.0F;
            if (str11.equals("right")) {
                fontRenderer.drawStringWithShadow(str8, f + textWidth - fontRenderer.getStringWidth(str8), f3, n5);
                fontRenderer.drawStringWithShadow(str7, f + textWidth - fontRenderer.getStringWidth(str7), f3 + fontRenderer.FONT_HEIGHT, n5);
                fontRenderer.drawStringWithShadow(str6, f + textWidth - fontRenderer.getStringWidth(str6), f3 + fontRenderer.FONT_HEIGHT * 2, n5);
                fontRenderer.drawStringWithShadow(str9, f + textWidth - fontRenderer.getStringWidth(str9), f3 + fontRenderer.FONT_HEIGHT * 3, n5);
            } else {
                fontRenderer.drawStringWithShadow(str8, f, f3, n5);
                fontRenderer.drawStringWithShadow(str7, f, f3 + fontRenderer.FONT_HEIGHT, n5);
                fontRenderer.drawStringWithShadow(str6, f, f3 + fontRenderer.FONT_HEIGHT * 2, n5);
                fontRenderer.drawStringWithShadow(str9, f, f3 + fontRenderer.FONT_HEIGHT * 3, n5);
            }
        }
    }

    public MysticDropCounter() {
        super("Mystic Drop Counter", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xSetting, this.ySetting, this.colorRGBSetting, this.leftSetting, this.toggledSetting, this.debugSetting});
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.count = 0;
        this.count2 = 0;
        this.count3 = 0;
        this.sendMessage("Mystic Drop Counter enabled. Debug: " + this.debugSetting.isEnabled());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null && this.mc.theWorld != null && this.toggledSetting.isEnabled()) {
                if (event instanceof GetPacketEvent getPacketEvent) {
                    Packet packet = getPacketEvent.getPacket();
                    if (packet instanceof S02PacketChat) {
                        S02PacketChat packet2 = getPacketEvent.getPacket();
                        String text = packet2.getChatComponent().getFormattedText();
                        if (this.debugSetting.isEnabled()) {
                            this.sendMessage(
                                "Debug: S02PacketChat received. Debug enabled: " + this.debugSetting.isEnabled() + ", Raw message: \"" + text + "\""
                            );
                        }

                        if (text.contains("MYSTIC ITEM!") && !text.contains(":") && text.contains("dropped")) {
                            this.count2++;
                            this.count3 = 0;
                            if (this.debugSetting.isEnabled()) {
                                this.sendMessage("Debug: Mystic item detected! Drops: " + this.count2);
                            }
                        } else if (text.contains("KILL!") && !text.contains(":") && text.contains("[") && text.contains("]")) {
                            this.count++;
                            this.count3++;
                            if (this.debugSetting.isEnabled()) {
                                this.sendMessage("Debug: Kill detected! Kills: " + this.count + ", Since last mystic: " + this.count3);
                            }
                        }
                    }
                }
            }
        }
    }

    private void sendMessage(String str) {
        try {
            ModuleManager.handleString(MessageUtils.computeString(str, MessageUtils.text8));
        } catch (Exception e) {
            System.out.println("[MysticsCounterHUD] Failed to send chat message: " + str + " | Error: " + e.getMessage());
        }

        System.out.println("[MysticsCounterHUD] " + str);
    }
}
