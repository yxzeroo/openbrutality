package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import org.brutality.module.impl.visual.FPSBooster;

public class EntityParticleEmitter extends EntityFX {
    private Entity attachedEntity;
    private int age;
    private int lifetime;
    private EnumParticleTypes particleTypes;

    public EntityParticleEmitter(World worldIn, Entity p_i46279_2_, EnumParticleTypes particleTypesIn) {
        super(worldIn, p_i46279_2_.posX, p_i46279_2_.getEntityBoundingBox().minY + p_i46279_2_.height / 2.0F, p_i46279_2_.posZ, p_i46279_2_.motionX, p_i46279_2_.motionY, p_i46279_2_.motionZ);
        this.attachedEntity = p_i46279_2_;
        this.lifetime = 3;
        this.particleTypes = particleTypesIn;
        this.onUpdate();
    }

    @Override
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
    }

    @Override
    public void onUpdate() {
        if (this.attachedEntity != null && !FPSBooster.matchesDouble(this.attachedEntity.posX, this.attachedEntity.posY, this.attachedEntity.posZ)) {
            for (int i = 0; i < 16; i++) {
                double d = this.rand.nextFloat() * 2.0F - 1.0F;
                double d2 = this.rand.nextFloat() * 2.0F - 1.0F;
                double d3 = this.rand.nextFloat() * 2.0F - 1.0F;
                if (d * d + d2 * d2 + d3 * d3 <= 1.0) {
                    double d4 = this.attachedEntity.posX + d * this.attachedEntity.width / 4.0;
                    double d5 = this.attachedEntity.getEntityBoundingBox().minY
                        + this.attachedEntity.height / 2.0F
                        + d2 * this.attachedEntity.height / 4.0;
                    double d6 = this.attachedEntity.posZ + d3 * this.attachedEntity.width / 4.0;
                    this.worldObj.spawnParticle(this.particleTypes, false, d4, d5, d6, d, d2 + 0.2, d3);
                }
            }

            this.age++;
            if (this.age >= this.lifetime) {
                this.setDead();
            }
        } else {
            this.setDead();
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
