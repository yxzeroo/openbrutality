package org.brutality.module.impl.pit.hypixel.hud;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.IsDoneHelper;
import org.brutality.utils.animations.impl.ImplAnimation6;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class SpawnPoints extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final BooleanSetting showBoxSetting = new BooleanSetting("Show Box", this, true);
    private final BooleanSetting showBeaconSetting = new BooleanSetting("Show Beacon", this, true);
    private final BooleanSetting showTextSetting = new BooleanSetting("Show Text", this, true);
    private final BooleanSetting showArrowSetting = new BooleanSetting("Show Arrow", this, true);
    private final BooleanSetting pulseEffectSetting = new BooleanSetting("Pulse Effect", this, true);
    private final BooleanSetting customFontSetting = new BooleanSetting("Custom Font", this, false);
    private final ColorSetting boxColorSetting = new ColorSetting("Box Color", this, 120.0F);
    private final ColorSetting beaconColorSetting = new ColorSetting("Beacon Color", this, 120.0F);
    private final ColorSetting arrowColorSetting = new ColorSetting("Arrow Color", this, 0.0F);
    private final NumberSetting boxSizeSetting = new NumberSetting("Box Size", this, 3.0, 1.0, 10.0, 0.5);
    private final NumberSetting beaconHeightSetting = new NumberSetting("Beacon Height", this, 50.0, 10.0, 100.0, 5.0);
    private final NumberSetting lineWidthSetting = new NumberSetting("Line Width", this, 2.0, 1.0, 5.0, 0.5);
    private final NumberSetting textScaleSetting = new NumberSetting("Text Scale", this, 2.0, 0.5, 10.0, 0.1);
    private final NumberSetting arrowSizeSetting = new NumberSetting("Arrow Size", this, 1.5, 0.5, 3.0, 0.1);
    private final NumberSetting arrowSpeedSetting = new NumberSetting("Arrow Speed", this, 1.0, 0.1, 3.0, 0.1);
    private final NumberSetting arrowDistanceSetting = new NumberSetting("Arrow Distance", this, 3.0, 1.0, 10.0, 0.5);
    private final NumberSetting fillOpacitySetting = new NumberSetting("Fill Opacity", this, 30.0, 0.0, 255.0, 5.0);
    private final NumberSetting outlineOpacitySetting = new NumberSetting("Outline Opacity", this, 150.0, 0.0, 255.0, 5.0);
    private final NumberSetting beaconOpacitySetting = new NumberSetting("Beacon Opacity", this, 100.0, 0.0, 255.0, 5.0);
    private final KeybindListSetting visualSetting = new KeybindListSetting(
        "Visual",
        this,
        this.showBoxSetting,
        this.showBeaconSetting,
        this.showTextSetting,
        this.showArrowSetting,
        this.pulseEffectSetting,
        this.customFontSetting
    );
    private final KeybindListSetting colorsSetting = new KeybindListSetting(
        "Colors", this, this.boxColorSetting, this.beaconColorSetting, this.arrowColorSetting
    );
    private final KeybindListSetting sizeSetting = new KeybindListSetting(
        "Size", this, this.boxSizeSetting, this.beaconHeightSetting, this.lineWidthSetting, this.textScaleSetting, this.arrowSizeSetting
    );
    private final KeybindListSetting animationSetting = new KeybindListSetting("Animation", this, this.arrowSpeedSetting, this.arrowDistanceSetting);
    private final KeybindListSetting opacitySetting = new KeybindListSetting(
        "Opacity", this, this.fillOpacitySetting, this.outlineOpacitySetting, this.beaconOpacitySetting
    );
    private static final Map<String, BlockPos[]> lookupMap = new HashMap<>();
    private static final Map<String, String> lookupMap2 = new HashMap<>();
    private IsDoneHelper isDoneHelper;
    private FontRenderer fontRenderer;

    private void handleBlockPos(BlockPos pos, Color color, float f) {
        double d = pos.getX() + 0.5 - mc.getRenderManager().viewerPosX;
        double d2 = pos.getY() - mc.getRenderManager().viewerPosY;
        double d3 = pos.getZ() + 0.5 - mc.getRenderManager().viewerPosZ;
        double d4 = this.beaconHeightSetting.computeDouble();
        double d5 = this.boxSizeSetting.computeDouble() / 2.0;
        double[][] doublesArray = new double[][]{
            {d - d5, d3 - d5}, {d + d5, d3 - d5}, {d + d5, d3 + d5}, {d - d5, d3 + d5}
        };
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableDepth();
        GL11.glLineWidth((float)this.lineWidthSetting.computeDouble());
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        GL11.glBegin(GL11.GL_LINES);

        for (double[] doubles : doublesArray) {
            GL11.glVertex3d(doubles[0], d2, doubles[1]);
            GL11.glVertex3d(doubles[0], d2 + d4, doubles[1]);
        }

        GL11.glEnd();
        byte b = 5;

        for (int i = 0; i < b; i++) {
            double d6 = d2 + d4 / b * i;
            float f2 = color.getAlpha() / 255.0F * (1.0F - (float)i / b);
            GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f2);
            GL11.glBegin(GL11.GL_LINE_LOOP);

            for (double[] doubles2 : doublesArray) {
                GL11.glVertex3d(doubles2[0], d6, doubles2[1]);
            }

            GL11.glEnd();
        }

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    static {
        lookupMap.put("elemental", new BlockPos[]{new BlockPos(62, 93, -62)});
        lookupMap2.put("elemental", "Elemental");
        lookupMap.put("kings", new BlockPos[]{new BlockPos(-33, 68, 86)});
        lookupMap2.put("kings", "Kings");
        lookupMap.put("genesis", new BlockPos[]{new BlockPos(-65, 64, 65), new BlockPos(64, 64, -64)});
        lookupMap2.put("genesis", "Genesis");
        lookupMap.put("seasons", new BlockPos[]{new BlockPos(-127, 94, -19)});
        lookupMap2.put("seasons", "Seasons");
        lookupMap.put("corals", new BlockPos[]{new BlockPos(-41, 97, 113)});
        lookupMap2.put("corals", "Corals");
    }

    private void onEventRender3D(EventRender3D event) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            String str = FreshHelper.computeString(mc.theWorld);
            if (str != null && lookupMap.containsKey(str)) {
                BlockPos[] posArray = lookupMap.get(str);
                if (posArray != null) {
                    if (this.isDoneHelper.isDone()) {
                        this.isDoneHelper.process();
                    }

                    float factor = event.getFactor();

                    for (BlockPos pos : posArray) {
                        this.handleBlockPos(pos, factor, str);
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventRender3D) {
                    this.onEventRender3D((EventRender3D)event);
                }
            }
        }
    }

    private void handleDouble(double d, double d2, double d3, Color color, float f) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(d, d2, d3);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(f, f, f);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableTexture2D();
        GL11.glLineWidth((float)this.lineWidthSetting.computeDouble() * 2.0F);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        GL11.glVertex2f(0.0F, -3.0F);
        GL11.glVertex2f(-2.0F, 0.0F);
        GL11.glVertex2f(2.0F, 0.0F);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-0.5F, 0.0F);
        GL11.glVertex2f(-0.5F, 3.0F);
        GL11.glVertex2f(0.5F, 3.0F);
        GL11.glVertex2f(0.5F, 0.0F);
        GL11.glEnd();
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.8F);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(0.0F, -3.0F);
        GL11.glVertex2f(-2.0F, 0.0F);
        GL11.glVertex2f(2.0F, 0.0F);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(-0.5F, 0.0F);
        GL11.glVertex2f(-0.5F, 3.0F);
        GL11.glVertex2f(0.5F, 3.0F);
        GL11.glVertex2f(0.5F, 0.0F);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void handleBlockPos2(BlockPos pos, float f, String str) {
        double d = pos.getX() + 0.5 - mc.getRenderManager().viewerPosX;
        double d2 = pos.getY() + this.beaconHeightSetting.computeDouble() + 10.0 - mc.getRenderManager().viewerPosY;
        double d3 = pos.getZ() + 0.5 - mc.getRenderManager().viewerPosZ;
        String orDefault = lookupMap2.getOrDefault(str, "Unknown");
        if (this.showTextSetting.isEnabled()) {
            this.handleString(orDefault, d, d2, d3, (float)this.textScaleSetting.computeDouble());
        }

        if (this.showArrowSetting.isEnabled()) {
            double d4 = this.isDoneHelper.computeDouble() * this.arrowDistanceSetting.computeDouble();
            double d5 = pos.getY() + this.beaconHeightSetting.computeDouble() - 5.0 - d4 - mc.getRenderManager().viewerPosY;
            this.handleDouble(d, d5, d3, this.arrowColorSetting.computeColor(), (float)this.arrowSizeSetting.computeDouble());
        }
    }

    private void handleString(String str, double d, double d2, double d3, float f) {
        FontRenderer fontRenderer = mc.fontRendererObj;
        this.fontRenderer = this.computeFontRenderer2(16.0F);
        boolean enabled = this.customFontSetting.isEnabled();
        GlStateManager.pushMatrix();
        GlStateManager.translate(d, d2, d3);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f * 0.02F, -f * 0.02F, f * 0.02F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.enableTexture2D();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        String str2 = "" + EnumChatFormatting.BOLD + EnumChatFormatting.DARK_RED + "SPAWN POINT ";
        String str3 = EnumChatFormatting.GRAY + "(";
        String str4 = EnumChatFormatting.YELLOW + str;
        String str5 = EnumChatFormatting.GRAY + ")";
        String str6 = str2 + str3 + str4 + str5;
        int n = enabled ? this.fontRenderer.getStringWidth(str6) : fontRenderer.getStringWidth(str6);
        int n2 = enabled ? this.fontRenderer.FONT_HEIGHT : fontRenderer.FONT_HEIGHT;
        float f2 = -n / 2.0F;
        GlStateManager.disableTexture2D();
        GL11.glBegin(GL11.GL_QUADS);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.5F);
        GL11.glVertex2f(f2 - 4.0F, -4.0F);
        GL11.glVertex2f(f2 - 4.0F, n2 + 4);
        GL11.glVertex2f(f2 + n + 4.0F, n2 + 4);
        GL11.glVertex2f(f2 + n + 4.0F, -4.0F);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        if (enabled) {
            this.fontRenderer.drawString(str6, f2 + 1.0F, 1.0F, Integer.MIN_VALUE, false);
            this.fontRenderer.drawString(str6, f2, 0.0F, -1, false);
        } else {
            fontRenderer.drawString(str6, (int)f2 + 1, 1, Integer.MIN_VALUE);
            fontRenderer.drawString(str6, (int)f2, 0, -1);
        }

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public SpawnPoints() {
        super("SpawnPoints", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.visualSetting, this.colorsSetting, this.sizeSetting, this.animationSetting, this.opacitySetting});
        this.isDoneHelper = new ImplAnimation6(1000, 1.0, ForwardsType.FORWARDS);
    }

    private void handleBlockPos(BlockPos pos, float f, String str) {
        float f2 = 1.0F;
        if (this.pulseEffectSetting.isEnabled()) {
            double d = System.currentTimeMillis() / 1000.0;
            f2 = (float)(0.8 + 0.2 * Math.sin(d * 2.0));
        }

        Color color = this.boxColorSetting.computeColor();
        Color color2 = this.beaconColorSetting.computeColor();
        float f3 = (float)this.boxSizeSetting.computeDouble() * f2;
        if (this.showBoxSetting.isEnabled()) {
            Color color3 = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(this.fillOpacitySetting.computeDouble() * f2));
            new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)this.outlineOpacitySetting.computeDouble());
            ResetColorHelper.handleBlockPos2(pos, color3, true, true, (float)this.lineWidthSetting.computeDouble());
        }

        if (this.showBeaconSetting.isEnabled()) {
            Color color4 = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), (int)(this.beaconOpacitySetting.computeDouble() * f2));
            this.handleBlockPos(pos, color4, f);
        }

        if (this.showTextSetting.isEnabled() || this.showArrowSetting.isEnabled()) {
            this.handleBlockPos2(pos, f, str);
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.isDoneHelper.reset();
        }
    }
}
