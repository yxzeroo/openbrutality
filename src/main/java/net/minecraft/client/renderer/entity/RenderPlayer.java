package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.EventState;
import org.brutality.event.impl.render.GetEntityEvent3;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.visual.Animations;
import org.brutality.module.impl.visual.BrightPlayers;
import org.brutality.module.impl.visual.Chams;
import org.brutality.utils.render.GlStateGuard;

public class RenderPlayer extends RendererLivingEntity<AbstractClientPlayer> {
    private boolean smallArms;

    public RenderPlayer(RenderManager renderManager) {
        this(renderManager, false);
    }

    public RenderPlayer(RenderManager renderManager, boolean useSmallArms) {
        super(renderManager, new ModelPlayer(0.0F, useSmallArms), 0.5F);
        this.smallArms = useSmallArms;
        this.addLayer(new LayerBipedArmor(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerDeadmau5Head(this));
        this.addLayer(new LayerCape(this));
        this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
    }

    public ModelPlayer getMainModel() {
        return (ModelPlayer)super.getMainModel();
    }

    public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!entity.isUser() || this.renderManager.livingPlayer == entity) {
            double d = y;
            if (entity.isSneaking() && !(entity instanceof EntityPlayerSP)) {
                d = y - 0.125;
            }

            if (BrightPlayers.getInstance().checkState44()) {
                GlStateManager.disableLighting();
            }

            GetEntityEvent3 getEntityEvent3 = new GetEntityEvent3(entity, EventState.PRE);
            getEntityEvent3.process();

            try (GlStateGuard renderHelper7 = Chams.computeRenderHelper7(entity)) {
                this.setModelVisibilities(entity);
                super.doRender(entity, x, d, z, entityYaw, partialTicks);
            }

            GetEntityEvent3 getEntityEvent32 = new GetEntityEvent3(entity, EventState.POST);
            getEntityEvent32.process();
        }
    }

    private void setModelVisibilities(AbstractClientPlayer clientPlayer) {
        ModelPlayer mainModel = this.getMainModel();
        if (clientPlayer.isSpectator()) {
            mainModel.setInvisible(false);
            mainModel.bipedHead.showModel = true;
            mainModel.bipedHeadwear.showModel = true;
        } else {
            ItemStack stack = Scaffold.computeItemStack(clientPlayer, clientPlayer.inventory.getCurrentItem());
            mainModel.setInvisible(true);
            mainModel.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            mainModel.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            mainModel.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            mainModel.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            mainModel.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            mainModel.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            mainModel.heldItemLeft = 0;
            mainModel.aimedBow = false;
            mainModel.isSneak = clientPlayer.isSneaking();
            if (stack == null) {
                mainModel.heldItemRight = 0;
            } else {
                mainModel.heldItemRight = 1;
                Animations instance = Animations.getInstance();
                if (clientPlayer == Minecraft.getMinecraft().thePlayer && instance != null && instance.checkState5() && instance.matchesItemStack(stack)) {
                    mainModel.heldItemRight = 3;
                }

                if (stack.getItemUseAction() == EnumAction.BLOCK && KillAura.matchesEntityLivingBase6(clientPlayer)) {
                    mainModel.heldItemRight = 3;
                }

                if (clientPlayer.getItemInUseCount() > 0) {
                    EnumAction itemUseAction = stack.getItemUseAction();
                    if (itemUseAction == EnumAction.BLOCK) {
                        mainModel.heldItemRight = 3;
                    } else if (itemUseAction == EnumAction.BOW) {
                        mainModel.aimedBow = true;
                    }
                }
            }
        }
    }

    protected ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
        return entity.getLocationSkin();
    }

    @Override
    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0F, 0.1875F, 0.0F);
    }

    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime) {
        float f = 0.9375F;
        GlStateManager.scale(f, f, f);
    }

    protected void renderOffsetLivingLabel(AbstractClientPlayer entityIn, double x, double y, double z, String str, float p_177069_9_, double p_177069_10_) {
        if (p_177069_10_ < 100.0) {
            Scoreboard worldScoreboard = entityIn.getWorldScoreboard();
            ScoreObjective objectiveInDisplaySlot = worldScoreboard.getObjectiveInDisplaySlot(2);
            if (objectiveInDisplaySlot != null) {
                Score valueFromObjective = worldScoreboard.getValueFromObjective(entityIn.getCommandSenderName(), objectiveInDisplaySlot);
                this.renderLivingLabel(entityIn, valueFromObjective.getScorePoints() + " " + objectiveInDisplaySlot.getDisplayName(), x, y, z, 64);
                y += this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * p_177069_9_;
            }
        }

        super.renderOffsetLivingLabel(entityIn, x, y, z, str, p_177069_9_, p_177069_10_);
    }

    public void renderRightArm(AbstractClientPlayer clientPlayer) {
        float f = 1.0F;
        GlStateManager.color(f, f, f);
        ModelPlayer mainModel = this.getMainModel();
        this.setModelVisibilities(clientPlayer);
        mainModel.swingProgress = 0.0F;
        mainModel.isSneak = false;
        mainModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        mainModel.renderRightArm();
    }

    public void renderLeftArm(AbstractClientPlayer clientPlayer) {
        float f = 1.0F;
        GlStateManager.color(f, f, f);
        ModelPlayer mainModel = this.getMainModel();
        this.setModelVisibilities(clientPlayer);
        mainModel.isSneak = false;
        mainModel.swingProgress = 0.0F;
        mainModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        mainModel.renderLeftArm();
    }

    protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z) {
        if (entityLivingBaseIn.isEntityAlive() && entityLivingBaseIn.isPlayerSleeping()) {
            super.renderLivingAt(entityLivingBaseIn, x + entityLivingBaseIn.renderOffsetX, y + entityLivingBaseIn.renderOffsetY, z + entityLivingBaseIn.renderOffsetZ);
        } else {
            super.renderLivingAt(entityLivingBaseIn, x, y, z);
        }
    }

    protected void rotateCorpse(AbstractClientPlayer bat, float p_77043_2_, float p_77043_3_, float partialTicks) {
        if (bat.isEntityAlive() && bat.isPlayerSleeping()) {
            GlStateManager.rotate(bat.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        } else {
            super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);
        }
    }
}
