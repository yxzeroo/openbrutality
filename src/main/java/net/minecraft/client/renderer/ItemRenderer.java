package net.minecraft.client.renderer;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.brutality.Brutality;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.NoSlowDown;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.visual.Animations;
import org.brutality.module.impl.visual.Chams;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.player.PlayerHelper13;
import org.brutality.utils.render.HeldItemCustomization;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ItemRenderer {
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
    private final Minecraft mc;
    private ItemStack itemToRender;
    private float animationPartialTicks;
    private float equippedProgress;
    private float prevEquippedProgress;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;
    private int equippedItemSlot = -1;
    private int rotate;

    public ItemRenderer(Minecraft mcIn) {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getRenderItem();
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, TransformType transform) {
        if (heldStack != null) {
            Item item = heldStack.getItem();
            Block blockFromItem = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            Animations instance = Animations.getInstance();
            boolean flag = instance != null
                && entityIn == this.mc.thePlayer
                && instance.matchesItemStack2(heldStack)
                && (transform == TransformType.FIRST_PERSON || transform == TransformType.THIRD_PERSON && instance.checkState5());
            boolean flag2 = flag && GL11.glIsEnabled(GL11.GL_NORMALIZE);

            try (HeldItemCustomization.SharpDraw sharpDraw = flag ? HeldItemCustomization.beginSharpDraw() : null) {
                if (flag) {
                    instance.process();
                    GlStateManager.enableNormalize();
                }

                if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
                    GlStateManager.scale(2.0F, 2.0F, 2.0F);
                    if (this.isBlockTranslucent(blockFromItem) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask)) {
                        GlStateManager.depthMask(false);
                    }
                }

                this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);
                if (this.isBlockTranslucent(blockFromItem)) {
                    GlStateManager.depthMask(true);
                }
            } finally {
                if (flag && !flag2) {
                    GlStateManager.disableNormalize();
                }

                GlStateManager.popMatrix();
            }
        }
    }

    private boolean isBlockTranslucent(Block blockIn) {
        return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    private void func_178101_a(float angle, float p_178101_2_) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(p_178101_2_, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void func_178109_a(AbstractClientPlayer clientPlayer) {
        int combinedLight = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
        if (Config.isDynamicLights()) {
            combinedLight = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), combinedLight);
        }

        float f = combinedLight & 65535;
        float f2 = combinedLight >> 16;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f2);
    }

    private void func_178110_a(EntityPlayerSP entityplayerspIn, float partialTicks) {
        if (!PlayerHelper13.matchesEntity(entityplayerspIn)) {
            float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
            float f2 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
            GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate((entityplayerspIn.rotationYaw - f2) * 0.1F, 0.0F, 1.0F, 0.0F);
        }
    }

    private float func_178100_c(float p_178100_1_) {
        float f = 1.0F - p_178100_1_ / 45.0F + 0.1F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
    }

    private void renderRightArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.25F, -0.85F, 0.75F);
        renderPlayerIn.renderRightArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderLeftArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-0.3F, -1.1F, 0.45F);
        renderPlayerIn.renderLeftArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderPlayerArms(AbstractClientPlayer clientPlayer) {
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        Render entityRenderObject = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer renderPlayer = (RenderPlayer)entityRenderObject;
        if (!clientPlayer.isInvisible()) {
            GlStateManager.disableCull();
            this.renderRightArm(renderPlayer);
            this.renderLeftArm(renderPlayer);
            GlStateManager.enableCull();
        }
    }

    private void renderItemMap(AbstractClientPlayer clientPlayer, float p_178097_2_, float p_178097_3_, float p_178097_4_) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI);
        float f2 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI * 2.0F);
        float f3 = -0.2F * MathHelper.sin(p_178097_4_ * (float) Math.PI);
        GlStateManager.translate(f, f2, f3);
        float f4 = this.func_178100_c(p_178097_2_);
        GlStateManager.translate(0.0F, 0.04F, -0.72F);
        GlStateManager.translate(0.0F, p_178097_3_ * -1.2F, 0.0F);
        GlStateManager.translate(0.0F, f4 * -0.5F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f4 * -85.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        this.renderPlayerArms(clientPlayer);
        float sin = MathHelper.sin(p_178097_4_ * p_178097_4_ * (float) Math.PI);
        float sin2 = MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI);
        GlStateManager.rotate(sin * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(sin2 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(sin2 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-1.0F, -1.0F, 0.0F);
        GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldRenderer.func_181662_b(-7.0, 135.0, 0.0).func_181673_a(0.0, 1.0).func_181675_d();
        worldRenderer.func_181662_b(135.0, 135.0, 0.0).func_181673_a(1.0, 1.0).func_181675_d();
        worldRenderer.func_181662_b(135.0, -7.0, 0.0).func_181673_a(1.0, 0.0).func_181675_d();
        worldRenderer.func_181662_b(-7.0, -7.0, 0.0).func_181673_a(0.0, 0.0).func_181675_d();
        tessellator.draw();
        MapData mapData = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);
        if (mapData != null) {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapData, false);
        }
    }

    private void func_178095_a(AbstractClientPlayer clientPlayer, float p_178095_2_, float p_178095_3_) {
        float f = -0.3F * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI);
        float f2 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI * 2.0F);
        float f3 = -0.4F * MathHelper.sin(p_178095_3_ * (float) Math.PI);
        GlStateManager.translate(f, f2, f3);
        GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178095_2_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float sin = MathHelper.sin(p_178095_3_ * p_178095_3_ * (float) Math.PI);
        float sin2 = MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI);
        GlStateManager.rotate(sin2 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(sin * -20.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        GlStateManager.translate(-1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        GlStateManager.translate(5.6F, 0.0F, 0.0F);
        Render entityRenderObject = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        GlStateManager.disableCull();
        RenderPlayer renderPlayer = (RenderPlayer)entityRenderObject;
        renderPlayer.renderRightArm(this.mc.thePlayer);
        GlStateManager.enableCull();
    }

    private void func_178105_d(float p_178105_1_) {
        float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI);
        float f2 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI * 2.0F);
        float f3 = -0.2F * MathHelper.sin(p_178105_1_ * (float) Math.PI);
        GlStateManager.translate(f, f2, f3);
    }

    private void func_178104_a(AbstractClientPlayer clientPlayer, float p_178104_2_) {
        float f = clientPlayer.getItemInUseCount() - p_178104_2_ + 1.0F;
        float f2 = f / this.itemToRender.getMaxItemUseDuration();
        float f3 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
        if (f2 >= 0.8F) {
            f3 = 0.0F;
        }

        GlStateManager.translate(0.0F, f3, 0.0F);
        Animations instance = Animations.getInstance();
        if (instance == null || !instance.matchesItemStack2(this.itemToRender) || !instance.rotationlessDrinkSetting.isEnabled()) {
            float f4 = 1.0F - (float)Math.pow(f2, 27.0);
            GlStateManager.translate(f4 * 0.6F, f4 * -0.5F, f4 * 0.0F);
            GlStateManager.rotate(f4 * 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f4 * 10.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f4 * 30.0F, 0.0F, 0.0F, 1.0F);
        }
    }

    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float sin = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float sin2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(sin * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(sin2 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(sin2 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void func_178098_a(float p_178098_1_, AbstractClientPlayer clientPlayer) {
        GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.9F, 0.2F, 0.0F);
        float f = this.itemToRender.getMaxItemUseDuration() - (clientPlayer.getItemInUseCount() - p_178098_1_ + 1.0F);
        float f2 = f / 20.0F;
        float f3 = (f2 * f2 + f2 * 2.0F) / 3.0F;
        if (f3 > 1.0F) {
            f3 = 1.0F;
        }

        if (f3 > 0.1F) {
            float sin = MathHelper.sin((f - 0.1F) * 1.3F);
            float f4 = f3 - 0.1F;
            float f5 = sin * f4;
            GlStateManager.translate(f5 * 0.0F, f5 * 0.01F, f5 * 0.0F);
        }

        GlStateManager.translate(f3 * 0.0F, f3 * 0.0F, f3 * 0.1F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F + f3 * 0.2F);
    }

    private void func_178103_d() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    private boolean fakeShovelBlock() {
        Animations instance = Animations.getInstance();
        return instance != null && instance.matchesItemStack(this.itemToRender);
    }

    public boolean fakeBlock() {
        if (this.fakeShovelBlock()) {
            return true;
        }
        if (this.isViaSwordBlockVisual()) {
            return true;
        }
        if (KillAura.matchesEntityLivingBase6(this.mc.thePlayer)) {
            return true;
        }
        KillAura killAura = (KillAura)Brutality.moduleManager.computeModule("Kill Aura");
        NoSlowDown noSlowDown = (NoSlowDown)Brutality.moduleManager.computeModule("NoSlowDown");
        if (killAura != null
            && killAura.verusSetting != null
            && killAura.checkState44()
            && (
                killAura.blocking
                    || (killAura.verusSetting.matchesString2("Watchdog") || killAura.verusSetting.matchesString2("Grim"))
                        && KillAura.entityLivingBase2 != null
                        && GetCurrentScreenHelper.checkState6()
                        && (KillAura.getInstance().flag33 || !KillAura.getInstance().manualBlockSetting.isEnabled())
            )) {
            String text = killAura.verusSetting.getText();
            if (!text.equalsIgnoreCase("None")) {
                return true;
            }
        }

        return noSlowDown != null && noSlowDown.checkState44() && noSlowDown.fakeSetting.getText().equalsIgnoreCase("Fake") && noSlowDown.flag8;
    }

    private boolean isViaSwordBlockVisual() {
        if (this.itemToRender != null
            && this.itemToRender.getItem() instanceof ItemSword
            && this.mc.thePlayer != null
            && Mouse.isCreated()
            && Mouse.isButtonDown(1)) {
            ViaLoadingBase instance = ViaLoadingBase.getInstance();
            if (instance != null && instance.getTargetVersion().newerThan(ProtocolVersion.v1_8)) {
                KillAura instance2 = KillAura.instance;
                return instance2 == null || !KillAura.matchesEntityPlayer(this.mc.thePlayer, this.itemToRender);
            }
            return false;
        }
        return false;
    }

    private void doBlockTransformations() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    private void doSwingAnimation(String mode, float equip, float swing) {
        Animations instance = Animations.getInstance();
        if (instance.checkState()) {
            this.func_178105_d(swing);
            this.transformFirstPersonItem(equip, swing);
        } else {
            boolean flag = "Spin".equals(mode);
            if (!flag) {
                this.func_178105_d(swing);
            }

            this.transformFirstPersonItem(equip, flag ? 0.0F : swing);
            float sin = MathHelper.sin(swing * (float) Math.PI);
            float sin2 = MathHelper.sin(swing * swing * (float) Math.PI);
            switch (mode) {
                case "Slash":
                    GlStateManager.rotate(-sin2 * 30.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case "Tap":
                case "Push":
                    GlStateManager.translate(0.0F, sin2 * 0.2F, 0.0F);
                    break;
                case "Spin":
                    this.applySpin(instance);
                    break;
                case "Swong":
                    GlStateManager.rotate(sin * 30.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case "Stab":
                    GlStateManager.translate(0.0F, 0.0F, sin * 0.4F);
                    break;
                case "Swank":
                    GlStateManager.rotate(sin * 20.0F, 0.0F, 0.0F, 1.0F);
                    break;
                case "Exhibition":
                    GlStateManager.rotate(-sin2 * 40.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case "Summer":
                    GlStateManager.rotate(sin * 20.0F, 1.0F, 0.0F, 1.0F);
                    break;
                case "Autumn":
                    GlStateManager.rotate(sin * 30.0F, 1.0F, 1.0F, 0.0F);
                    break;
                case "Winter":
                    GlStateManager.rotate(sin2 * 25.0F, 0.0F, 0.0F, -1.0F);
                    break;
                case "Spring":
                    GlStateManager.rotate(sin * 25.0F, 0.0F, 1.0F, -1.5F);
                    break;
                case "Punch":
                    GlStateManager.translate(0.0F, 0.0F, sin * 0.3F);
                    break;
                case "Zoom":
                    GlStateManager.translate(0.0F, 0.0F, -sin * 0.4F);
                    break;
                case "Swing":
                    GlStateManager.rotate(sin * 45.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case "Slide":
                    GlStateManager.translate(sin * 0.3F, 0.0F, 0.0F);
                    break;
                case "Wave":
                    GlStateManager.rotate(sin * 20.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.translate(0.0F, sin * 0.2F, 0.0F);
            }
        }
    }

    private void applySpin(Animations animations) {
        float f = animations.computeFloat(this.animationPartialTicks);
        if (animations.spinXAxisSetting.isEnabled()) {
            GlStateManager.rotate(f, 1.0F, 0.0F, 0.0F);
        }

        if (animations.spinYAxisSetting.isEnabled()) {
            GlStateManager.rotate(f, 0.0F, 1.0F, 0.0F);
        }

        if (animations.spinZAxisSetting.isEnabled()) {
            GlStateManager.rotate(f, 0.0F, 0.0F, 1.0F);
        }
    }

    public void doBlockAnimation(String anim, float f, float f1) {
        Animations instance = Animations.getInstance();
        if (instance != null && instance.checkState44()) {
            switch (anim) {
                case "Virtue":
                    this.transformFirstPersonItem(f, f1);
                    this.doBlockTransformations();
                    GL11.glTranslated(-0.25, 0.2, 0.0);
                    GL11.glTranslatef(-0.05F, this.mc.thePlayer.isSneaking() ? -0.2F : 0.0F, 0.1F);
                    break;
                case "1.8":
                    this.transformFirstPersonItem(f / 2.0F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Slash":
                    this.transformFirstPersonItem(f, f1);
                    GL11.glTranslatef(0.1F, 0.2F, 0.3F);
                    GL11.glRotatef(-MathHelper.sin(f1 * f1 * (float) Math.PI) * 30.0F, 1.0F, 0.0F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Spin":
                    this.transformFirstPersonItem(f, 0.0F);
                    this.doBlockTransformations();
                    float f2 = instance.computeFloat(this.animationPartialTicks);
                    if (instance.spinXAxisSetting.isEnabled()) {
                        GL11.glRotatef(f2, 1.0F, 0.0F, 0.0F);
                    }

                    if (instance.spinYAxisSetting.isEnabled()) {
                        GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
                    }

                    if (instance.spinZAxisSetting.isEnabled()) {
                        GL11.glRotatef(f2, 0.0F, 0.0F, 1.0F);
                    }

                    GL11.glTranslatef(0.05F, 0.0F, 0.05F);
                    break;
                case "Swong":
                    this.transformFirstPersonItem(f, f1);
                    GL11.glRotatef(MathHelper.sin(f1 * (float) Math.PI) * 30.0F, 0.0F, 1.0F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Push":
                    this.transformFirstPersonItem(f, 0.0F);
                    GL11.glTranslatef(0.0F, MathHelper.sin(f1 * f1 * (float) Math.PI) * 0.2F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Stab":
                    this.transformFirstPersonItem(f, 0.0F);
                    GL11.glTranslatef(0.0F, 0.0F, MathHelper.sin(f1 * (float) Math.PI) * 0.4F);
                    this.doBlockTransformations();
                    break;
                case "Swank":
                    this.transformFirstPersonItem(f, f1);
                    GL11.glRotatef(MathHelper.sin(f1 * (float) Math.PI) * 20.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(MathHelper.cos(f1 * (float) Math.PI) * 20.0F, 1.0F, 0.0F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Exhibition":
                    this.transformFirstPersonItem(f, 0.0F);
                    GL11.glRotatef(-MathHelper.sin(f1 * f1 * (float) Math.PI) * 40.0F, 1.0F, 1.0F, 1.0F);
                    this.doBlockTransformations();
                    break;
                case "Summer":
                    this.transformFirstPersonItem(f / 2.0F, 0.0F);
                    GL11.glRotatef(MathHelper.cos(f1 * (float) Math.PI) * 20.0F, 1.0F, 0.0F, 1.0F);
                    this.doBlockTransformations();
                    break;
                case "Autumn":
                    this.transformFirstPersonItem(f, f1);
                    GL11.glRotatef(MathHelper.sin(f1 * (float) Math.PI) * 30.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(MathHelper.cos(f1 * (float) Math.PI) * 30.0F, 1.0F, 0.0F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Winter":
                    this.transformFirstPersonItem(f, 0.0F);
                    GL11.glTranslatef(0.35F, 0.0F, 0.0F);
                    GL11.glRotatef(f1 * 5.0F, 0.0F, 0.0F, -15.0F);
                    GL11.glTranslatef(-0.35F, 0.0F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Spring":
                    this.transformFirstPersonItem(f, f1);
                    GL11.glRotatef(MathHelper.cos(f1 * (float) Math.PI) * 25.0F, 0.0F, 1.0F, -1.5F);
                    this.doBlockTransformations();
                    break;
                case "Punch":
                    this.transformFirstPersonItem(f, 0.0F);
                    GL11.glTranslatef(0.0F, 0.0F, MathHelper.sin(f1 * (float) Math.PI) * 0.3F);
                    GL11.glRotatef(-MathHelper.sin(f1 * f1 * (float) Math.PI) * 40.0F, 0.0F, 1.0F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Zoom":
                    this.transformFirstPersonItem(f, 0.0F);
                    GL11.glTranslatef(0.0F, 0.0F, -MathHelper.sin(f1 * (float) Math.PI) * 0.4F);
                    this.doBlockTransformations();
                    break;
                case "Swing":
                    this.transformFirstPersonItem(f, f1);
                    GL11.glRotatef(MathHelper.sin(f1 * (float) Math.PI) * 45.0F, 0.0F, 1.0F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Slide":
                    this.transformFirstPersonItem(f, 0.0F);
                    GL11.glTranslatef(MathHelper.sin(f1 * (float) Math.PI) * 0.3F, 0.0F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Wave":
                    this.transformFirstPersonItem(f, f1);
                    GL11.glRotatef(MathHelper.sin(f1 * (float) Math.PI) * 20.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, MathHelper.cos(f1 * (float) Math.PI) * 0.2F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Tap":
                    this.transformFirstPersonItem(f, f1);
                    GL11.glTranslatef(0.0F, MathHelper.sin(f1 * f1 * (float) Math.PI) * 0.2F, 0.0F);
                    this.doBlockTransformations();
                    break;
                case "Exhibition Tilt":
                case "Interia":
                case "Sigma":
                case "Stella":
                case "Smooth":
                case "Up":
                case "Claude":
                    this.transformFirstPersonItem(f, "Smooth".equals(anim) ? f1 * 0.5F : 0.0F);
                    this.doBlockTransformations();
                    float sin = MathHelper.sin(f1 * (float) Math.PI);
                    switch (anim) {
                        case "Exhibition Tilt":
                            GlStateManager.rotate(-sin * 40.0F, 1.0F, 1.0F, 1.0F);
                            GlStateManager.rotate(15.0F, 0.0F, 0.0F, 1.0F);
                            return;
                        case "Interia":
                            GlStateManager.translate(-sin * 0.15F, sin * 0.1F, 0.0F);
                            return;
                        case "Sigma":
                            GlStateManager.rotate(-sin * 35.0F, 0.0F, 1.0F, 0.0F);
                            return;
                        case "Stella":
                            GlStateManager.rotate(sin * 25.0F, 0.0F, 0.0F, 1.0F);
                            return;
                        case "Up":
                            GlStateManager.translate(0.0F, sin * 0.3F, 0.0F);
                            return;
                        case "Claude":
                            GlStateManager.rotate(-sin * 30.0F, 1.0F, 0.0F, 0.0F);
                            return;
                        default:
                            return;
                    }
                default:
                    this.transformFirstPersonItem(f, f1);
                    this.doBlockTransformations();
            }
        } else {
            this.transformFirstPersonItem(f, f1);
            this.doBlockTransformations();
        }
    }

    private void doBlockAnimation(float equip, float swing) {
        Animations instance = Animations.getInstance();
        this.doBlockAnimation(instance != null ? instance.computeString() : "1.7", equip, swing);
    }

    public void renderItemInFirstPerson(float partialTicks) {
        if (!Config.isShaders() || !Shaders.isSkipRenderHand()) {
            this.animationPartialTicks = partialTicks;
            float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
            EntityPlayerSP player = this.mc.thePlayer;
            float swingProgress = player.getSwingProgress(partialTicks);
            Animations instance = Animations.getInstance();
            if (instance != null && instance.matchesItemStack2(this.itemToRender)) {
                swingProgress = instance.computeFloat(player, partialTicks, swingProgress);
                if (instance.cancelReEquipSetting.isEnabled()) {
                    f = 0.0F;
                }
            }

            float f2 = PlayerHelper13.computeFloat(player, player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks);
            float f3 = PlayerHelper13.computeFloat2(player, player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks);
            this.func_178101_a(f2, f3);
            this.func_178109_a(player);
            this.func_178110_a(player, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            if (this.itemToRender != null) {
                if (this.itemToRender.getItem() instanceof ItemMap) {
                    this.renderItemMap(player, f2, f, swingProgress);
                } else if (player.getItemInUseCount() <= 0 && !this.fakeBlock()) {
                    if (instance != null && instance.matchesItemStack2(this.itemToRender)) {
                        this.doSwingAnimation(instance.waveSetting.getText(), f, swingProgress);
                    } else {
                        this.func_178105_d(swingProgress);
                        this.transformFirstPersonItem(f, swingProgress);
                    }
                } else {
                    EnumAction enumAction = this.fakeShovelBlock() ? EnumAction.BLOCK : this.itemToRender.getItemUseAction();
                    switch (enumAction) {
                        case NONE:
                            this.transformFirstPersonItem(f, 0.0F);
                            break;
                        case EAT:
                        case DRINK:
                            this.func_178104_a(player, partialTicks);
                            this.transformFirstPersonItem(f, 0.0F);
                            break;
                        case BLOCK:
                            this.doBlockAnimation(f, swingProgress);
                            break;
                        case BOW:
                            this.transformFirstPersonItem(f, 0.0F);
                            this.func_178098_a(partialTicks, player);
                    }
                }
            } else if (!player.isInvisible()) {
                this.func_178095_a(player, f, swingProgress);
            }

            this.renderItem(player, this.itemToRender, TransformType.FIRST_PERSON);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
        }
    }

    public void renderOverlays(float partialTicks) {
        GlStateManager.disableAlpha();
        if (this.mc.thePlayer.isEntityInsideOpaqueBlock() && !Chams.checkState()) {
            IBlockState blockState = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos pos = new BlockPos(this.mc.thePlayer);
            EntityPlayerSP player = this.mc.thePlayer;

            for (int i = 0; i < 8; i++) {
                double d = player.posX + ((i >> 0) % 2 - 0.5F) * player.width * 0.8F;
                double d2 = player.posY + ((i >> 1) % 2 - 0.5F) * 0.1F;
                double d3 = player.posZ + ((i >> 2) % 2 - 0.5F) * player.width * 0.8F;
                BlockPos pos2 = new BlockPos(d, d2 + player.getEyeHeight(), d3);
                IBlockState blockState2 = this.mc.theWorld.getBlockState(pos2);
                if (blockState2.getBlock().isVisuallyOpaque()) {
                    blockState = blockState2;
                    pos = pos2;
                }
            }

            if (blockState.getBlock().getRenderType() != -1) {
                Object fieldValue = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);
                if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, this.mc.thePlayer, partialTicks, fieldValue, blockState, pos)) {
                    this.func_178108_a(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState));
                }
            }
        }

        if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water)
                && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, this.mc.thePlayer, partialTicks)) {
                this.renderWaterOverlayTexture(partialTicks);
            }

            if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, this.mc.thePlayer, partialTicks)) {
                this.renderFireInFirstPerson(partialTicks);
            }
        }

        GlStateManager.enableAlpha();
    }

    private void func_178108_a(float p_178108_1_, TextureAtlasSprite p_178108_2_) {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
        GlStateManager.pushMatrix();
        float f2 = -1.0F;
        float f4 = -1.0F;
        float f6 = -0.5F;
        float minU = p_178108_2_.getMinU();
        float maxU = p_178108_2_.getMaxU();
        float minV = p_178108_2_.getMinV();
        float maxV = p_178108_2_.getMaxV();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldRenderer.func_181662_b(-1.0, -1.0, -0.5).func_181673_a(maxU, maxV).func_181675_d();
        worldRenderer.func_181662_b(1.0, -1.0, -0.5).func_181673_a(minU, maxV).func_181675_d();
        worldRenderer.func_181662_b(1.0, 1.0, -0.5).func_181673_a(minU, minV).func_181675_d();
        worldRenderer.func_181662_b(-1.0, 1.0, -0.5).func_181673_a(maxU, minV).func_181675_d();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderWaterOverlayTexture(float p_78448_1_) {
        if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
            this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            float brightness = this.mc.thePlayer.getBrightness(p_78448_1_);
            GlStateManager.color(brightness, brightness, brightness, 0.5F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.pushMatrix();
            float f2 = -1.0F;
            float f4 = -1.0F;
            float f6 = -0.5F;
            float f7 = -this.mc.thePlayer.rotationYaw / 64.0F;
            float f8 = this.mc.thePlayer.rotationPitch / 64.0F;
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            worldRenderer.func_181662_b(-1.0, -1.0, -0.5).func_181673_a(4.0F + f7, 4.0F + f8).func_181675_d();
            worldRenderer.func_181662_b(1.0, -1.0, -0.5).func_181673_a(0.0F + f7, 4.0F + f8).func_181675_d();
            worldRenderer.func_181662_b(1.0, 1.0, -0.5).func_181673_a(0.0F + f7, 0.0F + f8).func_181675_d();
            worldRenderer.func_181662_b(-1.0, 1.0, -0.5).func_181673_a(4.0F + f7, 0.0F + f8).func_181675_d();
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }
    }

    private void renderFireInFirstPerson(float p_78442_1_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.depthFunc(GL11.GL_ALWAYS);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        float f = 1.0F;

        for (int i = 0; i < 2; i++) {
            GlStateManager.pushMatrix();
            TextureAtlasSprite atlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float minU = atlasSprite.getMinU();
            float maxU = atlasSprite.getMaxU();
            float minV = atlasSprite.getMinV();
            float maxV = atlasSprite.getMaxV();
            float f2 = (0.0F - f) / 2.0F;
            float f3 = f2 + f;
            float f4 = 0.0F - f / 2.0F;
            float f5 = f4 + f;
            float f6 = -0.5F;
            GlStateManager.translate(-(i * 2 - 1) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            worldRenderer.setSprite(atlasSprite);
            worldRenderer.func_181662_b(f2, f4, f6).func_181673_a(maxU, maxV).func_181675_d();
            worldRenderer.func_181662_b(f3, f4, f6).func_181673_a(minU, maxV).func_181675_d();
            worldRenderer.func_181662_b(f3, f5, f6).func_181673_a(minU, minV).func_181675_d();
            worldRenderer.func_181662_b(f2, f5, f6).func_181673_a(maxU, minV).func_181675_d();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
    }

    public void updateEquippedItem() {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayerSP player = this.mc.thePlayer;
        ItemStack stack = Scaffold.computeItemStack(player, player.inventory.getCurrentItem());
        int n = Scaffold.computeInt(this.mc.thePlayer);
        Animations instance = Animations.getInstance();
        if (instance != null && instance.matchesItemStack2(stack) && instance.cancelReEquipSetting.isEnabled()) {
            this.itemToRender = stack;
            this.equippedItemSlot = n;
            this.equippedProgress = this.prevEquippedProgress = 1.0F;
            if (Config.isShaders()) {
                Shaders.setItemToRenderMain(stack);
            }
        } else {
            boolean flag = false;
            if (this.itemToRender != null && stack != null) {
                if (!this.itemToRender.getIsItemStackEqual(stack)) {
                    if (Reflector.ForgeItem_shouldCauseReequipAnimation.exists()) {
                        boolean flag2 = Reflector.callBoolean(
                            this.itemToRender.getItem(),
                            Reflector.ForgeItem_shouldCauseReequipAnimation,
                            this.itemToRender,
                            stack,
                            this.equippedItemSlot != n
                        );
                        if (!flag2) {
                            this.itemToRender = stack;
                            this.equippedItemSlot = n;
                            return;
                        }
                    }

                    flag = true;
                }
            } else if (this.itemToRender == null && stack == null) {
                flag = false;
            } else {
                flag = true;
            }

            float f = 0.4F;
            float f2 = flag ? 0.0F : 1.0F;
            float f3 = MathHelper.clamp_float(f2 - this.equippedProgress, -f, f);
            this.equippedProgress += f3;
            if (this.equippedProgress < 0.1F) {
                this.itemToRender = stack;
                this.equippedItemSlot = n;
                if (Config.isShaders()) {
                    Shaders.setItemToRenderMain(stack);
                }
            }
        }
    }

    public void resetEquippedProgress() {
        this.equippedProgress = 0.0F;
    }

    public void resetEquippedProgress2() {
        this.equippedProgress = 0.0F;
    }
}
