package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.player.KOS;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.WHelper;
import org.brutality.utils.animation.IsFinishedHelper;
import org.brutality.utils.animation.LinearType;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class TargetESP extends Module {
    private final ModeSetting customSetting = new ModeSetting(
        "Mode", this, "Brutality", Arrays.asList("Brutality", "Exhibition", "Scanner", "Brutality Old", "Souls", "Custom")
    );
    private final BooleanSetting multipleTargetESPSetting = new BooleanSetting("Multiple Target ESP", this, true);
    private final BooleanSetting switchTargetESPSetting = new BooleanSetting("Switch Target ESP", this, false)
        .computeBooleanSetting(() -> this.multipleTargetESPSetting.isEnabled());
    private final NumberSetting opacitySetting = new NumberSetting("Opacity", this, 50.0, 1.0, 100.0, 1.0);
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final ModeSetting hurttimeSetting = new ModeSetting(
        "Color Mode", this, "Chroma", Arrays.asList("Theme", "ArrayList", "Custom", "Custom2", "Rainbow", "Chroma", "Primecheats", "Hurttime")
    );
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 0.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting) && !this.hurttimeSetting.matchesString2("Theme"));
    private final ColorSetting customColor2Setting = new ColorSetting("Custom Color 2", this, 0.33F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting) && !this.hurttimeSetting.matchesString2("Theme"));
    private final NumberSetting colorSpeedSetting = new NumberSetting("Color Speed", this, 5.0, 1.0, 20.0, 1.0);
    private final ColorSetting hurtColorSetting = new ColorSetting("Hurt Color", this, 0.0F);
    private final ColorSetting normalColorSetting = new ColorSetting("Normal Color", this, 0.33F);
    private final NumberSetting widthSetting = new NumberSetting("Width", this, 0.8, 0.1, 5.0, 0.05)
        .computeNumberSetting(() -> this.customSetting.matchesString2("Custom"));
    private final NumberSetting heightSetting = new NumberSetting("Height", this, 1.9, 0.1, 5.0, 0.05)
        .computeNumberSetting(() -> this.customSetting.matchesString2("Custom"));
    private BrutalityHelper brutalityHelper;
    private static final ResourceLocation resourceLocation = new ResourceLocation("textures/bloom.png");
    private static final long timestamp = 300L;
    private static final long timestamp2 = 700L;
    private static final long timestamp3 = 1000L;
    private final IsFinishedHelper isFinishedHelper = new IsFinishedHelper(LinearType.EASE_IN_OUT_SINE, 300L);
    private float factor = 0.0F;
    private EntityLivingBase entityLivingBase;
    private double amount;
    private double amount2;
    private double amount3;
    private float factor2 = 0.6F;
    private float factor3 = 1.8F;
    private long timestamp4;

    private double computeDouble(double d, double d2) {
        return d2 + (d - d2) * mc.timer.renderPartialTicks;
    }

    private void handleEntityLivingBase(EntityLivingBase living) {
        if (mc.thePlayer != null && mc.theWorld != null && mc.getRenderManager() != null) {
            float renderPartialTicks = mc.timer.renderPartialTicks;
            if (living != null) {
                this.handleEntityLivingBase8(living);
                this.timestamp4 = 0L;
                this.isFinishedHelper.setTimestamp(300L);
                this.isFinishedHelper.handleDouble2(1.0);
            } else if (this.entityLivingBase != null) {
                if (this.timestamp4 == 0L) {
                    this.timestamp4 = System.currentTimeMillis();
                }

                if (this.entityLivingBase.isEntityAlive() && !this.entityLivingBase.isDead) {
                    this.handleEntityLivingBase8(this.entityLivingBase);
                }

                boolean flag = System.currentTimeMillis() - this.timestamp4 < 1000L;
                this.isFinishedHelper.setTimestamp(flag ? 300L : 700L);
                this.isFinishedHelper.handleDouble2(flag ? 1.0 : 0.0);
            } else {
                this.isFinishedHelper.handleDouble2(0.0);
            }

            this.factor += 2.5F;
            if (this.entityLivingBase != null && !(this.isFinishedHelper.getValue() <= 0.001)) {
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                GlStateManager.enableDepth();
                GlStateManager.disableCull();
                GlStateManager.depthMask(false);
                GlStateManager.disableLighting();
                if (this.matchesFloat(renderPartialTicks)) {
                    GlStateManager.disableDepth();
                }

                double d = this.amount - mc.getRenderManager().renderPosX;
                double d2 = this.amount2 - mc.getRenderManager().renderPosY;
                double d3 = this.amount3 - mc.getRenderManager().renderPosZ;
                GlStateManager.pushMatrix();
                GlStateManager.translate(d, d2, d3);
                this.process3();
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
                GlStateManager.enableCull();
                GlStateManager.enableDepth();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            } else {
                if (living == null) {
                    this.entityLivingBase = null;
                }
            }
        }
    }

    private void handleEntityLivingBase7(EntityLivingBase living) {
        if (living != null && living.isEntityAlive() && mc.getRenderManager() != null) {
            double d = this.computeDouble(living.posX, living.lastTickPosX);
            double d2 = this.computeDouble(living.posY, living.lastTickPosY);
            double d3 = this.computeDouble(living.posZ, living.lastTickPosZ);
            double d4 = this.widthSetting.computeDouble() / 2.0;
            Color color = this.computeColor(living);
            float f = Math.max((float)this.opacitySetting.computeDouble() / 100.0F, 0.15F);

            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableCull();
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                ResetColorHelper.handleColor(GetColorHelper.computeColor2(color, (int)(f * 255.0F)));
                ResetColorHelper.handleAxisAlignedBB3(
                    new AxisAlignedBB(d - d4, d2, d3 - d4, d + d4, d2 + this.heightSetting.computeDouble(), d3 + d4)
                );
            }
        }
    }

    private void handleEntityLivingBase5(EntityLivingBase living) {
        if (this.customSetting.matchesString2("Custom")) {
            this.handleEntityLivingBase7(living);
        } else if (this.customSetting.matchesString2("Scanner")) {
            this.handleEntityLivingBase3(living);
        } else if (this.customSetting.matchesString2("Brutality")) {
            this.handleEntityLivingBase4(living);
        } else if (this.customSetting.matchesString2("Exhibition")) {
            this.handleEntityLivingBase2(living);
        } else if (this.customSetting.matchesString2("Brutality Old")) {
            this.handleEntityLivingBase6(living);
        }
    }

    public void handleEntityLivingBase3(EntityLivingBase living) {
        float renderPartialTicks = mc.timer.renderPartialTicks;
        if (mc.getRenderManager() != null && living != null) {
            double d = living.prevPosX + (living.posX - living.prevPosX) * renderPartialTicks - mc.getRenderManager().renderPosX;
            double d2 = living.prevPosY
                + (living.posY - living.prevPosY) * renderPartialTicks
                + Math.sin(System.currentTimeMillis() / 200.0)
                + 1.0
                - mc.getRenderManager().renderPosY;
            double d3 = living.prevPosZ + (living.posZ - living.prevPosZ) * renderPartialTicks - mc.getRenderManager().renderPosZ;
            Color color = this.computeColor(living);
            float f = Math.max((float)this.opacitySetting.computeDouble() / 100.0F, 0.15F);
            int n = (int)(f * 180.0F);
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            double d4 = 0.67 + 0.08 * Math.sin(System.currentTimeMillis() / 400.0);
            double d5 = 0.25 + 0.1 * Math.abs(Math.sin(System.currentTimeMillis() / 300.0));
            byte b = 30;

            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableFog();
                GlStateManager.disableDepth();
                GlStateManager.disableAlpha();
                GlStateManager.disableCull();
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldRenderer = tessellator.getWorldRenderer();
                worldRenderer.func_181668_a(6, DefaultVertexFormats.field_181706_f);
                worldRenderer.func_181662_b(d, d2, d3).func_181669_b(red, green, blue, n).func_181675_d();

                for (int i = 0; i <= b; i++) {
                    double d6 = i * Math.PI * 2.0 / b;
                    worldRenderer.func_181662_b(d + d4 * Math.cos(d6), d2, d3 + d4 * Math.sin(d6))
                        .func_181669_b(red, green, blue, 0)
                        .func_181675_d();
                }

                tessellator.draw();
                worldRenderer.func_181668_a(5, DefaultVertexFormats.field_181706_f);

                for (int j = 0; j <= b; j++) {
                    double d7 = j * Math.PI * 2.0 / b;
                    double d8 = d + d4 * Math.cos(d7);
                    double d9 = d3 + d4 * Math.sin(d7);
                    worldRenderer.func_181662_b(d8, d2, d9).func_181669_b(red, green, blue, n / 2).func_181675_d();
                    worldRenderer.func_181662_b(d8, d2 - d5, d9).func_181669_b(red, green, blue, 0).func_181675_d();
                }

                tessellator.draw();
            }
        }
    }

    private Color computeColor(Color color, Color color2, float f) {
        double d = (1.0 - Math.cos(f * Math.PI * 2.0)) / 2.0;
        return new Color(
            (int)(color.getRed() + (color2.getRed() - color.getRed()) * d),
            (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * d),
            (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * d)
        );
    }

    private void handleFloat(float f, Color color, float f2) {
        float f3 = f / 2.0F;
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, Math.min(1.0F, Math.max(0.0F, f2)));
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(-f3, -f3, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(f3, -f3, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(f3, f3, 0.0F);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(-f3, f3, 0.0F);
        GL11.glEnd();
    }

    private void handleEntityLivingBase2(EntityLivingBase living) {
        if (mc.getRenderManager() != null && living != null) {
            Color color = this.computeColor(living);
            float f = Math.max((float)this.opacitySetting.computeDouble() / 100.0F, 0.15F);
            double d = this.computeDouble(living.posX, living.lastTickPosX) - mc.getRenderManager().renderPosX;
            double d2 = this.computeDouble(living.posY, living.lastTickPosY) - mc.getRenderManager().renderPosY;
            double d3 = this.computeDouble(living.posZ, living.lastTickPosZ) - mc.getRenderManager().renderPosZ;
            float f2 = living.width / 1.5F;
            float f3 = living.height + (living.isSneaking() ? -0.2F : 0.1F);
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            boolean flag = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            boolean flag2 = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            boolean flag3 = GL11.glIsEnabled(GL11.GL_BLEND);
            boolean flag4 = GL11.glIsEnabled(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glLineWidth(2.0F);
            ResetColorHelper.handleColor(GetColorHelper.computeColor2(color, (int)(f * 255.0F)));
            float f4 = this.computeFloat(living.prevRotationYaw, living.rotationYaw);
            double rad = Math.toRadians(f4);
            double sin = Math.sin(rad);
            double cos = Math.cos(rad);
            double[][] doublesArray = new double[][]{
                {-f2, 0.0, -f2},
                {f2, 0.0, -f2},
                {f2, 0.0, f2},
                {-f2, 0.0, f2},
                {-f2, f3, -f2},
                {f2, f3, -f2},
                {f2, f3, f2},
                {-f2, f3, f2}
            };
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);

            for (int i = 0; i < 4; i++) {
                int n = (i + 1) % 4;
                double d4 = d + doublesArray[i][0] * cos - doublesArray[i][2] * sin;
                double d5 = d3 + doublesArray[i][0] * sin + doublesArray[i][2] * cos;
                double d6 = d2 + doublesArray[i][1];
                double d7 = d + doublesArray[n][0] * cos - doublesArray[n][2] * sin;
                double d8 = d3 + doublesArray[n][0] * sin + doublesArray[n][2] * cos;
                double d9 = d2 + doublesArray[n][1];
                worldRenderer.func_181662_b(d4, d6, d5).func_181675_d();
                worldRenderer.func_181662_b(d7, d9, d8).func_181675_d();
            }

            for (int j = 4; j < 8; j++) {
                int n2 = 4 + (j - 4 + 1) % 4;
                double d10 = d + doublesArray[j][0] * cos - doublesArray[j][2] * sin;
                double d11 = d3 + doublesArray[j][0] * sin + doublesArray[j][2] * cos;
                double d12 = d2 + doublesArray[j][1];
                double d13 = d + doublesArray[n2][0] * cos - doublesArray[n2][2] * sin;
                double d14 = d3 + doublesArray[n2][0] * sin + doublesArray[n2][2] * cos;
                double d15 = d2 + doublesArray[n2][1];
                worldRenderer.func_181662_b(d10, d12, d11).func_181675_d();
                worldRenderer.func_181662_b(d13, d15, d14).func_181675_d();
            }

            for (int k = 0; k < 4; k++) {
                double d16 = d + doublesArray[k][0] * cos - doublesArray[k][2] * sin;
                double d17 = d3 + doublesArray[k][0] * sin + doublesArray[k][2] * cos;
                double d18 = d2 + doublesArray[k][1];
                double d19 = d + doublesArray[k + 4][0] * cos - doublesArray[k + 4][2] * sin;
                double d20 = d3 + doublesArray[k + 4][0] * sin + doublesArray[k + 4][2] * cos;
                double d21 = d2 + doublesArray[k + 4][1];
                worldRenderer.func_181662_b(d16, d18, d17).func_181675_d();
                worldRenderer.func_181662_b(d19, d21, d20).func_181675_d();
            }

            tessellator.draw();
            GL11.glDepthMask(true);
            GlStateManager.resetColor();
            if (flag2) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            if (!flag4) {
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
            }

            if (flag) {
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            if (!flag3) {
                GL11.glDisable(GL11.GL_BLEND);
            }

            GlStateManager.popAttrib();
            GL11.glPopMatrix();
        }
    }

    private void process4() {
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
    }

    private void process() {
        this.brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.modeSetting);
    }

    private float computeFloat(float f, float f2) {
        float f3 = f2 - f;

        while (f3 < -180.0F) {
            f3 += 360.0F;
        }

        while (f3 >= 180.0F) {
            f3 -= 360.0F;
        }

        return f + f3 * mc.timer.renderPartialTicks;
    }

    private void handleEntityLivingBase6(EntityLivingBase living) {
        if (mc.getRenderManager() != null && living != null) {
            Color color = this.computeColor(living);
            int n = (int)(Math.max(this.opacitySetting.computeDouble() / 100.0, 0.25) * 255.0);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableTexture2D();
            GlStateManager.disableCull();
            GlStateManager.disableAlpha();
            GlStateManager.disableDepth();
            double d = this.computeDouble(living.posX, living.lastTickPosX);
            double d2 = this.computeDouble(living.posY, living.lastTickPosY);
            double d3 = this.computeDouble(living.posZ, living.lastTickPosZ);
            AxisAlignedBB box = living.getEntityBoundingBox()
                .expand(0.1, 0.1, 0.1)
                .offset(d - living.posX, d2 - living.posY, d3 - living.posZ)
                .offset(-mc.getRenderManager().renderPosX, -mc.getRenderManager().renderPosY, -mc.getRenderManager().renderPosZ);
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
            worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181669_b(red, green, blue, n).func_181675_d();
            tessellator.draw();
            GlStateManager.resetColor();
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GlStateManager.enableCull();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    private EntityLivingBase computeEntityLivingBase(KillAura killAura) {
        EntityLivingBase living = killAura.computeEntityLivingBase();
        if (living != null && living.isEntityAlive()) {
            this.entityLivingBase = living;
            return living;
        }
        return null;
    }

    private Color computeColor(EntityLivingBase living) {
        if (this.hurttimeSetting.matchesString2("ArrayList")) {
            return ArrayList.computeColor(this);
        }
        this.process();
        double d = 15000.0 / this.colorSpeedSetting.computeDouble();
        float f = (float)(System.currentTimeMillis() % (long)d / d);
        boolean flag = this.hurttimeSetting.matchesString2("Theme") || ModuleSpecificHelper.matchesModeSetting(this.modeSetting);
        if (flag) {
            BrutalityHelper brutalityHelper = this.hurttimeSetting.matchesString2("Theme") ? ModuleSpecificHelper.computeBrutalityHelper() : this.brutalityHelper;
            if (brutalityHelper != null) {
                return this.computeColor(brutalityHelper.getColor(), brutalityHelper.getColor2(), f);
            }
        }

        if (this.hurttimeSetting.matchesString2("Custom")) {
            return this.customColorSetting.computeColor();
        }
        if (this.hurttimeSetting.matchesString2("Custom2")) {
            return this.computeColor(this.customColorSetting.computeColor(), this.customColor2Setting.computeColor(), f);
        }
        if (this.hurttimeSetting.matchesString2("Primecheats")) {
            return computeColor(f);
        }
        if (this.hurttimeSetting.matchesString2("Rainbow") || this.hurttimeSetting.matchesString2("Chroma")) {
            return Color.getHSBColor(f, this.hurttimeSetting.matchesString2("Chroma") ? 1.0F : 0.85F, 1.0F);
        }
        if (!this.hurttimeSetting.matchesString2("Hurttime")) {
            return Color.WHITE;
        }
        return living != null && living.hurtTime > 0 ? this.hurtColorSetting.computeColor() : this.normalColorSetting.computeColor();
    }

    private void process3() {
        Color color = this.computeColor(this.entityLivingBase);
        float val = (float)this.isFinishedHelper.getValue();
        float f = val * Math.max((float)this.opacitySetting.computeDouble() / 100.0F, 0.15F) * 2.0F;
        float f2 = Math.min(1.0F, f);
        float f3 = this.factor2 * 1.5F;
        float f4 = this.factor3;
        mc.getTextureManager().bindTexture(resourceLocation);
        byte b = 2;
        int n = 0;
        int n2 = 0;

        for (int i = 0; i < 360; i += b) {
            float f5 = 0.13F + 0.005F * n;
            float f6 = 0.7F + 0.005F * n;
            if (n2 > 0) {
                n2 -= b;
            } else {
                n += b;
                if (n > 50) {
                    n2 = 100;
                    n = 0;
                } else {
                    float f7 = Math.max(0.5F, 1.2F - 0.5F * val);
                    float f8 = (float)(Math.sin(Math.toRadians(i + this.factor)) * f3 * f7);
                    float f9 = (float)(Math.cos(Math.toRadians(i + this.factor)) * f3 * f7);
                    float f10 = f4 / 1.5F + f4 / 3.0F * (float)Math.sin(Math.toRadians(i / 2.0F + this.factor / 5.0F));
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(f8, f10, f9);
                    this.process4();
                    this.handleFloat(f6, color, f2 * 0.05F);
                    this.handleFloat(f5, color, f2);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    private boolean matchesFloat(float f) {
        if (mc.getRenderViewEntity() == null) {
            return false;
        }
        Vec3 positionEyes = mc.getRenderViewEntity().getPositionEyes(f);
        MovingObjectPosition hit = mc.theWorld
            .rayTraceBlocks(positionEyes, new Vec3(this.amount, this.amount2 + this.factor3 * 0.85, this.amount3), false, true, false);
        return hit != null && hit.typeOfHit == MovingObjectType.BLOCK;
    }

    private void handleFloat(float f, float f2, float f3, float f4) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(9, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(f3, f2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f, f2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f4, 0.0).func_181675_d();
        tessellator.draw();
    }

    private void handleEntityLivingBase8(EntityLivingBase living) {
        this.amount = this.computeDouble(living.posX, living.lastTickPosX);
        this.amount2 = this.computeDouble(living.posY, living.lastTickPosY);
        this.amount3 = this.computeDouble(living.posZ, living.lastTickPosZ);
        this.factor2 = living.width;
        this.factor3 = living.height;
    }

    private static Color computeColor(float f) {
        double d = f * Math.PI * 2.0;
        double d2 = (1.0 + Math.cos(d)) * 0.5;
        double d3 = (1.0 + Math.cos(d - (Math.PI * 2.0 / 3.0))) * 0.5;
        double d4 = (1.0 + Math.cos(d + (Math.PI * 2.0 / 3.0))) * 0.5;
        double d5 = 255.0 / Math.max(d2, Math.max(d3, d4));
        return new Color((int)Math.round(d2 * d5), (int)Math.round(d3 * d5), (int)Math.round(d4 * d5));
    }

    private void handleEntityLivingBase4(EntityLivingBase living) {
        if (mc.getRenderManager() != null && living != null) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            boolean flag = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            boolean flag2 = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            boolean flag3 = GL11.glIsEnabled(GL11.GL_BLEND);
            boolean flag4 = GL11.glIsEnabled(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glLineWidth(1.8F);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GlStateManager.depthMask(true);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            double d = this.computeDouble(living.posX, living.lastTickPosX);
            double d2 = this.computeDouble(living.posY, living.lastTickPosY);
            double d3 = this.computeDouble(living.posZ, living.lastTickPosZ);
            double d4 = 0.45;
            double d5 = 1.8;
            Color color = this.computeColor(living);
            float f = Math.max((float)this.opacitySetting.computeDouble() / 100.0F, 0.15F);
            ResetColorHelper.handleColor(GetColorHelper.computeColor2(color, (int)(f * 255.0F)));
            ResetColorHelper.handleAxisAlignedBB3(new AxisAlignedBB(d - d4, d2, d3 - d4, d + d4, d2 + d5, d3 + d4));
            GL11.glDepthMask(true);
            GlStateManager.resetColor();
            if (!flag4) {
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
            }

            if (flag) {
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            if (flag2) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            if (!flag3) {
                GL11.glDisable(GL11.GL_BLEND);
            }

            GlStateManager.popAttrib();
            GL11.glPopMatrix();
        }
    }

    public TargetESP() {
        super("Target ESP", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.customSetting,
                this.multipleTargetESPSetting,
                this.switchTargetESPSetting,
                this.opacitySetting,
                this.modeSetting,
                this.hurttimeSetting,
                this.customColorSetting,
                this.customColor2Setting,
                this.colorSpeedSetting,
                this.hurtColorSetting,
                this.normalColorSetting,
                this.widthSetting,
                this.heightSetting
            }
        );
        this.process();
    }

    private List<EntityLivingBase> computeList(List<EntityLivingBase> livings) {
        if (livings != null && KOS.getInstance().checkState44()) {
            java.util.ArrayList list = new java.util.ArrayList();

            for (EntityLivingBase living : livings) {
                if (living instanceof EntityPlayer && WHelper.matchesString(living.getName())) {
                    list.add(living);
                }
            }

            return (List<EntityLivingBase>)(list.isEmpty() ? livings : list);
        }
        return livings;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPartialTicksEvent && this.checkState44()) {
                KillAura killAura = (KillAura)Brutality.moduleManager.computeModule("Kill Aura");
                if (killAura == null || !killAura.checkState44()) {
                    return;
                }

                EntityLivingBase living = killAura.computeEntityLivingBase();
                if (living == null && !this.customSetting.matchesString2("Souls")) {
                    return;
                }

                if (this.customSetting.matchesString2("Souls")) {
                    this.handleEntityLivingBase(this.computeEntityLivingBase(killAura));
                    return;
                }

                boolean flag = this.multipleTargetESPSetting.isEnabled()
                    && (
                        killAura.multipleSetting.getText().equalsIgnoreCase("Multiple")
                            || this.switchTargetESPSetting.isEnabled() && killAura.multipleSetting.getText().equalsIgnoreCase("Switch")
                    );
                if (flag) {
                    var livings = killAura.multipleSetting.getText().equalsIgnoreCase("Switch") ? killAura.computeList() : killAura.entries5;
                    livings = this.computeList(livings);
                    if (livings != null && !livings.isEmpty()) {
                        for (EntityLivingBase living2 : livings) {
                            if (living2 != null) {
                                this.handleEntityLivingBase5(living2);
                            }
                        }
                    }
                } else if (living != null) {
                    this.handleEntityLivingBase5(living);
                }
            }
        }
    }
}
