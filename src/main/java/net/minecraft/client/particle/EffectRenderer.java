package net.minecraft.client.particle;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EntityAuraFX.HappyVillagerFactory;
import net.minecraft.client.particle.EntityBreakingFX.SlimeFactory;
import net.minecraft.client.particle.EntityBreakingFX.SnowballFactory;
import net.minecraft.client.particle.EntityCrit2FX.MagicFactory;
import net.minecraft.client.particle.EntityDropParticleFX.LavaFactory;
import net.minecraft.client.particle.EntityDropParticleFX.WaterFactory;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX.EnchantmentTable;
import net.minecraft.client.particle.EntityExplodeFX.Factory;
import net.minecraft.client.particle.EntityFirework.SparkFX;
import net.minecraft.client.particle.EntityHeartFX.AngryVillagerFactory;
import net.minecraft.client.particle.EntitySpellParticleFX.AmbientMobFactory;
import net.minecraft.client.particle.EntitySpellParticleFX.InstantFactory;
import net.minecraft.client.particle.EntitySpellParticleFX.MobFactory;
import net.minecraft.client.particle.EntitySpellParticleFX.WitchFactory;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.reflect.Reflector;
import org.brutality.module.impl.visual.FPSBooster;
import org.brutality.utils.performance.PrintCopyPasteReportHelper;
import org.lwjgl.opengl.GL11;

public class EffectRenderer {
    public static final int MAX_PARTICLES_PER_LAYER = 2000;
    private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
    protected World worldObj;
    public List<EntityFX>[][] fxLayers = new List[4][];
    private List<EntityParticleEmitter> particleEmitters = new ParticleList<>(2000);
    private TextureManager renderer;
    private Random rand = new Random();
    private Map<Integer, IParticleFactory> particleTypes = Maps.newHashMap();

    public EffectRenderer(World worldIn, TextureManager rendererIn) {
        this.worldObj = worldIn;
        this.renderer = rendererIn;

        for (int i = 0; i < 4; i++) {
            this.fxLayers[i] = new List[2];

            for (int j = 0; j < 2; j++) {
                this.fxLayers[i][j] = new ParticleList<>(2000);
            }
        }

        this.registerVanillaParticles();
    }

