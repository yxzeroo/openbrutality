package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.lwjgl.opengl.GL11;

public class HitVector extends Module {
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 6.0, 1.0, 20.0, 5.0);
    private final NumberSetting circleSizeSetting = new NumberSetting("Circle Size", this, 0.3, 0.1, 1.0, 5.0);
    private final NumberSetting circleQualitySetting = new NumberSetting("Circle Quality", this, 20.0, 8.0, 50.0, 1.0);
    private final BooleanSetting playersOnlySetting = new BooleanSetting("Players Only", this, true);
    private final BooleanSetting throughWallsSetting = new BooleanSetting("Through Walls", this, false);
    private final ModeSetting distanceSetting = new ModeSetting("Color Mode", this, "Theme", Arrays.asList("Theme", "Custom", "Distance"));
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 0.0F);
    private final ModeSetting brutalitySetting = new ModeSetting("Theme", this, "Brutality", BrutalityHelper.computeList());
    private final BooleanSetting animateSetting = new BooleanSetting("Animate", this, true);
    private final NumberSetting animationSpeedSetting = new NumberSetting("Animation Speed", this, 2.0, 0.5, 5.0, 1.0);
    private final Minecraft mc = Minecraft.getMinecraft();
    private EntityLivingBase entityLivingBase = null;
    private Vec3 vec3 = null;
    private Vec3 vec32 = null;
    private static final double amount = 0.15;

    public HitVector() {
        super("HitVector", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.rangeSetting,
                this.circleSizeSetting,
                this.circleQualitySetting,
                this.playersOnlySetting,
                this.throughWallsSetting,
                this.distanceSetting,
                this.customColorSetting,
                this.brutalitySetting,
                this.animateSetting,
                this.animationSpeedSetting
            }
        );
    }

    private Vec3 computeVec3(Vec3 vec, Vec3 vec2, double d) {
        return new Vec3(
            vec.xCoord + (vec2.xCoord - vec.xCoord) * d,
            vec.yCoord + (vec2.yCoord - vec.yCoord) * d,
            vec.zCoord + (vec2.zCoord - vec.zCoord) * d
        );
    }

    private Vec3 computeVec3(EntityLivingBase living) {
        Vec3 positionEyes = this.mc.thePlayer.getPositionEyes(this.mc.timer.renderPartialTicks);
        double d = this.computeDouble(living.posX, living.prevPosX, this.mc.timer.renderPartialTicks);
        double d2 = this.computeDouble(living.posY, living.prevPosY, this.mc.timer.renderPartialTicks);
        double d3 = this.computeDouble(living.posZ, living.prevPosZ, this.mc.timer.renderPartialTicks);
        AxisAlignedBB entityBoundingBox = living.getEntityBoundingBox();
        double d4 = entityBoundingBox.minX + (d - living.posX);
        double d5 = entityBoundingBox.minY + (d2 - living.posY);
        double d6 = entityBoundingBox.minZ + (d3 - living.posZ);
        double d7 = entityBoundingBox.maxX + (d - living.posX);
        double d8 = entityBoundingBox.maxY + (d2 - living.posY);
        double d9 = entityBoundingBox.maxZ + (d3 - living.posZ);
        double d10 = Math.max(d4, Math.min(positionEyes.xCoord, d7));
        double d11 = Math.max(d5, Math.min(positionEyes.yCoord, d8));
        double d12 = Math.max(d6, Math.min(positionEyes.zCoord, d9));
        return new Vec3(d10, d11, d12);
    }

    private double computeDouble(double d, double d2, double d3) {
        return d2 + (d - d2) * d3;
    }

    private void process() {
        if (this.vec32 != null) {
            double d = this.vec32.xCoord - this.mc.getRenderManager().renderPosX;
            double d2 = this.vec32.yCoord - this.mc.getRenderManager().renderPosY;
            double d3 = this.vec32.zCoord - this.mc.getRenderManager().renderPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.translate(d, d2, d3);
            float yaw = this.mc.thePlayer.rotationYaw;
            float pitch = this.mc.thePlayer.rotationPitch;
            GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(2.0F);
            Color color = this.computeColor();
            double d4 = this.circleSizeSetting.computeDouble();
            if (this.animateSetting.isEnabled()) {
                double d5 = System.currentTimeMillis() / 1000.0 * this.animationSpeedSetting.computeDouble();
                d4 *= 0.8 + 0.4 * Math.sin(d5);
            }

            GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.8F);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.func_181668_a(6, DefaultVertexFormats.field_181705_e);
            worldRenderer.func_181662_b(0.0, 0.0, 0.0).func_181675_d();
            int n = (int)this.circleQualitySetting.computeDouble();

            for (int i = 0; i <= n; i++) {
                double d6 = (Math.PI * 2) * i / n;
                double d7 = Math.cos(d6) * d4;
                double d8 = Math.sin(d6) * d4;
                worldRenderer.func_181662_b(d7, d8, 0.0).func_181675_d();
            }

            tessellator.draw();
            GlStateManager.color(
                Math.min(1.0F, color.getRed() / 255.0F + 0.3F),
                Math.min(1.0F, color.getGreen() / 255.0F + 0.3F),
                Math.min(1.0F, color.getBlue() / 255.0F + 0.3F),
                1.0F
            );
            worldRenderer.func_181668_a(2, DefaultVertexFormats.field_181705_e);

            for (int j = 0; j < n; j++) {
                double d9 = (Math.PI * 2) * j / n;
                double d10 = Math.cos(d9) * d4;
                double d11 = Math.sin(d9) * d4;
                worldRenderer.func_181662_b(d10, d11, 0.0).func_181675_d();
            }

            tessellator.draw();
            GL11.glLineWidth(1.5F);
            worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
            double d12 = d4 * 0.3;
            worldRenderer.func_181662_b(-d12, 0.0, 0.0).func_181675_d();
            worldRenderer.func_181662_b(d12, 0.0, 0.0).func_181675_d();
            worldRenderer.func_181662_b(0.0, -d12, 0.0).func_181675_d();
            worldRenderer.func_181662_b(0.0, d12, 0.0).func_181675_d();
            tessellator.draw();
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private void process3() {
        EntityLivingBase living = null;
        double d = this.rangeSetting.computeDouble();
        Vec3 positionEyes = this.mc.thePlayer.getPositionEyes(this.mc.timer.renderPartialTicks);

        for (Entity entity : EntityCache.computeList()) {
            if (entity != this.mc.thePlayer && entity instanceof EntityLivingBase && (!this.playersOnlySetting.isEnabled() || entity instanceof EntityPlayer)) {
                EntityLivingBase living2 = (EntityLivingBase)entity;
                if (!living2.isDead && !(living2.getHealth() <= 0.0F)) {
                    double dist = this.mc.thePlayer.getDistanceToEntity(entity);
                    if (!(dist > this.rangeSetting.computeDouble())) {
                        if (!this.throughWallsSetting.isEnabled()) {
                            Vec3 vec = new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
                            MovingObjectPosition hit = this.mc.theWorld.rayTraceBlocks(positionEyes, vec, false, true, false);
                            if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK) {
                                continue;
                            }
                        }

                        if (dist < d) {
                            d = dist;
                            living = living2;
                        }
                    }
                }
            }
        }

        this.entityLivingBase = living;
        if (this.entityLivingBase != null) {
            this.vec3 = this.computeVec3(this.entityLivingBase);
        } else {
            this.vec3 = null;
            this.vec32 = null;
        }
    }

    public boolean checkState() {
        return this.entityLivingBase != null && this.vec3 != null;
    }

    private Color computeColor() {
        String text = this.distanceSetting.getText();
        switch (text) {
            case "Custom":
                return this.customColorSetting.computeColor();
            case "Distance":
                if (this.entityLivingBase != null) {
                    double dist = this.mc.thePlayer.getDistanceToEntity(this.entityLivingBase);
                    double d = Math.min(1.0, dist / this.rangeSetting.computeDouble());
                    int n = (int)(255.0 * d);
                    int n2 = (int)(255.0 * (1.0 - d));
                    return new Color(n, n2, 0);
                }

                return Color.WHITE;
            case "Theme":
            default:
                BrutalityHelper brutalityHelper = BrutalityHelper.computeBrutalityHelper(this.brutalitySetting.getText());
                if (brutalityHelper != null) {
                    if (this.animateSetting.isEnabled()) {
                        double d2 = System.currentTimeMillis() / 100.0;
                        return GetColorHelper.computeColor(
                            15, (int)(d2 * this.animationSpeedSetting.computeDouble()) % 360, brutalityHelper.getColor(), brutalityHelper.getColor3(), false
                        );
                    }
                    return brutalityHelper.getColor();
                } else {
                    return Color.RED;
                }
        }
    }

    public EntityLivingBase getEntityLivingBase() {
        return this.entityLivingBase;
    }

    public Vec3 getVec3() {
        return this.vec3;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender3D && this.mc.theWorld != null && this.mc.thePlayer != null) {
                this.process3();
                if (this.entityLivingBase != null && this.vec3 != null) {
                    if (this.vec32 == null) {
                        this.vec32 = this.vec3;
                    } else {
                        this.vec32 = this.computeVec3(this.vec32, this.vec3, 0.15);
                    }

                    this.process();
                } else {
                    this.vec32 = null;
                }
            }
        }
    }
}
