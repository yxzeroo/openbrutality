package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.DisplayHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.lwjgl.input.Mouse;

public class AutoKings extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final BooleanSetting showHUDSetting = new BooleanSetting("Show HUD", this, true);
    private final BooleanSetting showKingLocationSetting = new BooleanSetting("Show King Location", this, true);
    private final BooleanSetting soundOnCompleteSetting = new BooleanSetting("Sound on Complete", this, true);
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 2.0, 0.1);
    private int posX = 10;
    private int posY = 200;
    private int width = 180;
    private int height = 100;
    private boolean flag = false;
    private static final int count = -110;
    private static final int count2 = 79;
    private static final int count3 = -6;
    private boolean flag2 = false;
    private long timestamp = 0L;
    private static final long timestamp2 = 1000L;

    public AutoKings() {
        super("Auto Kings", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.showHUDSetting, this.showKingLocationSetting, this.soundOnCompleteSetting, this.scaleSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                this.process4();
            } else if (event instanceof EventRender2D) {
                if (this.showHUDSetting.isEnabled()) {
                    this.process3();
                }
            } else if (event instanceof ChatEvent) {
                this.onChatEvent((ChatEvent)event);
            }
        }
    }

    private void process5() {
        if (this.soundOnCompleteSetting.isEnabled()) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("note.pling"), 1.5F));
        }

        String str = MessageUtils.computeString("King's Quest ", MessageUtils.text7)
            + MessageUtils.computeString("is ready to complete!", MessageUtils.text11);
        MessageUtils.x(str);
        if (this.showKingLocationSetting.isEnabled()) {
            String str2 = MessageUtils.computeString("The King is at: ", MessageUtils.text15)
                + MessageUtils.computeString("(-110, 79, -6)", MessageUtils.text12)
                + MessageUtils.computeString(" [Port]", MessageUtils.text8);
            MessageUtils.x(str2);
        }
    }

    private int computeInt(DisplayHelper.UnacceptedMode unacceptedMode) {
        switch (unacceptedMode) {
            case UNACCEPTED:
                return 16755200;
            case INCOMPLETE:
                return 16733525;
            case COMPLETABLE:
                return 65280;
            case ACCEPTED:
                return 5635925;
            default:
                return 16777215;
        }
    }

    private void process4() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            FreshHelper.process();
            if (FreshHelper.flag) {
                String str = FreshHelper.computeString(mc.theWorld);
                if ("kings".equals(str)) {
                    if (mc.currentScreen instanceof GuiChest) {
                        long now = System.currentTimeMillis();
                        if (now - this.timestamp > 1000L) {
                            this.timestamp = now;
                            DisplayHelper.process4();
                        }
                    }

                    DisplayHelper.process6();
                    if (DisplayHelper.checkState() && !this.flag2) {
                        this.process5();
                        this.flag2 = true;
                    }

                    if (!DisplayHelper.checkState() && this.flag2) {
                        this.flag2 = false;
                    }
                }
            }
        }
    }

    private void handleInt(int n, int n2, int n3, int n4, int n5, int n6) {
        Gui.drawRect(n, n2, n + n3, n2 + n5, n6);
        Gui.drawRect(n, n2 + n4 - n5, n + n3, n2 + n4, n6);
        Gui.drawRect(n, n2, n + n5, n2 + n4, n6);
        Gui.drawRect(n + n3 - n5, n2, n + n3, n2 + n4, n6);
    }

    private String computeString(DisplayHelper.UnacceptedMode unacceptedMode) {
        switch (unacceptedMode) {
            case UNACCEPTED:
                return EnumChatFormatting.GRAY + "Not Accepted";
            case INCOMPLETE:
                return EnumChatFormatting.YELLOW + "In Progress";
            case COMPLETABLE:
                return EnumChatFormatting.GREEN + "Ready to Complete!";
            case ACCEPTED:
                return EnumChatFormatting.GREEN + "Completed";
            default:
                return EnumChatFormatting.GRAY + "Unknown";
        }
    }

    private void process3() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            String str = FreshHelper.computeString(mc.theWorld);
            if ("kings".equals(str)) {
                ScaledResolution sr = new ScaledResolution(mc);
                this.process();
                float f = (float)this.scaleSetting.computeDouble();
                int n = (int)(this.width * f);
                int n2 = (int)(this.height * f);
                this.posX = Math.max(0, Math.min(this.posX, sr.getScaledWidth() - n));
                this.posY = Math.max(0, Math.min(this.posY, sr.getScaledHeight() - n2));
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)this.posX, (float)this.posY, 0.0F);
                GlStateManager.scale(f, f, 1.0F);
                Gui.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 150).getRGB());
                this.handleInt(0, 0, this.width, this.height, 2, new Color(255, 215, 0, 200).getRGB());
                DisplayHelper.Entry entry = DisplayHelper.getEntry();
                String str2 = "King's Quest";
                int n3 = this.computeInt(entry.unacceptedMode);
                computeFontRenderer(AutoKings.class).drawStringWithShadow(str2, 4.0F, 4.0F, n3);
                String str3 = this.computeString(entry.unacceptedMode);
                computeFontRenderer(AutoKings.class).drawStringWithShadow(str3, 4.0F, 16.0F, 11184810);
                int n4 = 30;
                if (entry.unacceptedMode != DisplayHelper.UnacceptedMode.INCOMPLETE && entry.unacceptedMode != DisplayHelper.UnacceptedMode.COMPLETABLE) {
                    if (entry.unacceptedMode == DisplayHelper.UnacceptedMode.UNACCEPTED && this.showKingLocationSetting.isEnabled()) {
                        String str4 = EnumChatFormatting.YELLOW + "King Location:";
                        computeFontRenderer(AutoKings.class).drawStringWithShadow(str4, 4.0F, n4, 16777215);
                        n4 += 12;
                        String str5 = EnumChatFormatting.AQUA + "(-110, 79, -6)";
                        computeFontRenderer(AutoKings.class).drawStringWithShadow(str5, 4.0F, n4, 16777215);
                        n4 += 12;
                        String str6 = EnumChatFormatting.GRAY + "[Port]";
                        computeFontRenderer(AutoKings.class).drawStringWithShadow(str6, 4.0F, n4, 16777215);
                    }
                } else {
                    String str7 = entry.count >= entry.count2 ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString();
                    int n5 = entry.count2;
                    int count = entry.count;
                    String str8 = "Kills: " + str7 + count + "/" + n5;
                    computeFontRenderer(AutoKings.class).drawStringWithShadow(str8, 4.0F, n4, 16777215);
                    n4 += 12;
                    String str9 = entry.currentRenown >= entry.count3 ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString();
                    int n6 = entry.count3;
                    int currentRenown = entry.currentRenown;
                    String str10 = "Renown: " + str9 + currentRenown + "/" + n6;
                    computeFontRenderer(AutoKings.class).drawStringWithShadow(str10, 4.0F, n4, 16777215);
                    n4 += 12;
                    String str11 = entry.flag ? "✔" : "✖";
                    int n7 = entry.flag ? '\uff00' : 16711680;
                    String str12 = str11 + " " + (entry.text.isEmpty() ? "Mini Cake" : entry.text);
                    computeFontRenderer(AutoKings.class).drawStringWithShadow(str12, 4.0F, n4, n7);
                    n4 += 12;
                }

                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (HudDragHelper.matchesObject(this)) {
            HudDragHelper.handleObject(this);
        }
    }

    private void onChatEvent(ChatEvent chatEvent) {
        String text = chatEvent.message.getUnformattedText();
        DisplayHelper.handleString(text);
        if (text.contains("Grats") || text.contains("completed")) {
            this.flag2 = false;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        DisplayHelper.process();
        this.flag2 = false;
    }

    private void process() {
        if (mc.currentScreen instanceof GuiChat) {
            int[] ints = HudDragHelper.computeIntArray();
            int n = ints[0];
            int n2 = ints[1];
            boolean buttonDown = Mouse.isButtonDown(0);
            float f = (float)this.scaleSetting.computeDouble();
            int n3 = (int)(this.width * f);
            int n4 = (int)(this.height * f);
            if (buttonDown && !this.flag) {
                if (HudDragHelper.matchesInt(n, n2, this.posX, this.posY, n3, n4)) {
                    HudDragHelper.matchesObject(this, n, n2, this.posX, this.posY);
                }
            } else if (!buttonDown && this.flag) {
                HudDragHelper.handleObject(this);
            }

            if (HudDragHelper.matchesObject(this)) {
                int[] ints2 = HudDragHelper.computeIntArray(this);
                if (ints2 != null) {
                    this.posX = ints2[0];
                    this.posY = ints2[1];
                }
            }

            this.flag = buttonDown;
        } else {
            if (HudDragHelper.matchesObject(this)) {
                HudDragHelper.handleObject(this);
            }

            this.flag = false;
        }
    }
}
