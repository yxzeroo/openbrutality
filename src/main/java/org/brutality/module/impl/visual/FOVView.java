package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.lwjgl.opengl.GL11;

public class FOVView extends Module {
    private final NumberSetting fovRangeSetting = new NumberSetting("FOV Range", this, 90.0, 1.0, 360.0, 1.0);
    private final NumberSetting viewDistanceSetting = new NumberSetting("View Distance", this, 5.0, 1.0, 20.0, 1.0);
    private final NumberSetting opacitySetting = new NumberSetting("Opacity", this, 80.0, 1.0, 100.0, 1.0);
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final ModeSetting gradientSetting = new ModeSetting("Color Mode", this, "Custom", Arrays.asList("Custom", "Rainbow", "Gradient"))
        .computeModeSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final ColorSetting colorSetting = new ColorSetting("Color", this, 0.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final ColorSetting gradientColorSetting = new ColorSetting("Gradient Color", this, 0.33F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final NumberSetting colorSpeedSetting = new NumberSetting("Color Speed", this, 5.0, 1.0, 20.0, 1.0);
    private final ModeSetting bothSetting = new ModeSetting("Render Mode", this, "Filled", Arrays.asList("Filled", "Outlined", "Both"));
    private final BooleanSetting followYawSetting = new BooleanSetting("Follow Yaw", this, true);
    private final NumberSetting lineWidthSetting = new NumberSetting("Line Width", this, 2.0, 1.0, 5.0, 1.0);
    private BrutalityHelper brutalityHelper;
    private static final long timestamp = 1277172975745892351L;
    private static final long timestamp2 = 360L;

    private Color computeColor() {
        this.process();
        if (this.gradientSetting.matchesString2("Custom")) {
            return this.colorSetting.computeColor();
        }
        if (this.gradientSetting.matchesString2("Rainbow")) {
            double d = this.colorSpeedSetting.computeDouble() / 5.0;
            int n = (int)(1000.0 / d);
            return GetColorHelper.computeColor(n);
        }
        if (this.gradientSetting.matchesString2("Gradient")) {
            return this.colorSetting.computeColor();
        }
        if (ModuleSpecificHelper.matchesModeSetting(this.modeSetting) && this.brutalityHelper != null) {
            double d2 = this.colorSpeedSetting.computeDouble() / 5.0;
            long l = (long)(System.currentTimeMillis() * d2 / 10.0);
            return GetColorHelper.computeColor(30, (int)(l % timestamp2), this.brutalityHelper.getColor(), this.brutalityHelper.getColor2(), true);
        }
        return Color.CYAN;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender3D && this.checkState44()) {
                this.process3();
            }
        }
    }

