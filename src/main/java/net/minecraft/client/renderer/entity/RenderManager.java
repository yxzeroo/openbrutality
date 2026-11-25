package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.player.PlayerItemsLayer;
import net.optifine.reflect.Reflector;
import org.brutality.event.EventState;
import org.brutality.event.impl.render.GetEntityEvent;
import org.brutality.utils.performance.PrintCopyPasteReportHelper;
import org.brutality.utils.player.GetPitchHelper;
import org.brutality.utils.player.PlayerHelper13;
import org.brutality.utils.player.PlayerHelper5;
import org.lwjgl.opengl.GL11;

public class RenderManager {
    private Map<Class, Render> entityRenderMap = Maps.newHashMap();
    private Map<String, RenderPlayer> skinMap = Maps.newHashMap();
    private RenderPlayer playerRenderer;
    private FontRenderer textRenderer;
    public double renderPosX;
    public double renderPosY;
    public double renderPosZ;
    public TextureManager renderEngine;
    private boolean renderingGlow = false;
    public World worldObj;
    private boolean suppressNameTags = false;
    public Entity livingPlayer;
    public Entity pointedEntity;
    public float playerViewY;
    public float playerViewX;
    public GameSettings options;
    public double viewerPosX;
    public double viewerPosY;
    public double viewerPosZ;
    private boolean renderOutlines = false;
    private boolean renderShadow = true;
    private boolean debugBoundingBox = false;
    public Render renderRender = null;

