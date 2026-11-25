package org.brutality.module.impl.visual;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.event.impl.render.SetShaderTypeEvent.BlurMode;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.WHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.input.Mouse;

public class Session extends Module {
    private int count = 0;
    private int count2 = 0;
    private int count3 = 0;
    private long startTime = 0L;
    public ModeSetting oldSetting = new ModeSetting("Mode", this, "Brutality", Arrays.asList("Brutality", "Exhibition", "Old"));
    public BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 0.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final NumberSetting colorSpeedSetting = new NumberSetting("Color Speed", this, 5.0, 1.0, 20.0, 1.0);
    private final NumberSetting opacitySetting = new NumberSetting("Opacity", this, 120.0, 50.0, 255.0, 1.0);
    private final NumberSetting positionXSetting = new NumberSetting("Position X", this, 20.0, 0.0, 1920.0, 1.0);
    private final NumberSetting positionYSetting = new NumberSetting("Position Y", this, 20.0, 0.0, 1080.0, 1.0);
    private FontRenderer fontRenderer;
    private FontRenderer fontRenderer2;
    private BrutalityHelper brutalityHelper;
    private boolean flag = false;
    private static final String[] stringArray = new String[]{
        "SERVER FOUND!",
        "1st Killer - ",
        "1st Place - ",
        "Winner: ",
        " - Damage Dealt - ",
        "1st - ",
        "Winning Team - ",
        "Winners: ",
        "Winner: ",
        "Winning Team: ",
        " win the game!",
        "Top Seeker: ",
        "1st Place: ",
        "Last team standing!",
        "Winner #1 (",
        "Top Survivors",
        "Winners - "
    };
    private static final String[] stringArray2 = new String[]{"by ", "para ", "fue destrozado a manos de "};

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.checkState44()) {
                if (event instanceof GetPacketEvent getPacketEvent) {
                    if (getPacketEvent.getPacket() instanceof S02PacketChat) {
                        S02PacketChat packet = getPacketEvent.getPacket();
                        String text = packet.getChatComponent().getUnformattedText();
                        String str = StringUtils.stripControlCodes(text);
                        this.handleString(str);
                        this.handleString2(str);
                    }
                } else if (event instanceof SetShaderTypeEvent setShaderTypeEvent && this.oldSetting.getText().equals("Brutality") && setShaderTypeEvent.getShaderType() == BlurMode.GLOW) {
                    this.process3();
                }
            }
        }
    }

    private void process4() {
        this.brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.modeSetting);
    }

    private Color computeColor2() {
        return new Color(255, 120, 120);
    }

    private String computeString() {
        if (this.startTime == 0L) {
            return "00:00:00";
        }
        long now = System.currentTimeMillis();
        long l = now - this.startTime;
        long l2 = l / 1000L % 60L;
        long l3 = l / 60000L % 60L;
        long l4 = l / 3600000L % 24L;
        return String.format("%02d:%02d:%02d", l4, l3, l2);
    }

    private void handleMinecraft(Minecraft mc, ScaledResolution sr) {
        String str = this.computeString();
        String[] parts = new String[]{"Session: " + str, "Games: " + this.count, "Kills: " + this.count2, "KOS: " + this.count3};
        byte b = 5;
        byte b2 = 5;

        for (int i = 0; i < parts.length; i++) {
            computeFontRenderer(Session.class).drawStringWithShadow(parts[i], b, b2 + i * 12, Color.WHITE.getRGB());
        }
    }

    @Override
    public boolean onBlurMode(BlurMode blurMode) {
        return this.oldSetting.matchesString2("Brutality") && blurMode == BlurMode.GLOW;
    }

    @Override
    public void onRender() {
        if (this.checkState44()) {
            Minecraft mc = Minecraft.getMinecraft();
            this.process();
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
            if (this.oldSetting.getText().equals("Exhibition")) {
                this.handleMinecraft2(mc, sr);
            } else if (this.oldSetting.getText().equals("Brutality")) {
                this.handleMinecraft3(mc, sr);
            } else if (this.oldSetting.getText().equals("Old")) {
                this.handleMinecraft(mc, sr);
            }
        }
    }

    public Session() {
        super("Session", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.oldSetting,
                this.debugSetting,
                this.modeSetting,
                this.customColorSetting,
                this.colorSpeedSetting,
                this.opacitySetting,
                this.positionXSetting,
                this.positionYSetting
            }
        );
        this.process4();
    }

    private Color computeColor() {
        return new Color(120, 255, 120);
    }

    private Color computeColor3() {
        this.process4();
        if (ModuleSpecificHelper.matchesModeSetting(this.modeSetting) && this.brutalityHelper != null) {
            double d = this.colorSpeedSetting.computeDouble() / 5.0;
            long l = (long)(System.currentTimeMillis() * d / 10.0);
            return GetColorHelper.computeColor(15, (int)(l % 360L), this.brutalityHelper.getColor(), this.brutalityHelper.getColor3(), false);
        }
        return this.customColorSetting.computeColor();
    }

    @Override
    public void onEnable() {
        if (this.startTime == 0L) {
            this.startTime = System.currentTimeMillis();
        }

        super.onEnable();
    }

    private void handleMinecraft2(Minecraft mc, ScaledResolution sr) {
        String str = new SimpleDateFormat("HH:mm:ss").format(new Date());
        int textWidth = computeFontRenderer(Session.class).getStringWidth(str);
        computeFontRenderer(Session.class).drawStringWithShadow(str, (sr.getScaledWidth() - textWidth) / 2, 5.0F, Color.WHITE.getRGB());
        String str2 = "Kills: " + this.count2;
        computeFontRenderer(Session.class)
            .drawStringWithShadow(str2, (sr.getScaledWidth() - computeFontRenderer(Session.class).getStringWidth(str2)) / 2, 17.0F, Color.WHITE.getRGB());
        String str3 = "KOS Kills: " + this.count3;
        computeFontRenderer(Session.class)
            .drawStringWithShadow(
                str3, (sr.getScaledWidth() - computeFontRenderer(Session.class).getStringWidth(str3)) / 2, 29.0F, new Color(255, 120, 120).getRGB()
            );
    }

    private void handleMinecraft3(Minecraft mc, ScaledResolution sr) {
        short s = 200;
        byte b = 110;
        int n = (int)Math.max(0.0, Math.min(this.positionXSetting.computeDouble(), (double)(sr.getScaledWidth() - s)));
        int n2 = (int)Math.max(0.0, Math.min(this.positionYSetting.computeDouble(), (double)(sr.getScaledHeight() - b)));
        int n3 = 6;
        int[] ints = HudDragHelper.computeIntArray();
        int n4 = ints[0];
        int n5 = ints[1];
        if (HudDragHelper.matchesInt(n4, n5, n, n2, s, b)
            && Mouse.isButtonDown(0)
            && !this.flag
            && HudDragHelper.matchesObject(this, n4, n5, n, n2)) {
            this.flag = true;
        }

        if (this.flag) {
            if (Mouse.isButtonDown(0)) {
                int[] ints2 = HudDragHelper.computeIntArray(this);
                if (ints2 != null) {
                    this.positionXSetting.handleDouble2(ints2[0]);
                    this.positionYSetting.handleDouble2(ints2[1]);
                    n = ints2[0];
                    n2 = ints2[1];
                }
            } else {
                HudDragHelper.handleObject(this);
                this.flag = false;
            }
        }

        Color color = new Color(40, 10, 10, (int)this.opacitySetting.computeDouble());
        Color color2 = new Color(100, 20, 20, (int)this.opacitySetting.computeDouble());
        OutlineThicknessHelper.handleFloat(n, n2, s, b, n3, color, color2);
        Color color3 = new Color(200, 50, 50, 200);
        OutlineThicknessHelper.handleFloat(n, n2, s, b, n3, 2.0F, new Color(0, 0, 0, 0), color3);
        byte b2 = 20;
        int n6 = n2 + 10;
        int n7 = n + 10;

        try {
            EntityPlayerSP player = mc.thePlayer;
            ResourceLocation locationSkin = player.getLocationSkin();
            mc.getTextureManager().bindTexture(locationSkin);
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawScaledCustomSizeModalRect(n7, n6, 8.0F, 8.0F, 8, 8, b2, b2, 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect(n7, n6, 40.0F, 8.0F, 8, 8, b2, b2, 64.0F, 64.0F);
            GlStateManager.disableBlend();
        } catch (Exception e) {
            ResetColorHelper.drawRect(n7, n6, n7 + b2, n6 + b2, Color.GRAY.getRGB());
        }

        if (this.fontRenderer2 != null) {
            this.fontRenderer2.drawString(mc.thePlayer.getName(), n7 + b2 + 8, n6 + 6, new Color(255, 200, 200).getRGB());
        }

        int n8 = n2 + 40;
        byte b3 = 15;
        if (this.fontRenderer != null) {
            String str = this.computeString();
            this.fontRenderer.drawString("Session Time:", n + 10, n8, new Color(200, 150, 150).getRGB());
            this.fontRenderer.drawString(str, n + s - 10 - this.fontRenderer.getStringWidth(str), n8, new Color(255, 200, 200).getRGB());
            n8 += b3;
            this.fontRenderer.drawString("Kills:", n + 10, n8, new Color(200, 150, 150).getRGB());
            this.fontRenderer
                .drawString(
                    String.valueOf(this.count2),
                    n + s - 10 - this.fontRenderer.getStringWidth(String.valueOf(this.count2)),
                    n8,
                    new Color(150, 255, 150).getRGB()
                );
            n8 += b3;
            this.fontRenderer.drawString("KOS Kills:", n + 10, n8, new Color(200, 150, 150).getRGB());
            this.fontRenderer
                .drawString(
                    String.valueOf(this.count3),
                    n + s - 10 - this.fontRenderer.getStringWidth(String.valueOf(this.count3)),
                    n8,
                    new Color(255, 150, 150).getRGB()
                );
            n8 += b3;
            this.fontRenderer.drawString("Games Played:", n + 10, n8, new Color(200, 150, 150).getRGB());
            this.fontRenderer
                .drawString(
                    String.valueOf(this.count),
                    n + s - 10 - this.fontRenderer.getStringWidth(String.valueOf(this.count)),
                    n8,
                    new Color(200, 200, 255).getRGB()
                );
        }
    }

    private void handleString2(String str) {
        for (String str2 : stringArray) {
            if (str.contains(str2)) {
                this.count++;
                if (this.debugSetting.isEnabled()) {
                    Minecraft.getMinecraft()
                        .thePlayer
                        .addChatMessage(new ChatComponentText("§7[Session Debug] §aGame completion detected: " + str2 + " | Games played: " + this.count));
                }
                break;
            }
        }
    }

    @Override
    public void onDisable() {
        this.startTime = 0L;
        this.count2 = 0;
        this.count3 = 0;
        this.count = 0;
        if (this.flag) {
            HudDragHelper.handleObject(this);
            this.flag = false;
        }

        super.onDisable();
    }

    private void handleString(String str) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!str.contains(":")) {
            for (String str2 : stringArray2) {
                String str3 = str2 + mc.thePlayer.getName();
                if (str.contains(str3)) {
                    String str4 = str.trim().split(" ")[0];
                    this.count2++;
                    if (this.debugSetting.isEnabled()) {
                        mc.thePlayer.addChatMessage(new ChatComponentText("§7[Session Debug] §eKill detected: " + str4 + " | Total kills: " + this.count2));
                    }

                    if (WHelper.matchesString(str4)) {
                        this.count3++;
                        if (this.debugSetting.isEnabled()) {
                            mc.thePlayer.addChatMessage(new ChatComponentText("§7[Session Debug] §cKOS Kill: " + str4 + " | KOS kills: " + this.count3));
                        }
                    }
                    break;
                }
            }
        }
    }

    private void process3() {
        int n = (int)this.positionXSetting.computeDouble();
        int n2 = (int)this.positionYSetting.computeDouble();
        short s = 200;
        byte b = 110;
        byte b2 = 6;
        Color color = new Color(200, 50, 50, 255);
        OutlineThicknessHelper.handleFloat(n, n2, s, b, b2, 2.0F, new Color(0, 0, 0, 0), color);
    }

    private void process() {
        this.fontRenderer2 = this.computeFontRenderer2(16.0F);
        this.fontRenderer = this.computeFontRenderer2(14.0F);
    }
}
