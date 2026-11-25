package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import java.util.Arrays;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class MidCounter extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static MidCounter instance;
    private final NumberSetting xPositionSetting = new NumberSetting("X Position", this, 5.0, 0.0, 1920.0, 1.0);
    private final NumberSetting yPositionSetting = new NumberSetting("Y Position", this, 80.0, 0.0, 1080.0, 1.0);
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 3.0, 1.0);
    private final ModeSetting fancySetting = new ModeSetting("Style", this, "Box", Arrays.asList("Box", "Minimal", "Rounded", "Fancy"));
    private final ModeSetting rainbowSetting = new ModeSetting("Color", this, "Dynamic", Arrays.asList("Dynamic", "Static", "Rainbow"));
    private final BooleanSetting backgroundSetting = new BooleanSetting("Background", this, true);
    private final BooleanSetting textShadowSetting = new BooleanSetting("Text Shadow", this, true);
    private final BooleanSetting smoothCountSetting = new BooleanSetting("Smooth Count", this, true);
    private final BooleanSetting pulseEffectSetting = new BooleanSetting("Pulse Effect", this, true);
    private float factor = 0.0F;
    private float factor2 = 1.0F;
    private long timestamp = 0L;
    private int count = 0;
    private float factor3 = 0.0F;
    private static final String text = "PIT";
    private static final int count2 = 250;
    private static final int count3 = new Color(0, 0, 0, 160).getRGB();
    private static final int count4 = new Color(20, 20, 20, 200).getRGB();
    private static final int count5 = new Color(255, 50, 50).getRGB();
    private static final int count6 = new Color(255, 200, 50).getRGB();
    private static final int count7 = new Color(50, 255, 50).getRGB();

    @Generated
    public static MidCounter getInstance() {
        return instance;
    }

    private int computeInt(int n) {
        if (this.rainbowSetting.getText().equals("Rainbow")) {
            return Color.HSBtoRGB(this.factor3 / 360.0F, 0.8F, 0.9F);
        }
        if (this.rainbowSetting.getText().equals("Static")) {
            return Color.WHITE.getRGB();
        }
        if (n > 8) {
            return count5;
        }
        return n > 4 ? count6 : count7;
    }

    public MidCounter() {
        super("MidCounter", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.xPositionSetting,
                this.yPositionSetting,
                this.scaleSetting,
                this.fancySetting,
                this.rainbowSetting,
                this.backgroundSetting,
                this.textShadowSetting,
                this.smoothCountSetting,
                this.pulseEffectSetting
            }
        );
        instance = this;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
    }

    public static int computeInt() {
        return FreshHelper.computeInt();
    }

    private void handleFloat(float f, float f2, float f3, float f4, float f5, int n, boolean flag) {
        float f6 = f;
        float f7 = f2;
        float f8 = f3;
        float f9 = f4;
        if (!flag) {
            f6 = f - f5;
            f7 = f2 - f5;
            f8 = f3 + f5;
            f9 = f4 + f5;
        }

        this.handleFloat(f6, f7, f8, f7 + f5, n);
        this.handleFloat(f6, f9 - f5, f8, f9, n);
        this.handleFloat(f6, f7 + f5, f6 + f5, f9 - f5, n);
        this.handleFloat(f8 - f5, f7 + f5, f8, f9 - f5, n);
    }

    private void handleInt(int count) {
        long now = System.currentTimeMillis();
        if (this.smoothCountSetting.isEnabled()) {
            if (count != this.count) {
                this.count = count;
                this.timestamp = now;
            }

            float f = count;
            float f2 = f - this.factor;
            float f3 = 0.1F;
            this.factor += f2 * f3;
        } else {
            this.factor = count;
        }

        if (this.pulseEffectSetting.isEnabled()) {
            if (count != this.count) {
                this.factor2 = 1.2F;
            }

            if (this.factor2 > 1.0F) {
                this.factor2 = this.factor2 - (this.factor2 - 1.0F) * 0.1F;
                if (this.factor2 < 1.01F) {
                    this.factor2 = 1.0F;
                }
            }
        } else {
            this.factor2 = 1.0F;
        }

        this.factor3 = (this.factor3 + 0.5F) % 360.0F;
    }

    private void handleFloat(float f, float f2, float f3, float f4, int n) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.color(
            ((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F,
            ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F,
            (n & 0xFF) / 255.0F,
            (n >> 24 & 0xFF) / 255.0F
        );
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(f, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f, f2, 0.0).func_181675_d();
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender2D) {
                this.onEventRender2D((EventRender2D)event);
            }
        }
    }

    private void onEventRender2D(EventRender2D event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.thePlayer != null) {
            FontRenderer fontRenderer = computeFontRenderer(MidCounter.class);
            float f = (float)this.scaleSetting.computeDouble();
            float f2 = (float)this.xPositionSetting.computeDouble();
            float f3 = (float)this.yPositionSetting.computeDouble();
            int n = FreshHelper.computeInt();
            this.handleInt(n);
            String str = this.smoothCountSetting.isEnabled() ? String.format("%.1f", this.factor) : String.valueOf(n);
            int textWidth = fontRenderer.getStringWidth("PIT");
            int textWidth2 = fontRenderer.getStringWidth(str);
            int n2 = Math.max(textWidth, textWidth2) + 10;
            byte b = 30;
            if (mc.currentScreen instanceof GuiChat) {
                int[] ints = HudDragHelper.computeIntArray();
                int n3 = ints[0];
                int n4 = ints[1];
                if (Mouse.isButtonDown(0)) {
                    if (!HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(n3, n4, f2, f3, n2, b)) {
                        HudDragHelper.matchesObject(this, n3, n4, (int)f2, (int)f3);
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                if (HudDragHelper.matchesObject(this)) {
                    int[] ints2 = HudDragHelper.computeIntArray(this);
                    if (ints2 != null) {
                        this.xPositionSetting.handleDouble2(ints2[0]);
                        this.yPositionSetting.handleDouble2(ints2[1]);
                        f2 = ints2[0];
                        f3 = ints2[1];
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(f2, f3, 0.0F);
            GlStateManager.scale(f * this.factor2, f * this.factor2, 1.0F);
            int n5 = this.computeInt(n);
            if (this.backgroundSetting.isEnabled()) {
                String text = this.fancySetting.getText();
                switch (text) {
                    case "Box":
                        Gui.drawRect(0, 0, n2, b, count3);
                        this.handleFloat(0.0F, 0.0F, n2, b, 1.0F, count4, true);
                        break;
                    case "Minimal":
                        Gui.drawRect(0, 0, n2, b, new Color(0, 0, 0, 100).getRGB());
                        break;
                    case "Rounded":
                        ResetColorHelper.handleFloat3(0.0F, 0.0F, n2, b, 5.0F, count3);
                        break;
                    case "Fancy":
                        ResetColorHelper.handleFloat2(
                            0.0F, 0.0F, (float)n2, (float)b, new Color(20, 20, 20, 200).getRGB(), new Color(10, 10, 10, 200).getRGB()
                        );
                        this.handleFloat(0.0F, 0.0F, n2, b, 1.0F, n5 & 1358954495, true);
                }
            }

            fontRenderer.drawString("PIT", n2 / 2 - textWidth / 2, 3.0F, Color.WHITE.getRGB(), this.textShadowSetting.isEnabled());
            fontRenderer.drawString(str, n2 / 2 - textWidth2 / 2, 15.0F, n5, this.textShadowSetting.isEnabled());
            GlStateManager.popMatrix();
        }
    }
}
