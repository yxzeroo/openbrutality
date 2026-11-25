package org.brutality.module.impl.visual;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.opengl.GL11;

public class CollisionBox extends Module {
    private NumberSetting rangeSetting = new NumberSetting("Range", this, 3.0, 1.0, 6.0, 0.1);
    private BooleanSetting showAllEntitiesSetting = new BooleanSetting("Show All Entities", this, false);
    private NumberSetting lineWidthSetting = new NumberSetting("Line Width", this, 2.0, 0.5, 5.0, 0.1);
    private NumberSetting alphaSetting = new NumberSetting("Alpha", this, 100.0, 10.0, 255.0, 1.0);
    private ColorSetting boxColorSetting = new ColorSetting("Box Color", this, 0.33F, 1.0F, 1.0F);
    private final Minecraft mc = Minecraft.getMinecraft();

    private Entity computeEntity() {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            double d = this.rangeSetting.computeDouble();
            Vec3 positionEyes = this.mc.thePlayer.getPositionEyes(1.0F);
            Vec3 look = this.mc.thePlayer.getLook(1.0F);
            Vec3 vec = positionEyes.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
            Entity entity = null;
            double d2 = d;

            for (Entity entity2 : this.mc
                .theWorld
                .getEntitiesWithinAABBExcludingEntity(
                    this.mc.thePlayer,
                    this.mc.thePlayer.getEntityBoundingBox().addCoord(look.xCoord * d, look.yCoord * d, look.zCoord * d).expand(1.0, 1.0, 1.0)
                )) {
                if (entity2 instanceof EntityLivingBase && entity2.canBeCollidedWith()) {
                    float collisionBorderSize = entity2.getCollisionBorderSize();
                    AxisAlignedBB box = entity2.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                    MovingObjectPosition hit = box.calculateIntercept(positionEyes, vec);
                    if (box.isVecInside(positionEyes)) {
                        if (d2 >= 0.0) {
                            entity = entity2;
                            d2 = 0.0;
                        }
                    } else if (hit != null) {
                        double d3 = positionEyes.distanceTo(hit.hitVec);
                        if (d3 < d2 || d2 == 0.0) {
                            entity = entity2;
                            d2 = d3;
                        }
                    }
                }
            }

            return entity;
        }
        return null;
    }

    private void handleEntityLivingBase(EntityLivingBase living) {
        if (this.mc.getRenderManager() != null) {
            double d = this.computeDouble(living.posX, living.lastTickPosX) - this.mc.getRenderManager().renderPosX;
            double d2 = this.computeDouble(living.posY, living.lastTickPosY) - this.mc.getRenderManager().renderPosY;
            double d3 = this.computeDouble(living.posZ, living.lastTickPosZ) - this.mc.getRenderManager().renderPosZ;
            AxisAlignedBB entityBoundingBox = living.getEntityBoundingBox();
            AxisAlignedBB box = new AxisAlignedBB(
                entityBoundingBox.minX - living.posX + d,
                entityBoundingBox.minY - living.posY + d2,
                entityBoundingBox.minZ - living.posZ + d3,
                entityBoundingBox.maxX - living.posX + d,
                entityBoundingBox.maxY - living.posY + d2,
                entityBoundingBox.maxZ - living.posZ + d3
            );
            Color color = this.boxColorSetting.computeColor();
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glLineWidth((float)this.lineWidthSetting.computeDouble());
            GlStateManager.color(
                color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, (float)this.alphaSetting.computeDouble() / 255.0F
            );
            RenderGlobal.func_181563_a(box, color.getRed(), color.getGreen(), color.getBlue(), (int)this.alphaSetting.computeDouble());
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender3D) {
                if (this.showAllEntitiesSetting.isEnabled()) {
                    this.process();
                } else {
                    Entity entity = this.computeEntity();
                    if (entity instanceof EntityLivingBase) {
                        this.handleEntityLivingBase((EntityLivingBase)entity);
                    }
                }
            }
        }
    }

    private void process() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            for (Entity entity : EntityCache.computeList()) {
                if (entity instanceof EntityLivingBase && entity != this.mc.thePlayer) {
                    double dist = this.mc.thePlayer.getDistanceToEntity(entity);
                    if (dist <= this.rangeSetting.computeDouble()) {
                        this.handleEntityLivingBase((EntityLivingBase)entity);
                    }
                }
            }
        }
    }

    private double computeDouble(double d, double d2) {
        return d2 + (d - d2) * this.mc.timer.renderPartialTicks;
    }

    public CollisionBox() {
        super("CollisionBox", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.rangeSetting, this.showAllEntitiesSetting, this.lineWidthSetting, this.alphaSetting, this.boxColorSetting});
    }
}