    private void registerVanillaParticles() {
        this.registerParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), new Factory());
        this.registerParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(), new net.minecraft.client.particle.EntityBubbleFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), new net.minecraft.client.particle.EntitySplashFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_WAKE.getParticleID(), new net.minecraft.client.particle.EntityFishWakeFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), new net.minecraft.client.particle.EntityRainFX.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED.getParticleID(), new net.minecraft.client.particle.EntitySuspendFX.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED_DEPTH.getParticleID(), new net.minecraft.client.particle.EntityAuraFX.Factory());
        this.registerParticle(EnumParticleTypes.CRIT.getParticleID(), new net.minecraft.client.particle.EntityCrit2FX.Factory());
        this.registerParticle(EnumParticleTypes.CRIT_MAGIC.getParticleID(), new MagicFactory());
        this.registerParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), new net.minecraft.client.particle.EntitySmokeFX.Factory());
        this.registerParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), new net.minecraft.client.particle.EntityCritFX.Factory());
        this.registerParticle(EnumParticleTypes.SPELL.getParticleID(), new net.minecraft.client.particle.EntitySpellParticleFX.Factory());
        this.registerParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), new InstantFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), new MobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID(), new AmbientMobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(), new WitchFactory());
        this.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new WaterFactory());
        this.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new LavaFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_ANGRY.getParticleID(), new AngryVillagerFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_HAPPY.getParticleID(), new HappyVillagerFactory());
        this.registerParticle(EnumParticleTypes.TOWN_AURA.getParticleID(), new net.minecraft.client.particle.EntityAuraFX.Factory());
        this.registerParticle(EnumParticleTypes.NOTE.getParticleID(), new net.minecraft.client.particle.EntityNoteFX.Factory());
        this.registerParticle(EnumParticleTypes.PORTAL.getParticleID(), new net.minecraft.client.particle.EntityPortalFX.Factory());
        this.registerParticle(EnumParticleTypes.ENCHANTMENT_TABLE.getParticleID(), new EnchantmentTable());
        this.registerParticle(EnumParticleTypes.FLAME.getParticleID(), new net.minecraft.client.particle.EntityFlameFX.Factory());
        this.registerParticle(EnumParticleTypes.LAVA.getParticleID(), new net.minecraft.client.particle.EntityLavaFX.Factory());
        this.registerParticle(EnumParticleTypes.FOOTSTEP.getParticleID(), new net.minecraft.client.particle.EntityFootStepFX.Factory());
        this.registerParticle(EnumParticleTypes.CLOUD.getParticleID(), new net.minecraft.client.particle.EntityCloudFX.Factory());
        this.registerParticle(EnumParticleTypes.REDSTONE.getParticleID(), new net.minecraft.client.particle.EntityReddustFX.Factory());
        this.registerParticle(EnumParticleTypes.SNOWBALL.getParticleID(), new SnowballFactory());
        this.registerParticle(EnumParticleTypes.SNOW_SHOVEL.getParticleID(), new net.minecraft.client.particle.EntitySnowShovelFX.Factory());
        this.registerParticle(EnumParticleTypes.SLIME.getParticleID(), new SlimeFactory());
        this.registerParticle(EnumParticleTypes.HEART.getParticleID(), new net.minecraft.client.particle.EntityHeartFX.Factory());
        this.registerParticle(EnumParticleTypes.BARRIER.getParticleID(), new net.minecraft.client.particle.Barrier.Factory());
        this.registerParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), new net.minecraft.client.particle.EntityBreakingFX.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), new net.minecraft.client.particle.EntityDiggingFX.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), new net.minecraft.client.particle.EntityBlockDustFX.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_HUGE.getParticleID(), new net.minecraft.client.particle.EntityHugeExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), new net.minecraft.client.particle.EntityLargeExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), new net.minecraft.client.particle.EntityFirework.Factory());
        this.registerParticle(EnumParticleTypes.MOB_APPEARANCE.getParticleID(), new net.minecraft.client.particle.MobAppearance.Factory());
    }

    public void registerParticle(int id, IParticleFactory particleFactory) {
        this.particleTypes.put(id, particleFactory);
    }

    public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes) {
        if (entityIn != null && !FPSBooster.matchesDouble(entityIn.posX, entityIn.posY, entityIn.posZ)) {
            this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
        }
    }

    public EntityFX spawnEffectParticle(int particleId, double p_178927_2_, double p_178927_4_, double p_178927_6_, double p_178927_8_, double p_178927_10_, double p_178927_12_, int... p_178927_14_) {
        if (FPSBooster.matchesDouble(p_178927_2_, p_178927_4_, p_178927_6_)) {
            return null;
        }
        IParticleFactory iParticleFactory = this.particleTypes.get(particleId);
        if (iParticleFactory != null) {
            EntityFX entityFX = iParticleFactory.getEntityFX(particleId, this.worldObj, p_178927_2_, p_178927_4_, p_178927_6_, p_178927_8_, p_178927_10_, p_178927_12_, p_178927_14_);
            if (entityFX != null) {
                this.addEffect(entityFX);
                return entityFX;
            }
        }

        return null;
    }

    public void addEffect(EntityFX effect) {
        if (effect != null) {
            if (FPSBooster.matchesDouble(effect.posX, effect.posY, effect.posZ)) {
                return;
            }

            if (!(effect instanceof SparkFX) || Config.isFireworkParticles()) {
                int fxLayer = effect.getFXLayer();
                int n = effect.getAlpha() != 1.0F ? 0 : 1;
                if (fxLayer < 0 || fxLayer >= this.fxLayers.length) {
                    return;
                }

                int n2 = FPSBooster.computeInt();
                if (this.fxLayers[fxLayer][n].size() >= n2) {
                    trimOldest(this.fxLayers[fxLayer][n], this.fxLayers[fxLayer][n].size() - n2 + 1);
                }

                this.fxLayers[fxLayer][n].add(effect);
            }
        }
    }

    public void updateEffects() {
        int n = FPSBooster.computeInt();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                var entities = this.fxLayers[i][j];
                if (entities.size() > n) {
                    trimOldest(entities, entities.size() - n);
                }
            }

            this.updateEffectLayer(i);
        }

        for (int k = 0; k < this.particleEmitters.size(); k++) {
            this.particleEmitters.get(k).onUpdate();
        }

        this.particleEmitters.removeIf(entity -> entity.isDead);
    }

    private static void trimOldest(List<?> particles, int count) {
        if (particles instanceof ParticleList) {
            ((ParticleList)particles).removeRange(0, count);
        } else {
            particles.subList(0, count).clear();
        }
    }

    private void updateEffectLayer(int p_178922_1_) {
        for (int i = 0; i < 2; i++) {
            this.updateEffectAlphaLayer(this.fxLayers[p_178922_1_][i]);
        }
    }

    private void updateEffectAlphaLayer(List<EntityFX> entitiesFX) {
        long l = System.nanoTime() + 20000000L;
        int size = entitiesFX.size();

        for (int i = 0; i < entitiesFX.size(); i++) {
            this.tickParticle((EntityFX)entitiesFX.get(i));
            size--;
            if (System.nanoTime() > l) {
                break;
            }
        }

        if (size > 0) {
            int n = Math.min(size, entitiesFX.size());

            for (int j = 0; j < n; j++) {
                ((EntityFX)entitiesFX.get(j)).setDead();
            }

            trimOldest(entitiesFX, n);
        }

        entitiesFX.removeIf(entity -> entity.isDead);
    }

    private void tickParticle(final EntityFX p_178923_1_) {
        try {
            p_178923_1_.onUpdate();
        } catch (Throwable e) {
            CrashReport crashReport = CrashReport.makeCrashReport(e, "Ticking Particle");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Particle being ticked");
            final int fxLayer = p_178923_1_.getFXLayer();
            crashReportCategory.addCrashSectionCallable("Particle", new Callable<String>() {
                public String call() throws Exception {
                    return p_178923_1_.toString();
                }
            });
            crashReportCategory.addCrashSectionCallable("Particle Type", new Callable<String>() {
                public String call() throws Exception {
                    return fxLayer == 0 ? "MISC_TEXTURE" : (fxLayer == 1 ? "TERRAIN_TEXTURE" : (fxLayer == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + fxLayer));
                }
            });
            throw new ReportedException(crashReport);
        }
    }

    public void renderParticles(Entity entityIn, float partialTicks) {
        long l = 0L;
        if (PrintCopyPasteReportHelper.isEnabled()) {
            l = System.nanoTime();
        }

        float rotationX = ActiveRenderInfo.getRotationX();
        float rotationZ = ActiveRenderInfo.getRotationZ();
        float rotationYZ = ActiveRenderInfo.getRotationYZ();
        float rotationXY = ActiveRenderInfo.getRotationXY();
        float rotationXZ = ActiveRenderInfo.getRotationXZ();
        EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
        EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
        EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        double x = entityIn.posX;
        double y = entityIn.posY;
        double z = entityIn.posZ;
        Frustum frustum = null;
        if (FPSBooster.checkState2()) {
            frustum = new Frustum();
            frustum.setPosition(EntityFX.interpPosX, EntityFX.interpPosY, EntityFX.interpPosZ);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                var entities = this.fxLayers[i][j];
                if (!entities.isEmpty()) {
                    switch (j) {
                        case 0:
                            GlStateManager.depthMask(false);
                            break;
                        case 1:
                            GlStateManager.depthMask(true);
                    }

                    switch (i) {
                        case 0:
                        default:
                            this.renderer.bindTexture(particleTextures);
                            break;
                        case 1:
                            this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                    }

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
                    worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181704_d);
                    int size = entities.size();

                    for (int k = 0; k < size; k++) {
                        final EntityFX entity = (EntityFX)entities.get(k);
                        if (!entity.isDead && !FPSBooster.matchesDouble(entity.posX, entity.posY, entity.posZ)) {
                            if (frustum != null) {
                                double d = Math.max(1.0, entity.particleScale * 0.25);
                                if (!frustum.isBoxInFrustum(
                                    entity.posX - d, entity.posY - d, entity.posZ - d, entity.posX + d, entity.posY + d, entity.posZ + d
                                )) {
                                    continue;
                                }
                            }

                            double d2 = entity.posX - x;
                            double d3 = entity.posY - y;
                            double d4 = entity.posZ - z;
                            if (!(d2 * d2 + d3 * d3 + d4 * d4 > 2304.0)) {
                                try {
                                    entity.renderParticle(worldRenderer, entityIn, partialTicks, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
                                } catch (Throwable e) {
                                    CrashReport crashReport = CrashReport.makeCrashReport(e, "Rendering Particle");
                                    CrashReportCategory crashReportCategory = crashReport.makeCategory("Particle being rendered");
                                    crashReportCategory.addCrashSectionCallable("Particle", new Callable<String>() {
                                        public String call() throws Exception {
                                            return entity.toString();
                                        }
                                    });
                                    final int var17Final = i;
                                    crashReportCategory.addCrashSectionCallable(
                                        "Particle Type",
                                        new Callable<String>() {
                                            public String call() throws Exception {
                                                return var17Final == 0
                                                    ? "MISC_TEXTURE"
                                                    : (var17Final == 1 ? "TERRAIN_TEXTURE" : (var17Final == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var17Final));
                                            }
                                        }
                                    );
                                    throw new ReportedException(crashReport);
                                }
                            }
                        }
                    }

                    tessellator.draw();
                }
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        if (l > 0L && PrintCopyPasteReportHelper.isEnabled()) {
            long l2 = System.nanoTime() - l;
            int n = 0;

            for (int idx = 0; idx < 4; idx++) {
                for (int idx2 = 0; idx2 < 2; idx2++) {
                    n += this.fxLayers[idx][idx2].size();
                }
            }

            PrintCopyPasteReportHelper.trackEntityProcessing("Particles", n, l2);
        }
    }

    public int getParticleCount() {
        int n = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                n += this.fxLayers[i][j].size();
            }
        }

        return n;
    }

    public void renderLitParticles(Entity entityIn, float p_78872_2_) {
        float f = (float) (Math.PI / 180.0);
        float cos = MathHelper.cos(entityIn.rotationYaw * (float) (Math.PI / 180.0));
        float sin = MathHelper.sin(entityIn.rotationYaw * (float) (Math.PI / 180.0));
        float f2 = -sin * MathHelper.sin(entityIn.rotationPitch * (float) (Math.PI / 180.0));
        float f3 = cos * MathHelper.sin(entityIn.rotationPitch * (float) (Math.PI / 180.0));
        float cos2 = MathHelper.cos(entityIn.rotationPitch * (float) (Math.PI / 180.0));

        for (int i = 0; i < 2; i++) {
            var entities = this.fxLayers[3][i];
            if (!entities.isEmpty()) {
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldRenderer = tessellator.getWorldRenderer();

                for (int j = 0; j < entities.size(); j++) {
                    EntityFX entity = (EntityFX)entities.get(j);
                    entity.renderParticle(worldRenderer, entityIn, p_78872_2_, cos, cos2, sin, f2, f3);
                }
            }
        }
    }

    public void clearEffects(World worldIn) {
        this.worldObj = worldIn;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                this.fxLayers[i][j].clear();
            }
        }

        this.particleEmitters.clear();
    }

    public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
        boolean flag;
        if (Reflector.ForgeBlock_addDestroyEffects.exists() && Reflector.ForgeBlock_isAir.exists()) {
            Block block = state.getBlock();
            flag = !Reflector.callBoolean(block, Reflector.ForgeBlock_isAir, this.worldObj, pos)
                && !Reflector.callBoolean(block, Reflector.ForgeBlock_addDestroyEffects, this.worldObj, pos, this);
        } else {
            flag = state.getBlock().getMaterial() != Material.air;
        }

        if (flag) {
            IBlockState actualState = state.getBlock().getActualState(state, this.worldObj, pos);
            byte b = 4;

            for (int i = 0; i < b; i++) {
                for (int j = 0; j < b; j++) {
                    for (int k = 0; k < b; k++) {
                        double d = pos.getX() + (i + 0.5) / b;
                        double d2 = pos.getY() + (j + 0.5) / b;
                        double d3 = pos.getZ() + (k + 0.5) / b;
                        this.addEffect(
                            new EntityDiggingFX(
                                    this.worldObj, d, d2, d3, d - pos.getX() - 0.5, d2 - pos.getY() - 0.5, d3 - pos.getZ() - 0.5, actualState
                                )
                                .func_174846_a(pos)
                        );
                    }
                }
            }
        }
    }

    public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
        IBlockState blockState = this.worldObj.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block.getRenderType() != -1) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            float f = 0.1F;
            double d = x
                + this.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - f * 2.0F)
                + f
                + block.getBlockBoundsMinX();
            double d2 = y
                + this.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - f * 2.0F)
                + f
                + block.getBlockBoundsMinY();
            double d3 = z
                + this.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - f * 2.0F)
                + f
                + block.getBlockBoundsMinZ();
            if (side == EnumFacing.DOWN) {
                d2 = y + block.getBlockBoundsMinY() - f;
            }

            if (side == EnumFacing.UP) {
                d2 = y + block.getBlockBoundsMaxY() + f;
            }

            if (side == EnumFacing.NORTH) {
                d3 = z + block.getBlockBoundsMinZ() - f;
            }

            if (side == EnumFacing.SOUTH) {
                d3 = z + block.getBlockBoundsMaxZ() + f;
            }

            if (side == EnumFacing.WEST) {
                d = x + block.getBlockBoundsMinX() - f;
            }

            if (side == EnumFacing.EAST) {
                d = x + block.getBlockBoundsMaxX() + f;
            }

            this.addEffect(
                new EntityDiggingFX(this.worldObj, d, d2, d3, 0.0, 0.0, 0.0, blockState)
                    .func_174846_a(pos)
                    .multiplyVelocity(0.2F)
                    .multipleParticleScaleBy(0.6F)
            );
        }
    }

    public void moveToAlphaLayer(EntityFX effect) {
        this.moveToLayer(effect, 1, 0);
    }

    public void moveToNoAlphaLayer(EntityFX effect) {
        this.moveToLayer(effect, 0, 1);
    }

    private void moveToLayer(EntityFX effect, int p_178924_2_, int p_178924_3_) {
        if (effect != null) {
            int fxLayer = effect.getFXLayer();
            if (fxLayer >= 0 && fxLayer < this.fxLayers.length) {
                if (this.fxLayers[fxLayer][p_178924_2_].remove(effect)) {
                    var entities = this.fxLayers[fxLayer][p_178924_3_];
                    int n = FPSBooster.computeInt();
                    if (entities.size() >= n) {
                        trimOldest(entities, entities.size() - n + 1);
                    }

                    entities.add(effect);
                }
            }
        }
    }

    public String getStatistics() {
        int n = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                n += this.fxLayers[i][j].size();
            }
        }

        return String.valueOf(n);
    }

    public void addBlockHitEffects(BlockPos p_addBlockHitEffects_1_, MovingObjectPosition p_addBlockHitEffects_2_) {
        IBlockState blockState = this.worldObj.getBlockState(p_addBlockHitEffects_1_);
        if (blockState != null) {
            boolean flag = Reflector.callBoolean(blockState.getBlock(), Reflector.ForgeBlock_addHitEffects, this.worldObj, p_addBlockHitEffects_2_, this);
            if (blockState != null && !flag) {
                this.addBlockHitEffects(p_addBlockHitEffects_1_, p_addBlockHitEffects_2_.sideHit);
            }
        }
    }
}
