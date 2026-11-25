package org.brutality.module.impl.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.GetEntityEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class FPSBooster extends Module {
    private static FPSBooster instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    public BooleanSetting pitSpawnSetting = new BooleanSetting("Pit Spawn", this, true);
    public BooleanSetting showKOSSetting = new BooleanSetting("Show KOS", this, false);
    public BooleanSetting showFriendsSetting = new BooleanSetting("Show Friends", this, false);
    public BooleanSetting hideFarAwaySetting = new BooleanSetting("Hide Far Away", this, false);
    public NumberSetting distanceSetting = new NumberSetting("Distance", this, 50.0, 10.0, 200.0, 1.0);
    public BooleanSetting hideItemsSetting = new BooleanSetting("Hide Items", this, true);
    public NumberSetting itemDistanceSetting = new NumberSetting("Item Distance", this, 20.0, 5.0, 50.0, 1.0);
    public NumberSetting itemAgeSetting = new NumberSetting("Item Age", this, 100.0, 50.0, 300.0, 1.0);
    public BooleanSetting hideXPSetting = new BooleanSetting("Hide XP", this, true);
    public NumberSetting xpDistanceSetting = new NumberSetting("XP Distance", this, 15.0, 5.0, 30.0, 1.0);
    public BooleanSetting hideArrowsSetting = new BooleanSetting("Hide Arrows", this, true);
    public NumberSetting arrowTimeSetting = new NumberSetting("Arrow Time", this, 40.0, 20.0, 100.0, 1.0);
    public BooleanSetting hidePlayerArrowsSetting = new BooleanSetting("Hide Player Arrows", this, false);
    public BooleanSetting hideAnimalsSetting = new BooleanSetting("Hide Animals", this, true);
    public BooleanSetting hideMobsSetting = new BooleanSetting("Hide Mobs", this, true);
    public NumberSetting mobDistanceSetting = new NumberSetting("Mob Distance", this, 30.0, 10.0, 60.0, 1.0);
    public BooleanSetting hideAFKSetting = new BooleanSetting("Hide AFK", this, true);
    public NumberSetting afkDistanceSetting = new NumberSetting("AFK Distance", this, 25.0, 10.0, 50.0, 1.0);
    public NumberSetting afkThresholdSetting = new NumberSetting("AFK Threshold", this, 0.01, 0.001, 0.1, 3.0);
    public BooleanSetting limitParticlesSetting = new BooleanSetting("Limit Particles", this, true);
    public NumberSetting particleLimitSetting = new NumberSetting("Particle Limit", this, 500.0, 100.0, 1000.0, 1.0);
    public BooleanSetting cullOffscreenParticlesSetting = new BooleanSetting("Cull Offscreen Particles", this, true);
    public BooleanSetting limitParticleDistanceSetting = new BooleanSetting("Limit Particle Distance", this, true);
    public NumberSetting particleDistanceSetting = new NumberSetting("Particle Distance", this, 24.0, 4.0, 48.0, 1.0)
        .computeNumberSetting(() -> this.limitParticleDistanceSetting.isEnabled());
    public BooleanSetting cacheCustomItemsSetting = new BooleanSetting("Cache Custom Items", this, true);
    public BooleanSetting simpleDistantItemsSetting = new BooleanSetting("Simple Distant Items", this, false);
    public NumberSetting customItemDistanceSetting = new NumberSetting("Custom Item Distance", this, 16.0, 4.0, 64.0, 1.0)
        .computeNumberSetting(() -> this.simpleDistantItemsSetting.isEnabled());
    public BooleanSetting hideHologramsSetting = new BooleanSetting("Hide Holograms", this, true);

    private void process() {
        if (this.hideArrowsSetting.isEnabled() && this.mc.theWorld != null) {
            int n = (int)this.arrowTimeSetting.computeDouble();

            for (Entity entity : this.mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityArrow entity2 && entity2.inGround && entity2.ticksInGround > n) {
                    entity2.setDead();
                }

                if (entity instanceof EntityLivingBase && entity != this.mc.thePlayer) {
                    EntityLivingBase living = (EntityLivingBase)entity;
                    if (living.getArrowCountInEntity() > 0) {
                        if (this.hidePlayerArrowsSetting.isEnabled() && entity instanceof EntityPlayer) {
                            living.setArrowCountInEntity(0);
                        } else if (this.hideArrowsSetting.isEnabled() && this.mc.thePlayer.getDistanceToEntity(entity) > 30.0F) {
                            living.setArrowCountInEntity(0);
                        }
                    }
                }
            }
        }
    }

    public static int computeInt() {
        FPSBooster fpsBooster = instance;
        return fpsBooster != null && fpsBooster.checkState44() && fpsBooster.limitParticlesSetting.isEnabled()
            ? Math.max(1, Math.min(2000, (int)fpsBooster.particleLimitSetting.computeDouble()))
            : 2000;
    }

    public static boolean matchesEntity(Entity entity) {
        FPSBooster fpsBooster = instance;
        return fpsBooster != null
            && fpsBooster.checkState44()
            && fpsBooster.mc.thePlayer != null
            && fpsBooster.mc.theWorld != null
            && fpsBooster.matchesEntity(entity, fpsBooster.mc.thePlayer, fpsBooster.mc.theWorld);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetEntityEvent getEntityEvent) {
                Entity entity = getEntityEvent.getEntity();
                if (entity == null || this.mc.thePlayer == null || this.mc.theWorld == null) {
                    return;
                }

                if (this.matchesEntity2(entity, this.mc.thePlayer, this.mc.theWorld)) {
                    getEntityEvent.setCancel(true);
                }
            } else if (event instanceof EventUpdate && this.mc.theWorld != null && this.mc.thePlayer != null) {
                this.process();
            }
        }
    }

    public static boolean matchesEntity2(Entity entity) {
        FPSBooster fpsBooster = instance;
        if (fpsBooster != null
            && fpsBooster.checkState44()
            && fpsBooster.simpleDistantItemsSetting.isEnabled()
            && entity instanceof EntityPlayer
            && entity != fpsBooster.mc.thePlayer
            && fpsBooster.mc.thePlayer != null
            && entity.worldObj == fpsBooster.mc.theWorld) {
            Object renderViewEntity = fpsBooster.mc.getRenderViewEntity();
            if (renderViewEntity == null) {
                renderViewEntity = fpsBooster.mc.thePlayer;
            }

            double d = fpsBooster.customItemDistanceSetting.computeDouble();
            return entity.getDistanceSqToEntity((Entity)renderViewEntity) > d * d;
        }
        return false;
    }

    private boolean matchesEntity2(Entity entity, EntityPlayer player, World world) {
        if (entity == player) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            if (this.pitSpawnSetting.isEnabled() && FreshHelper.matchesWorld3(world, entity)) {
                return this.matchesEntity(entity, player, world);
            }

            if (this.hideFarAwaySetting.isEnabled()) {
                float dist = player.getDistanceToEntity(entity);
                if (dist > this.distanceSetting.computeDouble()) {
                    return true;
                }
            }

            if (this.hideAFKSetting.isEnabled()) {
                EntityPlayer player2 = (EntityPlayer)entity;
                double d = Math.abs(player2.motionX) + Math.abs(player2.motionY) + Math.abs(player2.motionZ);
                if (d < this.afkThresholdSetting.computeDouble() && player.getDistanceToEntity(entity) > this.afkDistanceSetting.computeDouble()) {
                    return true;
                }
            }
        } else {
            if (this.hideHologramsSetting.isEnabled() && entity instanceof EntityArmorStand entity2 && entity2.isInvisible() && entity2.hasCustomName()) {
                return true;
            }

            if (this.hideItemsSetting.isEnabled()
                && entity instanceof EntityItem entity3
                && (entity3.getAge() > this.itemAgeSetting.computeDouble() || player.getDistanceToEntity(entity) > this.itemDistanceSetting.computeDouble())) {
                return true;
            }

            if (this.hideXPSetting.isEnabled() && entity instanceof EntityXPOrb && player.getDistanceToEntity(entity) > this.xpDistanceSetting.computeDouble()) {
                return true;
            }

            if (this.hideArrowsSetting.isEnabled()
                && entity instanceof EntityArrow entity4
                && entity4.inGround
                && entity4.ticksInGround > this.arrowTimeSetting.computeDouble()) {
                return true;
            }

            if (this.hideAnimalsSetting.isEnabled() && entity instanceof EntityAnimal) {
                return true;
            }

            if (this.hideMobsSetting.isEnabled() && entity instanceof EntityMob && player.getDistanceToEntity(entity) > this.mobDistanceSetting.computeDouble()) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesDouble(double d, double d2, double d3) {
        FPSBooster fpsBooster = instance;
        if (fpsBooster != null && fpsBooster.checkState44() && fpsBooster.limitParticleDistanceSetting.isEnabled() && fpsBooster.mc.theWorld != null) {
            Object renderViewEntity = fpsBooster.mc.getRenderViewEntity();
            if (renderViewEntity == null) {
                renderViewEntity = fpsBooster.mc.thePlayer;
            }

            if (renderViewEntity == null) {
                return false;
            }
            double d4 = d - ((Entity)renderViewEntity).posX;
            double d5 = d2 - ((Entity)renderViewEntity).posY;
            double d6 = d3 - ((Entity)renderViewEntity).posZ;
            double d7 = fpsBooster.particleDistanceSetting.computeDouble();
            return d4 * d4 + d5 * d5 + d6 * d6 > d7 * d7;
        }
        return false;
    }

    public static boolean checkState2() {
        return instance != null && instance.checkState44() && instance.cullOffscreenParticlesSetting.isEnabled();
    }

    public FPSBooster() {
        super("FPSBooster", 0, Category.VISUAL);
        instance = this;
        this.showKOSSetting.computeBooleanSetting(() -> this.pitSpawnSetting.isEnabled());
        this.showFriendsSetting.computeBooleanSetting(() -> this.pitSpawnSetting.isEnabled());
        this.handleSettingArray(
            new Setting[]{
                this.pitSpawnSetting,
                this.showKOSSetting,
                this.showFriendsSetting,
                this.hideFarAwaySetting,
                this.distanceSetting,
                this.hideItemsSetting,
                this.itemDistanceSetting,
                this.itemAgeSetting,
                this.hideXPSetting,
                this.xpDistanceSetting,
                this.hideArrowsSetting,
                this.arrowTimeSetting,
                this.hidePlayerArrowsSetting,
                this.hideAnimalsSetting,
                this.hideMobsSetting,
                this.mobDistanceSetting,
                this.hideAFKSetting,
                this.afkDistanceSetting,
                this.afkThresholdSetting,
                this.limitParticlesSetting,
                this.particleLimitSetting,
                this.hideHologramsSetting,
                this.cullOffscreenParticlesSetting,
                this.limitParticleDistanceSetting,
                this.particleDistanceSetting,
                this.cacheCustomItemsSetting,
                this.simpleDistantItemsSetting,
                this.customItemDistanceSetting
            }
        );
    }

    public static boolean checkState() {
        return instance == null || instance.cacheCustomItemsSetting.isEnabled();
    }

    private boolean matchesEntity(Entity entity, EntityPlayer player, World world) {
        if (entity == player
            || !(entity instanceof EntityPlayer)
            || !this.pitSpawnSetting.isEnabled()
            || !FreshHelper.matchesWorld3(world, entity)
            || FreshHelper.matchesWorld3(world, player)) {
            return false;
        }
        return this.showKOSSetting.isEnabled() && WHelper.matchesString(entity.getName())
            ? false
            : !this.showFriendsSetting.isEnabled() || !DefaultHelper.matchesString(entity.getName());
    }
}
