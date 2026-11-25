package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraft.src.Config;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.optifine.CustomItems;
import net.optifine.EmissiveTextures;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.command.impl.DenickCommand;
import org.brutality.event.EventState;
import org.brutality.event.impl.render.GetEntityEvent2;
import org.brutality.event.impl.render.GetStateEvent;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.pit.hypixel.automation.PinTimer;
import org.brutality.module.impl.pit.hypixel.hud.ToxicInfo;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.module.impl.pit.hypixel.utility.JumpBoostDetector;
import org.brutality.settings.impl.BooleanSetting;
import org.lwjgl.opengl.GL11;

public abstract class RendererLivingEntity<T extends EntityLivingBase> extends Render<T> {
    private static final Logger logger = LogManager.getLogger();
    private static final DynamicTexture field_177096_e = new DynamicTexture(16, 16);
    public ModelBase mainModel;
    protected FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
    protected List<LayerRenderer<T>> layerRenderers = Lists.newArrayList();
    protected boolean renderOutlines = false;
    public static float NAME_TAG_RANGE = 64.0F;
    public static float NAME_TAG_RANGE_SNEAK = 32.0F;
    public EntityLivingBase renderEntity;
    public float renderLimbSwing;
    public float renderLimbSwingAmount;
    public float renderAgeInTicks;
    public float renderHeadYaw;
    public float renderHeadPitch;
    public float renderScaleFactor;
    public float renderPartialTicks;
    private boolean renderModelPushMatrix;
    private boolean renderLayersPushMatrix;
    public static final boolean animateModelLiving = Boolean.getBoolean("animate.model.living");