    public FOVView() {
        super("FOV View", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.fovRangeSetting,
                this.viewDistanceSetting,
                this.opacitySetting,
                this.modeSetting,
                this.gradientSetting,
                this.colorSetting,
                this.gradientColorSetting,
                this.colorSpeedSetting,
                this.bothSetting,
                this.followYawSetting,
                this.lineWidthSetting
            }
        );
        this.process();
    }

    private void process() {
        this.brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.modeSetting);
    }

    private void handleDouble2(double d, double d2, double d3, float f, float f2, float f3, Color color, float f4) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(6, DefaultVertexFormats.field_181705_e);
        GetColorHelper.handleColor(GetColorHelper.computeColor2(color, (int)(f4 * 255.0F * 0.3)));
        worldRenderer.func_181662_b(d, d2, d3).func_181675_d();
        int n = (int)Math.max(8.0F, (f3 - f2) / 5.0F);

        for (int i = 0; i <= n; i++) {
            float rad = (float)Math.toRadians(f2 + (f3 - f2) * i / n);
            double d4 = Math.sin(rad) * f;
            double d5 = Math.cos(rad) * f;
            worldRenderer.func_181662_b(d + d4, d2, d3 + d5).func_181675_d();
        }

        tessellator.draw();
    }

    private void handleDouble(double d, double d2, double d3, float f, float f2, float f3, float f4) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(6, DefaultVertexFormats.field_181705_e);
        Color color = this.colorSetting.computeColor();
        GetColorHelper.handleColor(GetColorHelper.computeColor2(color, (int)(f4 * 255.0F * 0.5)));
        worldRenderer.func_181662_b(d, d2, d3).func_181675_d();
        int n = (int)Math.max(8.0F, (f3 - f2) / 5.0F);

        for (int i = 0; i <= n; i++) {
            float f5 = (float)i / n;
            Color color2 = GetColorHelper.computeColor(this.colorSetting.computeColor(), this.gradientColorSetting.computeColor(), f5);
            GetColorHelper.handleColor(GetColorHelper.computeColor2(color2, (int)(f4 * 255.0F * 0.3)));
            float rad = (float)Math.toRadians(f2 + (f3 - f2) * f5);
            double d4 = Math.sin(rad) * f;
            double d5 = Math.cos(rad) * f;
            worldRenderer.func_181662_b(d + d4, d2, d3 + d5).func_181675_d();
        }

        tessellator.draw();
    }

    private void handleDouble(double d, double d2, double d3, float f, float f2, float f3, Color color, float f4) {
        GetColorHelper.handleColor(GetColorHelper.computeColor2(color, (int)(f4 * 255.0F)));
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
        int n = (int)Math.max(8.0F, (f3 - f2) / 3.0F);

        for (int i = 0; i <= n; i++) {
            float rad = (float)Math.toRadians(f2 + (f3 - f2) * i / n);
            double d4 = Math.sin(rad) * f;
            double d5 = Math.cos(rad) * f;
            worldRenderer.func_181662_b(d + d4, d2, d3 + d5).func_181675_d();
        }

        tessellator.draw();
        if (f3 - f2 < 359.0F) {
            worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
            float rad2 = (float)Math.toRadians(f2);
            worldRenderer.func_181662_b(d, d2, d3).func_181675_d();
            worldRenderer.func_181662_b(d + Math.sin(rad2) * f, d2, d3 + Math.cos(rad2) * f).func_181675_d();
            float rad3 = (float)Math.toRadians(f3);
            worldRenderer.func_181662_b(d, d2, d3).func_181675_d();
            worldRenderer.func_181662_b(d + Math.sin(rad3) * f, d2, d3 + Math.cos(rad3) * f).func_181675_d();
            tessellator.draw();
        }
    }

    private void process3() {
        if (mc.thePlayer != null) {
            double d = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks;
            double d2 = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks;
            double d3 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks;
            double d4 = d - mc.getRenderManager().viewerPosX;
            double d5 = d2 - mc.getRenderManager().viewerPosY + 0.1;
            double d6 = d3 - mc.getRenderManager().viewerPosZ;
            float f = (float)this.fovRangeSetting.computeDouble();
            float f2 = (float)this.viewDistanceSetting.computeDouble();
            Color color = this.computeColor();
            float f3 = (float)this.opacitySetting.computeDouble() / 100.0F;
            GL11.glPushMatrix();
            GL11.glPushAttrib((int)timestamp);
            boolean flag = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            boolean flag2 = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            boolean flag3 = GL11.glIsEnabled(GL11.GL_BLEND);
            boolean flag4 = GL11.glIsEnabled(GL11.GL_LINE_SMOOTH);
            boolean flag5 = GL11.glIsEnabled(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDepthMask(false);
            GL11.glLineWidth((float)this.lineWidthSetting.computeDouble());
            float f4 = this.followYawSetting.isEnabled() ? -mc.thePlayer.rotationYaw - f / 2.0F : -(f / 2.0F);
            float f5 = f4 + f;
            if (this.bothSetting.getText().equals("Filled") || this.bothSetting.getText().equals("Both")) {
                if (this.gradientSetting.getText().equals("Gradient")) {
                    this.handleDouble(d4, d5, d6, f2, f4, f5, f3);
                } else {
                    this.handleDouble2(d4, d5, d6, f2, f4, f5, color, f3);
                }
            }

            if (this.bothSetting.getText().equals("Outlined") || this.bothSetting.getText().equals("Both")) {
                this.handleDouble(d4, d5, d6, f2, f4, f5, color, f3);
            }

            GL11.glDepthMask(true);
            GlStateManager.resetColor();
            if (flag5) {
                GL11.glEnable(GL11.GL_CULL_FACE);
            }

            if (flag2) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            if (!flag4) {
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
            }

            if (!flag3) {
                GL11.glDisable(GL11.GL_BLEND);
            }

            if (flag) {
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            GlStateManager.popAttrib();
            GL11.glPopMatrix();
        }
    }
}
