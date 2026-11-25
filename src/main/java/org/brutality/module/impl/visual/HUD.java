package org.brutality.module.impl.visual;

import java.awt.Color;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.UtilsHelper;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.lwjgl.input.Mouse;

public class HUD extends Module {
    private final ModeSetting hudSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final BooleanSetting customFontSetting;
    private final ModeSetting fontSetting;
    private final NumberSetting fontSizeSetting;
    private CustomFontRenderer drawStringWithShadowComponent;
    private String text = "";
    private final BooleanSetting cpsEnabledSetting;
    private BooleanSetting showLeftCPSSetting;
    private BooleanSetting showRightCPSSetting;
    private final NumberSetting cpsxSetting;
    private final NumberSetting cpsySetting;
    private final BooleanSetting fpsEnabledSetting;
    private final NumberSetting fpsxSetting;
    private final NumberSetting fpsySetting;
    private final BooleanSetting ipEnabledSetting;
    private final NumberSetting ipxSetting;
    private final NumberSetting ipySetting;
    private final BooleanSetting bpsEnabledSetting;
    private final NumberSetting bpsxSetting;
    private final NumberSetting bpsySetting;
    private final BooleanSetting pingEnabledSetting;
    private final NumberSetting pingXSetting;
    private final NumberSetting pingYSetting;
    private double amount;
    private double amount2;
    private double amount3;
    private long timestamp;
    private double amount4;
    private float factor = 0.0F;
    private static final Color color = new Color(5636095);
    private static final Color color2 = new Color(5635925);
    private static final Color color3 = Color.getHSBColor(0.58F, 1.0F, 1.0F);
    private static final Color color4 = new Color(5636095);
    private static final Color color5 = Color.getHSBColor(0.16F, 1.0F, 1.0F);
    private String text2 = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private static HUD instance;

