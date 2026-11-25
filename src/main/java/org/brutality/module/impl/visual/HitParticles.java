package org.brutality.module.impl.visual;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;

public class HitParticles extends Module {
    private static final int count = 4;
    private static final int count2 = 13;
    private static final double amount = 96.0;
    private static final int[] intValueArray = new int[0];
    private static final Random random = new Random();
    private final BooleanSetting meleeHitsSetting = new BooleanSetting("Melee hits", this, true);
    private final BooleanSetting arrowHitsSetting = new BooleanSetting("Arrow hits", this, true);
    private final NumberSetting meleeParticleSetting = new NumberSetting("Melee particle", this, 4.0, 0.0, 23.0, 1.0);
    private final NumberSetting rangedParticleSetting = new NumberSetting("Ranged particle", this, 4.0, 0.0, 23.0, 1.0);
    private final NumberSetting meleeMultiplierSetting = new NumberSetting("Melee multiplier", this, 1.0, 1.0, 8.0, 1.0);
    private final NumberSetting rangedMultiplierSetting = new NumberSetting("Ranged multiplier", this, 1.0, 1.0, 8.0, 1.0);
    private final Map<Integer, Integer> lookupMap = new HashMap<>();
    private static final int[] intValueArray2 = new int[]{20, 37, 32, 30, 9, -1, 25, 1, 26, 21, 34, 14, 27, 10, 15, 23, 24, 33, 11, 31, 3, 13, 5, 17};
    private static final int[] intValueArray3 = new int[]{8, 32, 16, 16, 32, -1, 16, 1, 16, 16, 8, 16, 16, 32, 16, 8, 32, 16, 16, 16, 16, 16, 32, 16};
    private static final float[] floatValueArray = new float[]{
        2.5F, 1.2F, 0.2F, 3.0F, 1.0F, -1.0F, 1.0F, 1.0F, 0.05F, 2.5F, 2.0F, 0.7F, 1.0F, 1.0F, 1.0F, 3.0F, 1.0F, 0.5F, 0.08F, 1.0F, 0.1F, 1.0F, 1.0F, 0.5F
    };
    private static final boolean[] flagArray = new boolean[]{
        true,
        true,
        false,
        true,
        false,
        false,
        true,
        true,
        false,
        true,
        true,
        false,
        false,
        false,
        false,
        true,
        true,
        false,
        true,
        true,
        false,
        false,
        false,
        false
    };
    private static final int[][] intValueArray4 = new int[][]{
        intValueArray,
        {Block.getIdFromBlock(Blocks.netherrack)},
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray,
        intValueArray
    };

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap.clear();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (!(event instanceof RenderEvent4) && GetCurrentScreenHelper.checkState2()) {
                if (event instanceof GetDamageEvent) {
                    this.onGetDamageEvent((GetDamageEvent)event);
                } else if (event instanceof EventUpdate && EventState.onEvent(event)) {
                    this.process();
                }
            } else {
                this.lookupMap.clear();
            }
        }
    }

    public HitParticles() {
        super("Hit Particles", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.meleeHitsSetting,
                this.meleeParticleSetting,
                this.meleeMultiplierSetting,
                this.arrowHitsSetting,
                this.rangedParticleSetting,
                this.rangedMultiplierSetting
            }
        );
    }

    @Override
    public void process2() {
        this.lookupMap.clear();
    }

    private void process() {
        if (this.arrowHitsSetting.isEnabled()) {
            this.process3();
            AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().expand(96.0, 96.0, 96.0);

            for (EntityArrow entity : mc.theWorld.getEntitiesWithinAABB(EntityArrow.class, box)) {
                if (entity.shootingEntity == mc.thePlayer && !entity.isDead && !entity.inGround) {
                    EntityLivingBase living = computeEntityLivingBase(entity);
                    if (living != null && living.hurtTime <= 0) {
                        int entityId = entity.getEntityId();
                        int entityId2 = living.getEntityId();
                        Integer integer = this.lookupMap.get(entityId);
                        if (integer == null || integer != entityId2) {
                            this.lookupMap.put(entityId, entityId2);
                            int n = (int)this.rangedParticleSetting.computeDouble();
                            this.handleInt(n, living, (int)this.rangedMultiplierSetting.computeDouble());
                        }
                    }
                }
            }
        }
    }

    private void onGetDamageEvent(GetDamageEvent getDamageEvent) {
        if (this.meleeHitsSetting.isEnabled()) {
            if (getDamageEvent.getEntity() == mc.thePlayer) {
                if (getDamageEvent.getEntity2() instanceof EntityLivingBase) {
                    EntityLivingBase entity2 = (EntityLivingBase)getDamageEvent.getEntity2();
                    if (entity2.hurtTime <= 5 && entity2.hurtResistantTime <= 0) {
                        int n = (int)this.meleeParticleSetting.computeDouble();
                        this.handleInt(n, entity2, (int)this.meleeMultiplierSetting.computeDouble());
                    }
                }
            }
        }
    }

    private void process3() {
        Iterator it = this.lookupMap.entrySet().iterator();

        while (it.hasNext()) {
            Entry entry = (Entry)it.next();
            if (mc.theWorld.getEntityByID((Integer)entry.getKey()) == null) {
                it.remove();
            }
        }
    }

    private static EntityLivingBase computeEntityLivingBase(EntityArrow entity) {
        World worldObj = entity.worldObj;
        Vec3 vec = new Vec3(entity.posX, entity.posY, entity.posZ);
        Vec3 vec2 = new Vec3(entity.posX + entity.motionX, entity.posY + entity.motionY, entity.posZ + entity.motionZ);
        MovingObjectPosition hit = worldObj.rayTraceBlocks(vec, vec2, false, true, false);
        Vec3 vec3 = hit == null ? vec2 : hit.hitVec;
        EntityLivingBase living = null;
        double d = 0.0;
        AxisAlignedBB box = entity.getEntityBoundingBox().addCoord(entity.motionX, entity.motionY, entity.motionZ).expand(1.0, 1.0, 1.0);

        for (Entity entity2 : worldObj.getEntitiesWithinAABBExcludingEntity(entity, box)) {
            if (entity2 instanceof EntityLivingBase living2 && living2.canBeCollidedWith() && living2 != entity.shootingEntity) {
                AxisAlignedBB box2 = entity2.getEntityBoundingBox().expand(0.3, 0.3, 0.3);
                MovingObjectPosition hit2 = box2.calculateIntercept(vec, vec3);
                if (hit2 != null) {
                    double d2 = vec.squareDistanceTo(hit2.hitVec);
                    if (!(d2 >= d) || d == 0.0) {
                        living = living2;
                        d = d2;
                    }
                }
            }
        }

        return living;
    }

    private static void handleInt(int n, Entity entity, int n2, int n3, float f, boolean flag, int[] ints) {
        for (int i = 0; i < n3 * n2; i++) {
            double d = random.nextFloat() * (f * 2.0F) - f;
            double d2 = random.nextFloat() * (f * 2.0F) - f;
            double d3 = random.nextFloat() * (f * 2.0F) - f;
            if (flag || d * d + d2 * d2 + d3 * d3 <= 1.0) {
                double d4 = entity.posX + d * entity.width / 4.0;
                double d5 = entity.getEntityBoundingBox().minY + entity.height / 2.0F + d2 * entity.height / 4.0;
                double d6 = entity.posZ + d3 * entity.width / 4.0;
                mc.effectRenderer.spawnEffectParticle(n, d4, d5, d6, d, d2, d3, ints);
            }
        }
    }

    private void handleInt(int n, Entity entity, int n2) {
        if (n >= 0 && n < intValueArray2.length) {
            int n3 = intValueArray2[n];
            if (n3 == -1) {
                handleInt(9, entity, n2, intValueArray3[4], floatValueArray[4], flagArray[4], intValueArray4[4]);
                handleInt(10, entity, n2, intValueArray3[13], floatValueArray[13], flagArray[13], intValueArray4[13]);
            } else {
                handleInt(n3, entity, n2, intValueArray3[n], floatValueArray[n], flagArray[n], intValueArray4[n]);
            }
        }
    }
}
