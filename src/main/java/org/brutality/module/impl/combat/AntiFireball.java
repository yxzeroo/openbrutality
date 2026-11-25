package org.brutality.module.impl.combat;

import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;

public class AntiFireball extends Module {
    private final NumberSetting fovSetting = new NumberSetting("FOV", this, 360.0, 30.0, 360.0, 4.0);
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 8.0, 3.0, 15.0, 0.5);
    private final NumberSetting targetCPSSetting = new NumberSetting("Target CPS", this, 12.0, 1.0, 20.0, 0.5);
    private final NumberSetting rotationSpeedSetting = new NumberSetting("Rotation Speed", this, 15.0, 1.0, 30.0, 1.0);
    private final BooleanSetting onGroundSetting = new BooleanSetting("On Ground", this, false);
    private final BooleanSetting sneakWhileActiveSetting = new BooleanSetting("Sneak While Active", this, false);
    private EntityFireball fireball;
    private long timestamp;
    private boolean x;
    public static AntiFireball instance;

    @Generated
    public static AntiFireball getInstance() {
        return instance;
    }

    private float computeFloat(float f, float f2, float f3) {
        float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
        if (f4 > f3) {
            f4 = f3;
        } else if (f4 < -f3) {
            f4 = -f3;
        }

        return f + f4;
    }

    public AntiFireball() {
        super("AntiFireball", 0, Category.COMBAT);
        this.handleSettingArray(
            new Setting[]{
                this.fovSetting, this.rangeSetting, this.targetCPSSetting, this.rotationSpeedSetting, this.onGroundSetting, this.sneakWhileActiveSetting
            }
        );
        instance = this;
    }

    private float[] computeFloatArray(EntityFireball entity) {
        if (entity != null && mc.thePlayer != null) {
            double d = entity.posX - mc.thePlayer.posX;
            double d2 = entity.posY + entity.height * 0.75 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
            double d3 = entity.posZ - mc.thePlayer.posZ;
            double dist = Math.sqrt(d * d + d3 * d3);
            float f = (float)(Math.toDegrees(Math.atan2(d3, d)) - 90.0);
            float f2 = (float)(-Math.toDegrees(Math.atan2(d2, dist)));
            return new float[]{f, f2};
        }
        return null;
    }

    public boolean isAttacking() {
        return this.fireball != null && this.checkState() && mc.currentScreen == null;
    }

    private boolean checkState() {
        return mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.isDead ? false : !this.onGroundSetting.isEnabled() || mc.thePlayer.onGround;
    }

    private EntityFireball computeEntityFireball() {
        EntityFireball entity = null;
        double MAX_VALUE = Double.MAX_VALUE;
        double d = this.rangeSetting.computeDouble() * this.rangeSetting.computeDouble();

        for (Entity entity2 : mc.theWorld.loadedEntityList) {
            if (entity2 instanceof EntityFireball) {
                double dist = mc.thePlayer.getDistanceSqToEntity(entity2);
                if (!(dist > d)
                    && (this.fovSetting.computeDouble() == 360.0 || GetCurrentScreenHelper.matchesFloat((float)this.fovSetting.computeDouble(), entity2))
                    && dist < MAX_VALUE) {
                    MAX_VALUE = dist;
                    entity = (EntityFireball)entity2;
                }
            }
        }

        return entity;
    }

    private void process3() {
        if (this.checkState() && mc.currentScreen == null) {
            this.fireball = this.computeEntityFireball();
            this.handleFlag(this.fireball != null && this.sneakWhileActiveSetting.isEnabled());
            if (this.fireball == null) {
                this.timestamp = 0L;
            } else {
                MovingObjectPosition objectMouseOver = mc.objectMouseOver;
                if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectType.ENTITY && objectMouseOver.entityHit == this.fireball) {
                    long now = System.currentTimeMillis();
                    if (this.timestamp == 0L) {
                        this.timestamp = now;
                    }

                    while (this.timestamp <= now) {
                        mc.thePlayer.swingItem();
                        mc.playerController.attackEntity(mc.thePlayer, this.fireball);
                        this.timestamp = this.timestamp + this.computeLong();
                    }
                } else {
                    this.timestamp = 0L;
                }
            }
        } else {
            this.fireball = null;
            this.handleFlag(false);
        }
    }

    private void onEventPreMotion(EventPreMotion event) {
        if (this.checkState() && this.fireball != null) {
            float[] floats = this.computeFloatArray(this.fireball);
            if (floats != null) {
                float f = (float)this.rotationSpeedSetting.computeDouble();
                float f2 = this.computeFloat(event.getYaw(), floats[0], f);
                float f3 = this.computeFloat(event.getPitch(), floats[1], f);
                event.setYaw(f2);
                event.setPitch(MathHelper.clamp_float(f3, -90.0F, 90.0F));
            }
        }
    }

    private void handleFlag(boolean flag) {
        if (mc.gameSettings != null) {
            if (flag) {
                mc.gameSettings.keyBindSneak.pressed = true;
                this.x = true;
            } else if (this.x) {
                mc.gameSettings.keyBindSneak.pressed = false;
                this.x = false;
            }
        }
    }

    @Override
    public void onDisable() {
        this.process();
    }

    private long computeLong() {
        int n = Math.max(1, (int)Math.round(this.targetCPSSetting.computeDouble()));
        int n2 = 1000 / n;
        return Math.max(33, n2);
    }

    public EntityFireball getFireball2() {
        return this.fireball;
    }

    private void process() {
        this.fireball = null;
        this.timestamp = 0L;
        this.handleFlag(false);
    }

    @Generated
    public EntityFireball getFireball() {
        return this.fireball;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate event2) {
                if (event2.getState() == EventState.PRE) {
                    this.process3();
                }
            } else if (event instanceof EventPreMotion event3 && event3.getState() == EventState.PRE) {
                this.onEventPreMotion(event3);
            }
        }
    }
}