    public RendererLivingEntity(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn);
        this.mainModel = modelBaseIn;
        this.shadowSize = shadowSizeIn;
        this.renderModelPushMatrix = this.mainModel instanceof ModelSpider;
    }

    public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U layer) {
        return this.layerRenderers.add((LayerRenderer<T>)layer);
    }

    protected <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean removeLayer(U layer) {
        return this.layerRenderers.remove(layer);
    }

    public ModelBase getMainModel() {
        return this.mainModel;
    }

    protected float interpolateRotation(float par1, float par2, float par3) {
        float f = par2 - par1;

        while (f < -180.0F) {
            f += 360.0F;
        }

        while (f >= 180.0F) {
            f -= 360.0F;
        }

        return par1 + par3 * f;
    }

    public void transformHeldFull3DItemLayer() {
    }

    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity != null && this.mainModel != null) {
            GetStateEvent getStateEvent = new GetStateEvent(entity, this, partialTicks, x, y, z, EventState.PRE);
            getStateEvent.process();
            if (!getStateEvent.cancel2()) {
                if (!Reflector.RenderLivingEvent_Pre_Constructor.exists()
                    || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Pre_Constructor, entity, this, x, y, z)) {
                    if (animateModelLiving) {
                        entity.limbSwingAmount = 1.0F;
                    }

                    GlStateManager.pushMatrix();
                    GlStateManager.disableCull();
                    boolean child = entity.isChild();
                    this.mainModel.swingProgress = this.getSwingProgress((T)entity, partialTicks);
                    this.mainModel.isRiding = entity.isRiding();
                    if (Reflector.ForgeEntity_shouldRiderSit.exists() && entity.ridingEntity != null) {
                        this.mainModel.isRiding = Reflector.callBoolean(entity.ridingEntity, Reflector.ForgeEntity_shouldRiderSit);
                    }

                    this.mainModel.isChild = child;

                    try {
                        float f2 = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                        float f3 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
                        float[] floats = KillAura.computeFloatArray(entity, partialTicks);
                        if (floats != null) {
                            f2 = floats[0];
                            f3 = floats[1];
                        }

                        Float val = Scaffold.computeFloatType(entity, partialTicks);
                        if (val != null) {
                            f2 = val;
                            f3 = val;
                        }

                        float f4 = f3 - f2;
                        if (this.mainModel.isRiding && entity.ridingEntity instanceof EntityLivingBase living) {
                            f2 = this.interpolateRotation(living.prevRenderYawOffset, living.renderYawOffset, partialTicks);
                            f4 = MathHelper.wrapAngleTo180_float(f3 - f2);
                            float f5 = MathHelper.clamp_float(f4, -85.0F, 85.0F);
                            f2 = f3 - f5;
                            if (f5 * f5 > 2500.0F) {
                                f2 += f5 * 0.2F;
                            }

                            f4 = f3 - f2;
                        }

                        float f6 = entity == this.renderManager.livingPlayer
                            ? entity.prevRotationPitchHead + (entity.rotationPitchHead - entity.prevRotationPitchHead) * partialTicks
                            : entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                        if (floats != null) {
                            f6 = floats[2];
                        }

                        Float val2 = Scaffold.computeFloatType2(entity, partialTicks);
                        if (val2 != null) {
                            f6 = val2;
                        }

                        this.renderLivingAt((T)entity, x, y, z);
                        float f7 = this.handleRotationFloat((T)entity, partialTicks);
                        this.rotateCorpse((T)entity, f7, f2, partialTicks);
                        GlStateManager.enableRescaleNormal();
                        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
                        this.preRenderCallback((T)entity, partialTicks);
                        GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
                        float f8 = Math.min(1.0F, entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks);
                        float f9 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
                        if (child) {
                            f9 *= 3.0F;
                        }

                        GlStateManager.enableAlpha();
                        if (this.mainModel != null) {
                            this.mainModel.setLivingAnimations(entity, f9, f8, partialTicks);
                            this.mainModel.setRotationAngles(f9, f8, f7, f4, f6, 0.0625F, entity);
                        }

                        if (CustomEntityModels.isActive()) {
                            this.renderEntity = entity;
                            this.renderLimbSwing = f9;
                            this.renderLimbSwingAmount = f8;
                            this.renderAgeInTicks = f7;
                            this.renderHeadYaw = f4;
                            this.renderHeadPitch = f6;
                            this.renderScaleFactor = 0.0625F;
                            this.renderPartialTicks = partialTicks;
                        }

                        if (this.renderOutlines) {
                            boolean flag = this.setScoreTeamColor((T)entity);
                            this.renderModel((T)entity, f9, f8, f7, f4, f6, 0.0625F);
                            if (flag) {
                                this.unsetScoreTeamColor();
                            }
                        } else {
                            boolean flag2 = this.setDoRenderBrightness((T)entity, partialTicks);
                            boolean active = EmissiveTextures.isActive();
                            if (active) {
                                EmissiveTextures.beginRender();
                            }

                            if (this.renderModelPushMatrix) {
                                GlStateManager.pushMatrix();
                            }

                            this.renderModel((T)entity, f9, f8, f7, f4, f6, 0.0625F);
                            if (this.renderModelPushMatrix) {
                                GlStateManager.popMatrix();
                            }

                            if (active) {
                                if (EmissiveTextures.hasEmissive()) {
                                    this.renderModelPushMatrix = true;
                                    EmissiveTextures.beginRenderEmissive();
                                    GlStateManager.pushMatrix();
                                    this.renderModel((T)entity, f9, f8, f7, f4, f6, 0.0625F);
                                    GlStateManager.popMatrix();
                                    EmissiveTextures.endRenderEmissive();
                                }

                                EmissiveTextures.endRender();
                            }

                            if (flag2) {
                                this.unsetBrightness();
                            }

                            GlStateManager.depthMask(true);
                            if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
                                this.renderLayers((T)entity, f9, f8, partialTicks, f7, f4, f6, 0.0625F);
                            }
                        }

                        if (CustomEntityModels.isActive()) {
                            this.renderEntity = null;
                        }

                        GlStateManager.disableRescaleNormal();
                    } catch (Exception e) {
                        logger.error(
                            "Couldn't render entity: "
                                + (entity != null ? entity.getClass().getSimpleName() + " at " + entity.posX + ", " + entity.posY + ", " + entity.posZ : "null entity"),
                            (Throwable)e
                        );
                    }

                    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                    GlStateManager.enableTexture2D();
                    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                    GlStateManager.enableCull();
                    GlStateManager.popMatrix();
                    if (!this.renderOutlines) {
                        super.doRender((T)entity, x, y, z, entityYaw, partialTicks);
                    }

                    if (Reflector.RenderLivingEvent_Post_Constructor.exists()) {
                        Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Post_Constructor, entity, this, x, y, z);
                    }

                    GetStateEvent getStateEvent2 = new GetStateEvent(entity, this, partialTicks, x, y, z, EventState.POST);
                    getStateEvent2.process();
                }
            }
        }
    }

    protected boolean setScoreTeamColor(T entityLivingBaseIn) {
        int n = 16777215;
        if (entityLivingBaseIn instanceof EntityPlayer) {
            ScorePlayerTeam team = (ScorePlayerTeam)entityLivingBaseIn.getTeam();
            if (team != null) {
                String formatFromString = FontRenderer.getFormatFromString(team.getColorPrefix());
                if (formatFromString.length() >= 2) {
                    n = this.getFontRendererFromRenderManager().getColorCode(formatFromString.charAt(1));
                }
            }
        }

        float f = (n >> 16 & 0xFF) / 255.0F;
        float f2 = (n >> 8 & 0xFF) / 255.0F;
        float f3 = (n & 0xFF) / 255.0F;
        GlStateManager.disableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(f, f2, f3, 1.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        return true;
    }

    protected void unsetScoreTeamColor() {
        GlStateManager.enableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    protected void renderModel(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        if (entitylivingbaseIn != null && this.mainModel != null) {
            boolean flag = !entitylivingbaseIn.isInvisible();
            boolean flag2 = !flag && Minecraft.getMinecraft().thePlayer != null && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
            if (flag || flag2) {
                if (!this.bindEntityTexture((T)entitylivingbaseIn)) {
                    return;
                }

                if (flag2) {
                    GlStateManager.pushMatrix();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
                }

                this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
                if (flag2) {
                    GlStateManager.disableBlend();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                    GlStateManager.popMatrix();
                    GlStateManager.depthMask(true);
                }
            }
        }
    }

    protected boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks) {
        return this.setBrightness((T)entityLivingBaseIn, partialTicks, true);
    }

    protected boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures) {
        float brightness = entitylivingbaseIn.getBrightness(partialTicks);
        int colorMultiplier = this.getColorMultiplier((T)entitylivingbaseIn, brightness, partialTicks);
        boolean flag = (colorMultiplier >> 24 & 0xFF) > 0;
        boolean flag2 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
        if (!flag && !flag2) {
            return false;
        }
        if (!flag && !combineTextures) {
            return false;
        }
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        this.brightnessBuffer.position(0);
        if (flag2) {
            this.brightnessBuffer.put(1.0F);
            this.brightnessBuffer.put(0.0F);
            this.brightnessBuffer.put(0.0F);
            this.brightnessBuffer.put(0.3F);
            if (Config.isShaders()) {
                Shaders.setEntityColor(1.0F, 0.0F, 0.0F, 0.3F);
            }
        } else {
            float f = (colorMultiplier >> 24 & 0xFF) / 255.0F;
            float f2 = (colorMultiplier >> 16 & 0xFF) / 255.0F;
            float f3 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
            float f4 = (colorMultiplier & 0xFF) / 255.0F;
            this.brightnessBuffer.put(f2);
            this.brightnessBuffer.put(f3);
            this.brightnessBuffer.put(f4);
            this.brightnessBuffer.put(1.0F - f);
            if (Config.isShaders()) {
                Shaders.setEntityColor(f2, f3, f4, 1.0F - f);
            }
        }

        this.brightnessBuffer.flip();
        GL11.glTexEnv(8960, 8705, this.brightnessBuffer);
        GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(field_177096_e.getGlTextureId());
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        return true;
    }

    protected void unsetBrightness() {
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
        GlStateManager.disableTexture2D();
        GlStateManager.bindTexture(0);
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
        GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        if (Config.isShaders()) {
            Shaders.setEntityColor(0.0F, 0.0F, 0.0F, 0.0F);
        }
    }

    protected void renderLivingAt(T entityLivingBaseIn, double x, double y, double z) {
        GlStateManager.translate((float)x, (float)y, (float)z);
    }

    protected void rotateCorpse(T bat, float p_77043_2_, float p_77043_3_, float partialTicks) {
        GlStateManager.rotate(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);
        if (bat.deathTime > 0) {
            float f = (bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            float f2 = MathHelper.sqrt_float(f);
            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            GlStateManager.rotate(f2 * this.getDeathMaxRotation((T)bat), 0.0F, 0.0F, 1.0F);
        } else {
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(bat.getCommandSenderName());
            if (textWithoutFormattingCodes != null
                && (textWithoutFormattingCodes.equals("Dinnerbone") || textWithoutFormattingCodes.equals("Grumm"))
                && (!(bat instanceof EntityPlayer) || ((EntityPlayer)bat).isWearing(EnumPlayerModelParts.CAPE))) {
                GlStateManager.translate(0.0F, bat.height + 0.1F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            }
        }
    }

    protected float getSwingProgress(T livingBase, float partialTickTime) {
        return livingBase.getSwingProgress(partialTickTime);
    }

    protected float handleRotationFloat(T livingBase, float partialTicks) {
        return livingBase.ticksExisted + partialTicks;
    }

    protected void renderLayers(T entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_) {
        if (entitylivingbaseIn != null && this.layerRenderers != null) {
            boolean flag = CustomItems.beginEntityRender(entitylivingbaseIn);

            try {
                for (LayerRenderer layerRenderer : this.layerRenderers) {
                    if (layerRenderer != null) {
                        boolean flag2 = this.setBrightness((T)entitylivingbaseIn, partialTicks, layerRenderer.shouldCombineTextures());
                        if (EmissiveTextures.isActive()) {
                            EmissiveTextures.beginRender();
                        }

                        if (this.renderLayersPushMatrix) {
                            GlStateManager.pushMatrix();
                        }

                        layerRenderer.doRenderLayer(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);
                        if (this.renderLayersPushMatrix) {
                            GlStateManager.popMatrix();
                        }

                        if (EmissiveTextures.isActive()) {
                            if (EmissiveTextures.hasEmissive()) {
                                this.renderLayersPushMatrix = true;
                                EmissiveTextures.beginRenderEmissive();
                                GlStateManager.pushMatrix();
                                layerRenderer.doRenderLayer(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);
                                GlStateManager.popMatrix();
                                EmissiveTextures.endRenderEmissive();
                            }

                            EmissiveTextures.endRender();
                        }

                        if (flag2) {
                            this.unsetBrightness();
                        }
                    }
                }
            } finally {
                CustomItems.endEntityRender(flag);
            }
        }
    }

    protected float getDeathMaxRotation(T entityLivingBaseIn) {
        return 90.0F;
    }

    protected int getColorMultiplier(T entitylivingbaseIn, float lightBrightness, float partialTickTime) {
        return 0;
    }

    protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
    }

    public void renderName(T entity, double x, double y, double z) {
        GetEntityEvent2 getEntityEvent2 = new GetEntityEvent2(entity, this, Minecraft.getMinecraft().timer.renderPartialTicks, x, y, z);
        getEntityEvent2.process();
        if (!getEntityEvent2.cancel2()) {
            if (this.canRenderName((T)entity)) {
                double dist = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
                float f = entity.isSneaking() ? 32.0F : 64.0F;
                if (dist < f * f) {
                    String text = entity.getDisplayName().getFormattedText();
                    String str = text;
                    if (entity instanceof EntityPlayer player) {
                        String name = player.getName();
                        String str2 = DenickCommand.computeString(name);
                        if (str2 != null && !str2.equalsIgnoreCase(name)) {
                            str = text + " " + EnumChatFormatting.GRAY + "(" + EnumChatFormatting.YELLOW + str2 + EnumChatFormatting.GRAY + ")";
                        } else {
                            String str3 = DenickCommand.computeString2(name);
                            if (str3 != null && !str3.equalsIgnoreCase(name)) {
                                str = text + " " + EnumChatFormatting.GRAY + "[Nick: " + EnumChatFormatting.YELLOW + str3 + EnumChatFormatting.GRAY + "]";
                            }
                        }

                        String pinnedPrefix = this.getPinnedPrefix(player);
                        if (!pinnedPrefix.isEmpty()) {
                            str = pinnedPrefix + " " + str;
                        }

                        String jumpBoostPrefix = this.getJumpBoostPrefix(player);
                        if (!jumpBoostPrefix.isEmpty()) {
                            str = jumpBoostPrefix + " " + str;
                        }

                        String venomedPrefix = this.getVenomedPrefix(player);
                        if (!venomedPrefix.isEmpty()) {
                            str = venomedPrefix + " " + str;
                        }

                        String toxicPrefix = this.getToxicPrefix(player);
                        if (!toxicPrefix.isEmpty()) {
                            str = toxicPrefix + " " + str;
                        }

                        if (DenickCommand.matchesString(name)) {
                            str = EnumChatFormatting.GREEN + "[D] " + str;
                        }
                    }

                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                    if (entity.isSneaking()) {
                        FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float)x, (float)y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float)z);
                        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                        GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                        GlStateManager.translate(0.0F, 9.374999F, 0.0F);
                        GlStateManager.disableLighting();
                        GlStateManager.depthMask(false);
                        GlStateManager.enableBlend();
                        GlStateManager.disableTexture2D();
                        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                        int n = fontRendererFromRenderManager.getStringWidth(str) / 2;
                        Tessellator tessellator = Tessellator.getInstance();
                        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
                        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
                        worldRenderer.func_181662_b(-n - 1, -1.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
                        worldRenderer.func_181662_b(-n - 1, 8.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
                        worldRenderer.func_181662_b(n + 1, 8.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
                        worldRenderer.func_181662_b(n + 1, -1.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
                        tessellator.draw();
                        GlStateManager.enableTexture2D();
                        GlStateManager.depthMask(true);
                        fontRendererFromRenderManager.drawString(str, -fontRendererFromRenderManager.getStringWidth(str) / 2, 0, 553648127);
                        GlStateManager.enableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.popMatrix();
                    } else {
                        this.renderOffsetLivingLabel((T)entity, x, y - (entity.isChild() ? entity.height / 2.0F : 0.0), z, str, 0.02666667F, dist);
                    }
                }
            }
        }
    }

    private String getPinnedPrefix(EntityPlayer player) {
        PinTimer instance = PinTimer.getInstance();
        if (instance == null || !instance.checkState44()) {
            return "";
        }
        if (player == Minecraft.getMinecraft().thePlayer) {
            return "";
        }
        int n = instance.computeInt(player.getName());
        if (n <= 0) {
            return "";
        }
        String str = instance.matchesString2(player.getName()) ? "§a" : "§c";
        String str2 = instance.matchesString2(player.getName()) ? "§2" : "§4";
        return str2 + "[" + str + n + "s" + str2 + "]";
    }

    private String getVenomedPrefix(EntityPlayer player) {
        VenomedTimer instance = VenomedTimer.getInstance();
        if (instance != null && instance.checkState44()) {
            BooleanSetting detectForOthersSetting = instance.getDetectForOthersSetting();
            if (detectForOthersSetting == null || !detectForOthersSetting.isEnabled()) {
                return "";
            }
            if (player == Minecraft.getMinecraft().thePlayer) {
                return "";
            }
            if (!instance.getDetectForOthersSetting().isEnabled()) {
                return "";
            }
            int n = instance.computeInt3(player.getName());
            return n <= 0 ? "" : "§5[§d" + n + "s§5]";
        }
        return "";
    }

    private String getToxicPrefix(EntityPlayer player) {
        ToxicInfo instance = ToxicInfo.getInstance();
        if (instance == null || !instance.checkState44()) {
            return "";
        }
        if (player == Minecraft.getMinecraft().thePlayer) {
            return "";
        }
        if (!instance.showForOthersSetting.isEnabled()) {
            return "";
        }
        int n = instance.computeInt(player.getName());
        return n <= 0 ? "" : "§4[§c-" + n + "%§4]";
    }

    private String getJumpBoostPrefix(EntityPlayer player) {
        if (player == Minecraft.getMinecraft().thePlayer) {
            return "";
        }
        JumpBoostDetector instance = JumpBoostDetector.getInstance();
        if (instance != null && instance.checkState44()) {
            int n = instance.computeInt(player.getName());
            return n > 0 ? "§2[§aJB " + n + "s§2]" : "";
        }
        return "";
    }

    protected boolean canRenderName(T entity) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (entity instanceof EntityPlayer && entity != player) {
            Team team = entity.getTeam();
            Team team2 = player.getTeam();
            if (team != null) {
                EnumVisible nameTagVisibility = team.getNameTagVisibility();
                switch (nameTagVisibility) {
                    case ALWAYS:
                        return true;
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return team2 == null || team.isSameTeam(team2);
                    case HIDE_FOR_OWN_TEAM:
                        return team2 == null || !team.isSameTeam(team2);
                    default:
                        return true;
                }
            }
        }

        return Minecraft.isGuiEnabled() && entity != this.renderManager.livingPlayer && !entity.isInvisibleToPlayer(player) && entity.riddenByEntity == null;
    }

    public void setRenderOutlines(boolean renderOutlinesIn) {
        this.renderOutlines = renderOutlinesIn;
    }

    public List<LayerRenderer<T>> getLayerRenderers() {
        return this.layerRenderers;
    }

    static {
        int[] textureData = field_177096_e.getTextureData();

        for (int i = 0; i < 256; i++) {
            textureData[i] = -1;
        }

        field_177096_e.updateDynamicTexture();
    }
}