    public RenderManager(TextureManager renderEngineIn, RenderItem itemRendererIn) {
        this.renderEngine = renderEngineIn;
        this.entityRenderMap.put(EntityCaveSpider.class, new RenderCaveSpider(this));
        this.entityRenderMap.put(EntitySpider.class, new RenderSpider(this));
        this.entityRenderMap.put(EntityPig.class, new RenderPig(this, new ModelPig(), 0.7F));
        this.entityRenderMap.put(EntitySheep.class, new RenderSheep(this, new ModelSheep2(), 0.7F));
        this.entityRenderMap.put(EntityCow.class, new RenderCow(this, new ModelCow(), 0.7F));
        this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(this, new ModelCow(), 0.7F));
        this.entityRenderMap.put(EntityWolf.class, new RenderWolf(this, new ModelWolf(), 0.5F));
        this.entityRenderMap.put(EntityChicken.class, new RenderChicken(this, new ModelChicken(), 0.3F));
        this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(this, new ModelOcelot(), 0.4F));
        this.entityRenderMap.put(EntityRabbit.class, new RenderRabbit(this, new ModelRabbit(), 0.3F));
        this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish(this));
        this.entityRenderMap.put(EntityEndermite.class, new RenderEndermite(this));
        this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper(this));
        this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman(this));
        this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan(this));
        this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton(this));
        this.entityRenderMap.put(EntityWitch.class, new RenderWitch(this));
        this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze(this));
        this.entityRenderMap.put(EntityPigZombie.class, new RenderPigZombie(this));
        this.entityRenderMap.put(EntityZombie.class, new RenderZombie(this));
        this.entityRenderMap.put(EntitySlime.class, new RenderSlime(this, new ModelSlime(16), 0.25F));
        this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube(this));
        this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(this, new ModelZombie(), 0.5F, 6.0F));
        this.entityRenderMap.put(EntityGhast.class, new RenderGhast(this));
        this.entityRenderMap.put(EntitySquid.class, new RenderSquid(this, new ModelSquid(), 0.7F));
        this.entityRenderMap.put(EntityVillager.class, new RenderVillager(this));
        this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem(this));
        this.entityRenderMap.put(EntityBat.class, new RenderBat(this));
        this.entityRenderMap.put(EntityGuardian.class, new RenderGuardian(this));
        this.entityRenderMap.put(EntityDragon.class, new RenderDragon(this));
        this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal(this));
        this.entityRenderMap.put(EntityWither.class, new RenderWither(this));
        this.entityRenderMap.put(Entity.class, new RenderEntity(this));
        this.entityRenderMap.put(EntityPainting.class, new RenderPainting(this));
        this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame(this, itemRendererIn));
        this.entityRenderMap.put(EntityLeashKnot.class, new RenderLeashKnot(this));
        this.entityRenderMap.put(EntityArrow.class, new RenderArrow(this));
        this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(this, Items.snowball, itemRendererIn));
        this.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(this, Items.ender_pearl, itemRendererIn));
        this.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(this, Items.ender_eye, itemRendererIn));
        this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(this, Items.egg, itemRendererIn));
        this.entityRenderMap.put(EntityPotion.class, new RenderPotion(this, itemRendererIn));
        this.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(this, Items.experience_bottle, itemRendererIn));
        this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(this, Items.fireworks, itemRendererIn));
        this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(this, 2.0F));
        this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(this, 0.5F));
        this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull(this));
        this.entityRenderMap.put(EntityItem.class, new RenderEntityItem(this, itemRendererIn));
        this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb(this));
        this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed(this));
        this.entityRenderMap.put(EntityFallingBlock.class, new RenderFallingBlock(this));
        this.entityRenderMap.put(EntityArmorStand.class, new ArmorStandRenderer(this));
        this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart(this));
        this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(this));
        this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart(this));
        this.entityRenderMap.put(EntityBoat.class, new RenderBoat(this));
        this.entityRenderMap.put(EntityFishHook.class, new RenderFish(this));
        this.entityRenderMap.put(EntityHorse.class, new RenderHorse(this, new ModelHorse(), 0.75F));
        this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt(this));
        this.playerRenderer = new RenderPlayer(this);
        this.skinMap.put("default", this.playerRenderer);
        this.skinMap.put("slim", new RenderPlayer(this, true));
        PlayerItemsLayer.register(this.skinMap);
        if (Reflector.RenderingRegistry_loadEntityRenderers.exists()) {
            Reflector.call(Reflector.RenderingRegistry_loadEntityRenderers, this, this.entityRenderMap);
        }
    }

    public void setRenderPosition(double renderPosXIn, double renderPosYIn, double renderPosZIn) {
        this.renderPosX = renderPosXIn;
        this.renderPosY = renderPosYIn;
        this.renderPosZ = renderPosZIn;
    }

    public <T extends Entity> Render<T> getEntityClassRenderObject(Class<? extends Entity> p_78715_1_) {
        Render render = this.entityRenderMap.get(p_78715_1_);
        if (render == null && p_78715_1_ != Entity.class) {
            render = this.getEntityClassRenderObject((Class<? extends Entity>)p_78715_1_.getSuperclass());
            this.entityRenderMap.put(p_78715_1_, render);
        }

        return render;
    }

    public <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn) {
        if (entityIn instanceof AbstractClientPlayer) {
            String skinType = ((AbstractClientPlayer)entityIn).getSkinType();
            RenderPlayer renderPlayer = this.skinMap.get(skinType);
            return (Render<T>)(renderPlayer != null ? renderPlayer : this.playerRenderer);
        }
        return this.getEntityClassRenderObject((Class<? extends Entity>)entityIn.getClass());
    }

    public void cacheActiveRenderInfo(World worldIn, FontRenderer textRendererIn, Entity livingPlayerIn, Entity pointedEntityIn, GameSettings optionsIn, float partialTicks) {
        this.worldObj = worldIn;
        this.options = optionsIn;
        this.livingPlayer = livingPlayerIn;
        this.pointedEntity = pointedEntityIn;
        this.textRenderer = textRendererIn;
        if (livingPlayerIn instanceof EntityLivingBase && ((EntityLivingBase)livingPlayerIn).isPlayerSleeping()) {
            IBlockState blockState = worldIn.getBlockState(new BlockPos(livingPlayerIn));
            Block block = blockState.getBlock();
            if (Reflector.callBoolean(block, Reflector.ForgeBlock_isBed, blockState, worldIn, new BlockPos(livingPlayerIn), (EntityLivingBase)livingPlayerIn)) {
                EnumFacing facing = (EnumFacing)Reflector.call(block, Reflector.ForgeBlock_getBedDirection, blockState, worldIn, new BlockPos(livingPlayerIn));
                int horizontalIndex = facing.getHorizontalIndex();
                this.playerViewY = horizontalIndex * 90 + 180;
                this.playerViewX = 0.0F;
            } else if (block == Blocks.bed) {
                int horizontalIndex2 = blockState.getValue(BlockBed.FACING).getHorizontalIndex();
                this.playerViewY = horizontalIndex2 * 90 + 180;
                this.playerViewX = 0.0F;
            }
        } else if (GetPitchHelper.flag) {
            this.playerViewY = GetPitchHelper.cameraYaw + 180.0F;
            this.playerViewX = GetPitchHelper.cameraPitch;
        } else {
            this.playerViewY = PlayerHelper13.computeFloat2(livingPlayerIn, livingPlayerIn.prevRotationYaw + (livingPlayerIn.rotationYaw - livingPlayerIn.prevRotationYaw) * partialTicks);
            this.playerViewX = PlayerHelper13.computeFloat(livingPlayerIn, livingPlayerIn.prevRotationPitch + (livingPlayerIn.rotationPitch - livingPlayerIn.prevRotationPitch) * partialTicks);
        }

        if (optionsIn.thirdPersonView == 2) {
            this.playerViewY += 180.0F;
        }

        this.viewerPosX = livingPlayerIn.lastTickPosX + (livingPlayerIn.posX - livingPlayerIn.lastTickPosX) * partialTicks;
        this.viewerPosY = livingPlayerIn.lastTickPosY + (livingPlayerIn.posY - livingPlayerIn.lastTickPosY) * partialTicks;
        this.viewerPosZ = livingPlayerIn.lastTickPosZ + (livingPlayerIn.posZ - livingPlayerIn.lastTickPosZ) * partialTicks;
    }

    public void setPlayerViewY(float playerViewYIn) {
        this.playerViewY = playerViewYIn;
    }

    public boolean isRenderShadow() {
        return this.renderShadow;
    }

    public void setRenderShadow(boolean renderShadowIn) {
        this.renderShadow = renderShadowIn;
    }

    public void setDebugBoundingBox(boolean debugBoundingBoxIn) {
        this.debugBoundingBox = debugBoundingBoxIn;
    }

    public boolean isDebugBoundingBox() {
        return this.debugBoundingBox;
    }

    public boolean renderEntitySimple(Entity entityIn, float partialTicks) {
        return this.renderEntityStatic(entityIn, partialTicks, false);
    }

    public boolean shouldRender(Entity entityIn, ICamera camera, double camX, double camY, double camZ) {
        Render entityRenderObject = this.getEntityRenderObject(entityIn);
        return entityRenderObject != null && entityRenderObject.shouldRender(entityIn, camera, camX, camY, camZ);
    }

    public boolean renderEntityStatic(Entity entity, float partialTicks, boolean p_147936_3_) {
        if (entity.ticksExisted == 0) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
        }

        double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        float f = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        int brightnessForRender = entity.getBrightnessForRender(partialTicks);
        if (entity.isBurning()) {
            brightnessForRender = 15728880;
        }

        int n = brightnessForRender % 65536;
        int n2 = brightnessForRender / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        return this.doRenderEntity(entity, d - this.renderPosX, d2 - this.renderPosY, d3 - this.renderPosZ, f, partialTicks, p_147936_3_);
    }

    public void renderWitherSkull(Entity entityIn, float partialTicks) {
        double d = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
        double d2 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
        double d3 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
        Render entityRenderObject = this.getEntityRenderObject(entityIn);
        if (entityRenderObject != null && this.renderEngine != null) {
            int brightnessForRender = entityIn.getBrightnessForRender(partialTicks);
            int n = brightnessForRender % 65536;
            int n2 = brightnessForRender / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            entityRenderObject.renderName(entityIn, d - this.renderPosX, d2 - this.renderPosY, d3 - this.renderPosZ);
        }
    }

    public boolean renderEntityWithPosYaw(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
        return this.doRenderEntity(entityIn, x, y, z, entityYaw, partialTicks, false);
    }

    public boolean doRenderEntity(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean p_147939_10_) {
        long l = 0L;
        if (PrintCopyPasteReportHelper.isEnabled()) {
            l = System.nanoTime();
        }

        Render render = null;
        GetEntityEvent getEntityEvent = new GetEntityEvent(entity, EventState.PRE);
        getEntityEvent.process();
        if (getEntityEvent.cancel2()) {
            return false;
        }
        float factor = getEntityEvent.getFactor();
        boolean flag = factor < 1.0F;

        try {
            render = this.getEntityRenderObject(entity);
            if (render != null && this.renderEngine != null) {
                try {
                    if (render instanceof RendererLivingEntity) {
                        ((RendererLivingEntity)render).setRenderOutlines(this.renderOutlines);
                    }

                    if (CustomEntityModels.isActive()) {
                        this.renderRender = render;
                    }

                    if (flag) {
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, factor);
                    }

                    render.doRender(entity, x, y, z, entityYaw, partialTicks);
                    if (flag) {
                        GlStateManager.disableBlend();
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    }
                } catch (Throwable e) {
                    throw new ReportedException(CrashReport.makeCrashReport(e, "Rendering entity in world"));
                }

                try {
                    if (!this.renderOutlines) {
                        render.doRenderShadowAndFire(entity, x, y, z, entityYaw, partialTicks);
                    }
                } catch (Throwable e2) {
                    throw new ReportedException(CrashReport.makeCrashReport(e2, "Post-rendering entity in world"));
                }

                if (this.debugBoundingBox && !entity.isInvisible() && !p_147939_10_) {
                    try {
                        this.renderDebugBoundingBox(entity, x, y, z, entityYaw, partialTicks);
                    } catch (Throwable e3) {
                        throw new ReportedException(CrashReport.makeCrashReport(e3, "Rendering entity hitbox in world"));
                    }
                }
            } else if (this.renderEngine != null) {
                return false;
            }

            GetEntityEvent getEntityEvent2 = new GetEntityEvent(entity, EventState.POST);
            getEntityEvent2.process();
            if (l > 0L && PrintCopyPasteReportHelper.isEnabled()) {
                long l2 = System.nanoTime() - l;
                String simpleName = entity.getClass().getSimpleName();
                if (entity instanceof EntityPlayer) {
                    PrintCopyPasteReportHelper.trackEntityProcessing("PlayerRender", 1, l2);
                } else {
                    PrintCopyPasteReportHelper.trackEntityProcessing("EntityRender_" + simpleName, 1, l2);
                }
            }

            return true;
        } catch (Throwable e4) {
            CrashReport crashReport = CrashReport.makeCrashReport(e4, "Rendering entity in world");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being rendered");
            entity.addEntityCrashInfo(crashReportCategory);
            CrashReportCategory crashReportCategory2 = crashReport.makeCategory("Renderer details");
            crashReportCategory2.addCrashSection("Assigned renderer", render);
            crashReportCategory2.addCrashSection("Location", CrashReportCategory.getCoordinateInfo(x, y, z));
            crashReportCategory2.addCrashSection("Rotation", entityYaw);
            crashReportCategory2.addCrashSection("Delta", partialTicks);
            throw new ReportedException(crashReport);
        }
    }

    private void renderDebugBoundingBox(Entity entityIn, double p_85094_2_, double p_85094_4_, double p_85094_6_, float p_85094_8_, float p_85094_9_) {
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        float f = entityIn.width / 2.0F;
        AxisAlignedBB entityBoundingBox = entityIn.getEntityBoundingBox();
        AxisAlignedBB box = new AxisAlignedBB(
            entityBoundingBox.minX - entityIn.posX + p_85094_2_,
            entityBoundingBox.minY - entityIn.posY + p_85094_4_,
            entityBoundingBox.minZ - entityIn.posZ + p_85094_6_,
            entityBoundingBox.maxX - entityIn.posX + p_85094_2_,
            entityBoundingBox.maxY - entityIn.posY + p_85094_4_,
            entityBoundingBox.maxZ - entityIn.posZ + p_85094_6_
        );
        RenderGlobal.func_181563_a(box, 255, 255, 255, 255);
        if (entityIn instanceof EntityLivingBase) {
            RenderGlobal.func_181563_a(
                new AxisAlignedBB(
                    p_85094_2_ - f, p_85094_4_ + entityIn.getEyeHeight() - 0.01F, p_85094_6_ - f, p_85094_2_ + f, p_85094_4_ + entityIn.getEyeHeight() + 0.01F, p_85094_6_ + f
                ),
                255,
                0,
                0,
                255
            );
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        Vec3 look = entityIn.getLook(p_85094_9_);
        worldRenderer.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        worldRenderer.func_181662_b(p_85094_2_, p_85094_4_ + entityIn.getEyeHeight(), p_85094_6_).func_181669_b(0, 0, 255, 255).func_181675_d();
        worldRenderer.func_181662_b(p_85094_2_ + look.xCoord * 2.0, p_85094_4_ + entityIn.getEyeHeight() + look.yCoord * 2.0, p_85094_6_ + look.zCoord * 2.0)
            .func_181669_b(0, 0, 255, 255)
            .func_181675_d();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    public void set(World worldIn) {
        this.worldObj = worldIn;
    }

    public double getDistanceToCamera(double p_78714_1_, double p_78714_3_, double p_78714_5_) {
        double d = p_78714_1_ - this.viewerPosX;
        double d2 = p_78714_3_ - this.viewerPosY;
        double d3 = p_78714_5_ - this.viewerPosZ;
        return d * d + d2 * d2 + d3 * d3;
    }

    public FontRenderer getFontRenderer() {
        return this.textRenderer;
    }

    public void setRenderOutlines(boolean renderOutlinesIn) {
        this.renderOutlines = renderOutlinesIn;
    }

    public Map<Class, Render> getEntityRenderMap() {
        return this.entityRenderMap;
    }

    public void setEntityRenderMap(Map p_setEntityRenderMap_1_) {
        this.entityRenderMap = p_setEntityRenderMap_1_;
    }

    public Map<String, RenderPlayer> getSkinMap() {
        return Collections.unmodifiableMap(this.skinMap);
    }

    public void setSuppressNameTags(boolean suppressNameTags) {
        this.suppressNameTags = suppressNameTags;
    }

    public boolean isSuppressNameTags() {
        return this.suppressNameTags;
    }

    public void renderEntityStaticNoShadow(Entity entity, float partialTicks, boolean p_147936_3_) {
        if (entity.ticksExisted == 0) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
        }

        double d = PlayerHelper5.computeDoubleType(entity.lastTickPosX, entity.posX, partialTicks);
        double d2 = PlayerHelper5.computeDoubleType(entity.lastTickPosY, entity.posY, partialTicks);
        double d3 = PlayerHelper5.computeDoubleType(entity.lastTickPosZ, entity.posZ, partialTicks);
        float f = PlayerHelper5.computeDoubleType(entity.prevRotationYaw, entity.rotationYaw, partialTicks).floatValue();
        int brightnessForRender = entity.getBrightnessForRender(partialTicks);
        int n = brightnessForRender % 65536;
        int n2 = brightnessForRender / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.suppressNameTags = true;
        this.doRenderEntityNoShadow(entity, d - this.renderPosX, d2 - this.renderPosY, d3 - this.renderPosZ, f, partialTicks, p_147936_3_);
        this.suppressNameTags = false;
    }

    public boolean doRenderEntityNoShadow(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean hideDebugBox) {
        Render render = null;

        try {
            render = this.getEntityRenderObject(entity);
            if (render != null && this.renderEngine != null) {
                try {
                    if (render instanceof RendererLivingEntity) {
                        ((RendererLivingEntity)render).setRenderOutlines(this.renderOutlines);
                    }

                    if (CustomEntityModels.isActive()) {
                        this.renderRender = render;
                    }

                    render.doRender(entity, x, y, z, entityYaw, partialTicks);
                } catch (Throwable e) {
                    throw new ReportedException(CrashReport.makeCrashReport(e, "Rendering entity in world"));
                }

                if (this.debugBoundingBox && !entity.isInvisible() && !hideDebugBox) {
                    try {
                        this.renderDebugBoundingBox(entity, x, y, z, entityYaw, partialTicks);
                    } catch (Throwable e2) {
                        throw new ReportedException(CrashReport.makeCrashReport(e2, "Rendering entity hitbox in world"));
                    }
                }
            } else if (this.renderEngine != null) {
                return false;
            }

            return true;
        } catch (Throwable e3) {
            CrashReport crashReport = CrashReport.makeCrashReport(e3, "Rendering entity in world");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being rendered");
            entity.addEntityCrashInfo(crashReportCategory);
            CrashReportCategory crashReportCategory2 = crashReport.makeCategory("Renderer details");
            crashReportCategory2.addCrashSection("Assigned renderer", render);
            crashReportCategory2.addCrashSection("Location", CrashReportCategory.getCoordinateInfo(x, y, z));
            crashReportCategory2.addCrashSection("Rotation", entityYaw);
            crashReportCategory2.addCrashSection("Delta", partialTicks);
            throw new ReportedException(crashReport);
        }
    }
}