    private Color computeColor(Color color) {
        BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.hudSetting);
        return brutalityHelper == null ? color : brutalityHelper.getColor();
    }

    public boolean checkState() {
        return this.customFontSetting.isEnabled();
    }

    private void handleDouble(double d, double d2) {
        String str = "BPS: ";
        String str2 = String.format("%.2f", this.amount4);
        Color color = this.computeColor(this.factor);
        CustomFontRenderer drawStringWithShadowComponent2 = this.getDrawStringWithShadowComponent2();
        if (drawStringWithShadowComponent2 != null) {
            float f = (float)d;
            drawStringWithShadowComponent2.computeInt3(str, f, (float)d2, color.getRGB());
            f += drawStringWithShadowComponent2.getStringWidth(str);
            drawStringWithShadowComponent2.computeInt3(str2, f, (float)d2, color.getRGB());
        } else {
            int n = (int)d;
            mc.fontRendererObj.drawString(str, n, (int)d2, color.getRGB());
            n += mc.fontRendererObj.getStringWidth(str);
            mc.fontRendererObj.drawString(str2, n, (int)d2, color.getRGB());
        }
    }

    private void handleDouble(double d, double d2, String str, Color color, boolean flag) {
        String[] parts = str.split(": ");
        if (parts.length == 2) {
            String str2 = parts[0] + ": ";
            String str3 = parts[1];
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.hudSetting);
            Color color2;
            Color color3;
            if (brutalityHelper != null) {
                float f = (this.factor + (float)d2 / 300.0F) % 1.0F;
                Color color4 = this.computeColor(f);
                color2 = color4;
                color3 = color4;
            } else {
                color2 = Color.WHITE;
                color3 = color;
            }

            if (this.customFontSetting.isEnabled() && this.drawStringWithShadowComponent != null) {
                float f2 = (float)d;
                this.drawStringWithShadowComponent.computeInt3(str2, f2, (float)d2, color2.getRGB());
                f2 += this.drawStringWithShadowComponent.getStringWidth(str2);
                this.drawStringWithShadowComponent.computeInt3(str3, f2, (float)d2, color3.getRGB());
            } else {
                int n = (int)d;
                if (flag) {
                    mc.fontRendererObj.drawStringWithShadow(str2, n, (int)d2, color2.getRGB());
                    n += mc.fontRendererObj.getStringWidth(str2);
                    mc.fontRendererObj.drawStringWithShadow(str3, n, (int)d2, color3.getRGB());
                } else {
                    mc.fontRendererObj.drawString(str2, n, (int)d2, color2.getRGB());
                    n += mc.fontRendererObj.getStringWidth(str2);
                    mc.fontRendererObj.drawString(str3, n, (int)d2, color3.getRGB());
                }
            }
        }
    }

    private String computeString() {
        String str = "Singleplayer";

        try {
            if (mc.getCurrentServerData() != null) {
                str = mc.getCurrentServerData().serverIP;
                if (str.length() > 25) {
                    str = str.substring(0, 22) + "...";
                }
            }
        } catch (Exception ignored) {
        }

        return "IP: " + str;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.thePlayer != null) {
            this.amount = mc.thePlayer.posX;
            this.amount2 = mc.thePlayer.posY;
            this.amount3 = mc.thePlayer.posZ;
            this.timestamp = System.currentTimeMillis();
        }
    }

    @Generated
    public static HUD getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.text2 = null;
    }

    public double computeDouble() {
        return this.fontSizeSetting.computeDouble();
    }

    private Color computeColor(float f) {
        BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.hudSetting);
        Color color = brutalityHelper != null ? brutalityHelper.getColor() : new Color(5636095);
        Color color2 = brutalityHelper != null ? brutalityHelper.getColor2() : new Color(43775);
        float f2 = (float)(Math.sin(f * 2.0F * Math.PI) * 0.5 + 0.5);
        int n = (int)(color.getRed() + (color2.getRed() - color.getRed()) * f2);
        int n2 = (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f2);
        int n3 = (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f2);
        return new Color(Math.max(0, Math.min(255, n)), Math.max(0, Math.min(255, n2)), Math.max(0, Math.min(255, n3)));
    }

    private void process5() {
        if (mc.thePlayer == null) {
            this.amount4 = 0.0;
        } else {
            this.amount4 = Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed * 20.0;
            this.amount4 = Math.round(this.amount4 * 100.0) / 100.0;
        }
    }

    private String computeString5() {
        return "BPS: " + String.format("%.2f", this.amount4);
    }

    public CustomFontRenderer getDrawStringWithShadowComponent() {
        return this.drawStringWithShadowComponent;
    }

    public String computeString3() {
        return this.fontSetting.getText();
    }

    public HUD() {
        super("HUD", 0, Category.VISUAL);
        instance = this;
        this.customFontSetting = new BooleanSetting("Custom Font", this, true);
        this.fontSetting = IsChoiceHelper.computeModeSetting("Font", this)
            .computeModeSetting(() -> this.customFontSetting.isEnabled() && !ModuleSpecificHelper.matchesModeSetting(this.hudSetting));
        this.fontSizeSetting = new NumberSetting("Font Size", this, 20.0, 10.0, 30.0, 1.0).computeNumberSetting(() -> this.customFontSetting.isEnabled());
        this.cpsEnabledSetting = new BooleanSetting("CPS Enabled", this, true);
        this.showLeftCPSSetting = new BooleanSetting("Show Left CPS", this, true);
        this.showRightCPSSetting = new BooleanSetting("Show Right CPS", this, true);
        this.cpsxSetting = new NumberSetting("CPS X", this, 5.0, 0.0, 1920.0, 1.0);
        this.cpsySetting = new NumberSetting("CPS Y", this, 5.0, 0.0, 1080.0, 1.0);
        this.fpsEnabledSetting = new BooleanSetting("FPS Enabled", this, true);
        this.fpsxSetting = new NumberSetting("FPS X", this, 5.0, 0.0, 1920.0, 1.0);
        this.fpsySetting = new NumberSetting("FPS Y", this, 20.0, 0.0, 1080.0, 1.0);
        this.ipEnabledSetting = new BooleanSetting("IP Enabled", this, true);
        this.ipxSetting = new NumberSetting("IP X", this, 5.0, 0.0, 1920.0, 1.0);
        this.ipySetting = new NumberSetting("IP Y", this, 35.0, 0.0, 1080.0, 1.0);
        this.bpsEnabledSetting = new BooleanSetting("BPS Enabled", this, true);
        this.bpsxSetting = new NumberSetting("BPS X", this, 5.0, 0.0, 1920.0, 1.0);
        this.bpsySetting = new NumberSetting("BPS Y", this, 50.0, 0.0, 1080.0, 1.0);
        this.pingEnabledSetting = new BooleanSetting("Ping Enabled", this, true);
        this.pingXSetting = new NumberSetting("Ping X", this, 5.0, 0.0, 1920.0, 1.0);
        this.pingYSetting = new NumberSetting("Ping Y", this, 65.0, 0.0, 1080.0, 1.0);
        this.handleSettingArray(
            new Setting[]{
                this.hudSetting,
                this.customFontSetting,
                this.fontSetting,
                this.fontSizeSetting,
                this.cpsEnabledSetting,
                this.showLeftCPSSetting,
                this.showRightCPSSetting,
                this.cpsxSetting,
                this.cpsySetting,
                this.fpsEnabledSetting,
                this.fpsxSetting,
                this.fpsySetting,
                this.ipEnabledSetting,
                this.ipxSetting,
                this.ipySetting,
                this.bpsEnabledSetting,
                this.bpsxSetting,
                this.bpsySetting,
                this.pingEnabledSetting,
                this.pingXSetting,
                this.pingYSetting
            }
        );
        this.timestamp = System.currentTimeMillis();
        this.amount4 = 0.0;
        this.process4();
    }

    private CustomFontRenderer getDrawStringWithShadowComponent2() {
        return this.drawStringWithShadowComponent;
    }

    private String computeString6() {
        return "FPS: " + Minecraft.getDebugFPS();
    }

    private String computeString2() {
        StringBuilder sb = new StringBuilder("CPS: ");
        boolean enabled = this.showLeftCPSSetting.isEnabled();
        boolean enabled2 = this.showRightCPSSetting.isEnabled();
        if (enabled && enabled2) {
            sb.append(UtilsHelper.computeInt());
            sb.append(" | ");
            sb.append(UtilsHelper.computeInt2());
        } else if (enabled) {
            sb.append(UtilsHelper.computeInt());
        } else if (enabled2) {
            sb.append(UtilsHelper.computeInt2());
        } else {
            sb.append("0");
        }

        return sb.toString();
    }

    private void process4() {
        if (!this.customFontSetting.isEnabled()) {
            this.drawStringWithShadowComponent = null;
            this.text = "";
        } else {
            try {
                String str;
                if (ModuleSpecificHelper.matchesModeSetting(this.hudSetting)) {
                    BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
                    str = brutalityHelper != null ? brutalityHelper.getText() : "Brutality";
                } else {
                    str = this.fontSetting.getText();
                }

                float f = (float)this.fontSizeSetting.computeDouble();
                String str2 = str + "@" + f;
                if (this.drawStringWithShadowComponent != null && str2.equals(this.text)) {
                    return;
                }

                this.text = str2;
                this.drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(str, f);
            } catch (Exception e) {
                e.printStackTrace();
                this.drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(
                    "Brutality", (float)this.fontSizeSetting.computeDouble()
                );
            }
        }
    }

    private void process() {
        this.factor = (this.factor + 0.008F) % 1.0F;
        if (this.cpsEnabledSetting.isEnabled()) {
            this.handleDouble(this.cpsxSetting.computeDouble(), this.cpsySetting.computeDouble(), this.computeString2(), this.computeColor(color), true);
        }

        if (this.fpsEnabledSetting.isEnabled()) {
            this.handleDouble(this.fpsxSetting.computeDouble(), this.fpsySetting.computeDouble(), this.computeString6(), this.computeColor(color2), true);
        }

        if (this.ipEnabledSetting.isEnabled()) {
            this.handleDouble(this.ipxSetting.computeDouble(), this.ipySetting.computeDouble(), this.computeString(), this.computeColor(color3), false);
        }

        if (this.bpsEnabledSetting.isEnabled()) {
            this.handleDouble(this.bpsxSetting.computeDouble(), this.bpsySetting.computeDouble());
        }

        if (this.pingEnabledSetting.isEnabled()) {
            this.handleDouble(this.pingXSetting.computeDouble(), this.pingYSetting.computeDouble(), this.computeString4(), this.computeColor(color5), false);
        }
    }

    private boolean matchesInt(int n, int n2, double d, double d2, String str) {
        int n3;
        int n4;
        if (this.customFontSetting.isEnabled() && this.drawStringWithShadowComponent != null) {
            n3 = this.drawStringWithShadowComponent.getStringWidth(str) + 4;
            n4 = this.drawStringWithShadowComponent.getHeight() + 4;
        } else {
            n3 = mc.fontRendererObj.getStringWidth(str) + 4;
            n4 = mc.fontRendererObj.FONT_HEIGHT + 4;
        }

        return n >= d && n <= d + n3 && n2 >= d2 && n2 <= d2 + n4;
    }

    private String computeString4() {
        int n = 0;

        try {
            if (mc.getCurrentServerData() != null && mc.thePlayer != null && mc.getNetHandler() != null) {
                n = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime();
            }
        } catch (Exception ignored) {
        }

        return "Ping: " + n + "ms";
    }

    private void process3() {
        if (!(mc.currentScreen instanceof GuiChat)) {
            this.text2 = null;
        } else {
            int[] ints = HudDragHelper.computeIntArray();
            int n = ints[0];
            int n2 = ints[1];
            boolean buttonDown = Mouse.isButtonDown(0);
            if (buttonDown) {
                if (this.text2 == null) {
                    if (this.matchesInt(n, n2, this.cpsxSetting.computeDouble(), this.cpsySetting.computeDouble(), this.computeString2())) {
                        this.text2 = "CPS";
                        this.dragOffsetX = n - (int)this.cpsxSetting.computeDouble();
                        this.dragOffsetY = n2 - (int)this.cpsySetting.computeDouble();
                    } else if (this.matchesInt(n, n2, this.fpsxSetting.computeDouble(), this.fpsySetting.computeDouble(), this.computeString6())) {
                        this.text2 = "FPS";
                        this.dragOffsetX = n - (int)this.fpsxSetting.computeDouble();
                        this.dragOffsetY = n2 - (int)this.fpsySetting.computeDouble();
                    } else if (this.matchesInt(n, n2, this.ipxSetting.computeDouble(), this.ipySetting.computeDouble(), this.computeString())) {
                        this.text2 = "IP";
                        this.dragOffsetX = n - (int)this.ipxSetting.computeDouble();
                        this.dragOffsetY = n2 - (int)this.ipySetting.computeDouble();
                    } else if (this.matchesInt(n, n2, this.bpsxSetting.computeDouble(), this.bpsySetting.computeDouble(), this.computeString5())) {
                        this.text2 = "BPS";
                        this.dragOffsetX = n - (int)this.bpsxSetting.computeDouble();
                        this.dragOffsetY = n2 - (int)this.bpsySetting.computeDouble();
                    } else if (this.matchesInt(n, n2, this.pingXSetting.computeDouble(), this.pingYSetting.computeDouble(), this.computeString4())) {
                        this.text2 = "PING";
                        this.dragOffsetX = n - (int)this.pingXSetting.computeDouble();
                        this.dragOffsetY = n2 - (int)this.pingYSetting.computeDouble();
                    }
                } else {
                    int n3 = n - this.dragOffsetX;
                    int n4 = n2 - this.dragOffsetY;
                    String str = this.text2;
                    switch (str) {
                        case "CPS":
                            this.cpsxSetting.handleDouble2(n3);
                            this.cpsySetting.handleDouble2(n4);
                            break;
                        case "FPS":
                            this.fpsxSetting.handleDouble2(n3);
                            this.fpsySetting.handleDouble2(n4);
                            break;
                        case "IP":
                            this.ipxSetting.handleDouble2(n3);
                            this.ipySetting.handleDouble2(n4);
                            break;
                        case "BPS":
                            this.bpsxSetting.handleDouble2(n3);
                            this.bpsySetting.handleDouble2(n4);
                            break;
                        case "PING":
                            this.pingXSetting.handleDouble2(n3);
                            this.pingYSetting.handleDouble2(n4);
                    }
                }
            } else {
                this.text2 = null;
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender2D) {
                if (this.customFontSetting.isEnabled()) {
                    this.process4();
                }

                this.process5();
                this.process3();
                this.process();
            }
        }
    }
}
