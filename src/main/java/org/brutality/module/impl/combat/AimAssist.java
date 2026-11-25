package org.brutality.module.impl.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.player.KOS;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.WHelper;
import org.lwjgl.input.Mouse;

public class AimAssist extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting horizontalSpeedSetting = new NumberSetting("Horizontal Speed", this, 45.0, 1.0, 100.0, 1.0);
    private final NumberSetting verticalSpeedSetting = new NumberSetting("Vertical Speed", this, 45.0, 1.0, 100.0, 1.0);
    private final BooleanSetting enableVerticalSetting = new BooleanSetting("Enable Vertical", this, true);
    private final NumberSetting distanceSetting = new NumberSetting("Distance", this, 6.0, 3.0, 10.0, 0.1);
    private final NumberSetting maxAngleSetting = new NumberSetting("Max Angle", this, 90.0, 10.0, 180.0, 1.0);
    private final BooleanSetting clickAimSetting = new BooleanSetting("Click Aim", this, true);
    private final BooleanSetting checkBlockBreakSetting = new BooleanSetting("Check Block Break", this, true);
    private final BooleanSetting throughWallsSetting = new BooleanSetting("Through Walls", this, false);
    private final ModeSetting hurttimeSetting = new ModeSetting("Sorting", this, "Distance", Arrays.asList("Distance", "Health", "FOV", "Armor", "Hurttime"));
    private final ModeSetting highSetting = new ModeSetting("Health Mode", this, "Low", Arrays.asList("Low", "High"))
        .computeModeSetting(() -> this.hurttimeSetting.matchesString2("Health"));
    private final ModeSetting distanceSetting2 = new ModeSetting("Hurttime Mode", this, "Health", Arrays.asList("Health", "Distance"))
        .computeModeSetting(() -> this.hurttimeSetting.matchesString2("Hurttime"));
    private final BooleanSetting playersSetting = new BooleanSetting("Players", this, true);
    private final BooleanSetting mobsSetting = new BooleanSetting("Mobs", this, false);
    private final BooleanSetting animalsSetting = new BooleanSetting("Animals", this, false);
    private final BooleanSetting invisibleSetting = new BooleanSetting("Invisible", this, false);
    private final KeybindListSetting targetsSetting = new KeybindListSetting(
        "Targets", this, this.playersSetting, this.mobsSetting, this.animalsSetting, this.invisibleSetting
    );
    private EntityLivingBase entityLivingBase;
    private boolean w = false;

    private boolean matchesEntityLivingBase(EntityLivingBase living) {
        Vec3 vec = new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight(), this.mc.thePlayer.posZ);
        Vec3 vec2 = new Vec3(living.posX, living.posY + living.getEyeHeight(), living.posZ);
        MovingObjectPosition hit = this.mc.theWorld.rayTraceBlocks(vec, vec2);
        return hit == null;
    }

    private float[] computeFloatArray(EntityLivingBase living) {
        if (living == null) {
            return null;
        }
        Vec3 vec = new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight(), this.mc.thePlayer.posZ);
        AxisAlignedBB entityBoundingBox = living.getEntityBoundingBox();
        Vec3 vec2 = this.computeVec3(vec, entityBoundingBox);
        double d = vec2.xCoord - vec.xCoord;
        double d2 = vec2.yCoord - vec.yCoord;
        double d3 = vec2.zCoord - vec.zCoord;
        float f = (float)(Math.atan2(d3, d) * (180.0 / Math.PI)) - 90.0F;
        float f2 = MathHelper.wrapAngleTo180_float(f - this.mc.thePlayer.rotationYaw);
        float f3 = this.mc.thePlayer.rotationYaw + f2;
        double d4 = MathHelper.sqrt_double(d * d + d3 * d3);
        float f4 = (float)(-(Math.atan2(d2, d4) * (180.0 / Math.PI)));
        float f5 = MathHelper.wrapAngleTo180_float(f4 - this.mc.thePlayer.rotationPitch);
        float f6 = this.mc.thePlayer.rotationPitch + f5;
        f6 = MathHelper.clamp_float(f6, -90.0F, 90.0F);
        return new float[]{f3, f6};
    }

    private boolean matchesEntityLivingBase2(EntityLivingBase living) {
        BooleanSetting boolSetting = (BooleanSetting)this.targetsSetting.computeSetting("Players");
        BooleanSetting boolSetting2 = (BooleanSetting)this.targetsSetting.computeSetting("Mobs");
        BooleanSetting boolSetting3 = (BooleanSetting)this.targetsSetting.computeSetting("Animals");
        if (living instanceof EntityPlayer) {
            return boolSetting != null && boolSetting.isEnabled();
        }
        if (living instanceof EntityMob) {
            return boolSetting2 != null && boolSetting2.isEnabled();
        }
        return living instanceof EntityAnimal ? boolSetting3 != null && boolSetting3.isEnabled() : boolSetting2 != null && boolSetting2.isEnabled();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.entityLivingBase = null;
        this.w = false;
    }

    private void handleEventPreMotion(EventPreMotion event, float[] floats) {
        float f = floats[0];
        float f2 = floats[1];
        float f3 = (float)this.horizontalSpeedSetting.computeDouble();
        float f4 = (float)this.verticalSpeedSetting.computeDouble();
        float f5 = this.updateRotation(this.mc.thePlayer.rotationYaw, f, f3);
        float pitch = this.mc.thePlayer.rotationPitch;
        if (this.enableVerticalSetting.isEnabled()) {
            pitch = this.updateRotation(this.mc.thePlayer.rotationPitch, f2, f4);
        }

        float[] floats2 = this.computeFloatArray(new float[]{f5, pitch});
        this.mc.thePlayer.rotationYaw = floats2[0];
        this.mc.thePlayer.rotationPitch = MathHelper.clamp_float(floats2[1], -90.0F, 90.0F);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion) {
                    if (this.mc.thePlayer == null || this.mc.theWorld == null || this.mc.thePlayer.isDead) {
                        return;
                    }

                    this.onEventPreMotion((EventPreMotion)event);
                }
            }
        }
    }

    private float computeFloat(float f, float f2) {
        float f3 = MathHelper.wrapAngleTo180_float(f2 - f);
        return Math.abs(f3);
    }

    private EntityLivingBase sortTargets(List<EntityLivingBase> livings) {
        if (livings.isEmpty()) {
            return null;
        }
        Comparator<EntityLivingBase> comparator;
        if (this.hurttimeSetting.matchesString2("Health")) {
            comparator = Comparator.comparingDouble(EntityLivingBase::getHealth);
            if (this.highSetting.matchesString2("High")) {
                comparator = comparator.reversed();
            }
        } else if (this.hurttimeSetting.matchesString2("FOV")) {
            comparator = Comparator.comparingDouble(living -> this.computeFloat(this.mc.thePlayer.rotationYaw, this.computeFloatArray(living)[0]));
        } else if (this.hurttimeSetting.matchesString2("Armor")) {
            comparator = Comparator.<EntityLivingBase>comparingInt(living2 -> living2 instanceof EntityPlayer ? ((EntityPlayer)living2).getTotalArmorValue() : 0)
                .reversed();
        } else if (this.hurttimeSetting.matchesString2("Hurttime")) {
            if (this.distanceSetting2.matchesString2("Health")) {
                comparator = (living3, living4) -> {
                    boolean flag = living3.hurtTime == 0;
                    boolean flag2 = living4.hurtTime == 0;
                    if (flag != flag2) {
                        return flag ? -1 : 1;
                    }
                    return flag ? Float.compare(living3.getHealth(), living4.getHealth()) : Integer.compare(living3.hurtTime, living4.hurtTime);
                };
            } else {
                comparator = Comparator.comparingInt(living5 -> living5.hurtTime);
            }
        } else {
            comparator = Comparator.comparingDouble(this.mc.thePlayer::getDistanceToEntity);
        }

        comparator = comparator.thenComparingDouble(this.mc.thePlayer::getDistanceToEntity);
        KOS instance = KOS.getInstance();
        boolean flag3 = instance != null && instance.checkState44();
        EntityLivingBase living6 = null;
        boolean flag4 = false;

        for (EntityLivingBase living7 : livings) {
            boolean flag5 = flag3 && living7 instanceof EntityPlayer && WHelper.matchesString(living7.getName());
            if (living6 == null || flag5 && !flag4 || flag5 == flag4 && comparator.compare(living7, living6) < 0) {
                living6 = living7;
                flag4 = flag5;
            }
        }

        return living6;
    }

    public EntityLivingBase getEntityLivingBase() {
        return this.entityLivingBase;
    }

    private float[] computeFloatArray(float[] floats) {
        float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f2 = f * f * f * 1.2F;
        float f3 = floats[0] - this.mc.thePlayer.rotationYaw;
        float f4 = floats[1] - this.mc.thePlayer.rotationPitch;
        float f5 = f3 - f3 % f2;
        float f6 = f4 - f4 % f2;
        return new float[]{this.mc.thePlayer.rotationYaw + f5, this.mc.thePlayer.rotationPitch + f6};
    }

    private void onEventPreMotion(EventPreMotion event) {
        if (this.mc.currentScreen != null) {
            this.entityLivingBase = null;
        } else {
            boolean buttonDown = Mouse.isButtonDown(0);
            if (this.clickAimSetting.isEnabled() && !buttonDown) {
                this.entityLivingBase = null;
                this.w = false;
            } else if (this.checkBlockBreakSetting.isEnabled() && this.mc.playerController.getIsHittingBlock()) {
                this.entityLivingBase = null;
            } else {
                EntityLivingBase living = this.computeEntityLivingBase();
                if (living == null) {
                    this.entityLivingBase = null;
                } else {
                    this.entityLivingBase = living;
                    float[] floats = this.computeFloatArray(living);
                    if (floats != null) {
                        float f = this.computeFloat(this.mc.thePlayer.rotationYaw, floats[0]);
                        if (!(f > this.maxAngleSetting.computeDouble())) {
                            this.handleEventPreMotion(event, floats);
                            this.w = buttonDown;
                        }
                    }
                }
            }
        }
    }

    private EntityLivingBase computeEntityLivingBase() {
        List list = new ArrayList();

        for (Entity entity : this.mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase living
                && !living.isDead
                && living != this.mc.thePlayer
                && !(this.mc.thePlayer.getDistanceToEntity(living) > this.distanceSetting.computeDouble())
                && this.matchesEntityLivingBase2(living)
                && (!living.isInvisible() || ((BooleanSetting)this.targetsSetting.computeSetting("Invisible")).isEnabled())
                && !(living instanceof EntityPlayer player && DefaultHelper.matchesString(player.getName()))
                && (this.throughWallsSetting.isEnabled() || this.matchesEntityLivingBase(living))) {
                float[] floats = this.computeFloatArray(living);
                if (!(this.computeFloat(this.mc.thePlayer.rotationYaw, floats[0]) > this.maxAngleSetting.computeDouble())) {
                    list.add(living);
                }
            }
        }

        return this.sortTargets(list);
    }

    private float updateRotation(float f, float f2, float f3) {
        float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
        float f5 = f3 / 100.0F;
        float f6 = Math.abs(f4) * f5;
        return Math.abs(f4) <= f6 ? f + f4 : f + (f4 > 0.0F ? f6 : -f6);
    }

    private Vec3 computeVec3(Vec3 vec, AxisAlignedBB box) {
        double d = Math.max(box.minX, Math.min(vec.xCoord, box.maxX));
        double d2 = Math.max(box.minY, Math.min(vec.yCoord, box.maxY));
        double d3 = Math.max(box.minZ, Math.min(vec.zCoord, box.maxZ));
        return new Vec3(d, d2, d3);
    }

    public AimAssist() {
        super("AimAssist", 0, Category.COMBAT);
        this.handleSettingArray(
            new Setting[]{
                this.horizontalSpeedSetting,
                this.verticalSpeedSetting,
                this.enableVerticalSetting,
                this.distanceSetting,
                this.maxAngleSetting,
                this.clickAimSetting,
                this.checkBlockBreakSetting,
                this.throughWallsSetting,
                this.hurttimeSetting,
                this.highSetting,
                this.distanceSetting2,
                this.targetsSetting
            }
        );
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.entityLivingBase = null;
            this.w = false;
        }
    }
}
