package org.brutality.module.impl.combat;

import java.util.Random;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.GetSpeedHelper;

public class Reach extends Module {
    private static Reach instance;
    private final NumberSetting minReachSetting = new NumberSetting("Min Reach", this, 3.0, 3.0, 6.0, 0.1);
    private final NumberSetting maxReachSetting = new NumberSetting("Max Reach", this, 3.2, 3.0, 6.0, 0.1);
    private final BooleanSetting weaponOnlySetting = new BooleanSetting("Weapon Only", this, true);
    private final BooleanSetting sprintOnlySetting = new BooleanSetting("Sprint Only", this, false);
    private final BooleanSetting sneakingOnlySetting = new BooleanSetting("Sneaking Only", this, false);
    private final BooleanSetting jumpingOnlySetting = new BooleanSetting("Jumping Only", this, false);
    private final BooleanSetting movingOnlySetting = new BooleanSetting("Moving Only", this, false);
    private final BooleanSetting playersOnlySetting = new BooleanSetting("Players Only", this, false);
    private final BooleanSetting combatOnlySetting = new BooleanSetting("Combat Only", this, false);
    private final BooleanSetting randomizeSetting = new BooleanSetting("Randomize", this, true);
    private final NumberSetting randomAmountSetting = new NumberSetting("Random Amount", this, 0.1, 0.01, 0.5, 0.01);
    private final BooleanSetting hitSelectSetting = new BooleanSetting("Hit Select", this, false);
    private final BooleanSetting throughWallsSetting = new BooleanSetting("Through Walls", this, false);
    private final NumberSetting wallsReachSetting = new NumberSetting("Walls Reach", this, 3.0, 3.0, 4.0, 0.1);
    private final BooleanSetting keepSprintSetting = new BooleanSetting("Keep-Sprint", this, true);
    private final BooleanSetting legitCheckSetting = new BooleanSetting("Legit Check", this, false);
    private final NumberSetting hitChanceSetting = new NumberSetting("Hit Chance", this, 100.0, 0.0, 100.0, 1.0);
    private final Random random = new Random();
    private long timestamp = 0L;
    private double amount = 3.0;
    private boolean flag = false;
    private Entity entity = null;

    private boolean checkState4() {
        if (mc.thePlayer != null && mc.thePlayer.getHeldItem() != null) {
            Item item = mc.thePlayer.getHeldItem().getItem();
            return item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemPickaxe || item instanceof ItemSpade || item instanceof ItemHoe;
        }
        return false;
    }

    public boolean matchesEntity(Entity entity) {
        if (!this.checkState44() || !this.checkState2()) {
            return false;
        }
        return this.playersOnlySetting.isEnabled() && !(entity instanceof EntityPlayer)
            ? false
            : !(entity instanceof EntityLivingBase living && living.getHealth() <= 0.0F);
    }

    public double getAmount() {
        return this.amount;
    }

    @Generated
    public static Reach getInstance() {
        return instance;
    }

    private void process() {
        Entity entity = this.computeEntity();
        if (entity != null && entity instanceof EntityLivingBase) {
            if (!this.flag || this.entity != entity) {
                this.timestamp = System.currentTimeMillis();
                this.flag = true;
                this.entity = entity;
            }
        } else if (this.flag) {
            long l = System.currentTimeMillis() - this.timestamp;
            if (l > 5000L) {
                this.flag = false;
                this.entity = null;
            }
        }
    }

    public void handleEntity(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            this.timestamp = System.currentTimeMillis();
            this.entity = entity;
            this.flag = true;
        }
    }

    private boolean checkState5() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            Entity entity = this.computeEntity();
            if (entity == null) {
                return false;
            }
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
            Vec3 vec = entity.getPositionVector().addVector(0.0, entity.getEyeHeight() / 2.0F, 0.0);
            MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(positionEyes, vec, false, true, false);
            return hit != null && hit.typeOfHit == MovingObjectType.BLOCK;
        }
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.playerController != null) {
            mc.playerController.setBlockReachDistance(4.5F);
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.process3();
        }
    }

    public boolean matchesEntity(Entity entity, double d) {
        if (!this.checkState44()) {
            return d <= 4.0;
        }
        if (!this.checkState2()) {
            return d <= 4.0;
        }
        if (this.hitSelectSetting.isEnabled()) {
            Entity entity2 = this.computeEntity();
            if (entity2 != entity) {
                return d <= 4.0;
            }
        }

        return d <= this.getAmount();
    }

    public Reach() {
        super("Reach", 0, Category.COMBAT);
        this.handleSettingArray(
            new Setting[]{
                this.minReachSetting,
                this.maxReachSetting,
                this.weaponOnlySetting,
                this.sprintOnlySetting,
                this.sneakingOnlySetting,
                this.jumpingOnlySetting,
                this.movingOnlySetting,
                this.playersOnlySetting,
                this.combatOnlySetting,
                this.randomizeSetting,
                this.randomAmountSetting,
                this.hitSelectSetting,
                this.throughWallsSetting,
                this.wallsReachSetting,
                this.keepSprintSetting,
                this.legitCheckSetting,
                this.hitChanceSetting
            }
        );
        instance = this;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate) {
                    if (mc.thePlayer == null || mc.theWorld == null) {
                        return;
                    }

                    if (this.checkState2()) {
                        this.process3();
                    }

                    this.process();
                }

                if (event instanceof GetDamageEvent
                    && ((GetDamageEvent)event).getEntity() == mc.thePlayer
                    && ((GetDamageEvent)event).getEntity2() instanceof EntityLivingBase) {
                    this.timestamp = System.currentTimeMillis();
                    this.entity = ((GetDamageEvent)event).getEntity2();
                    this.flag = true;
                    this.process3();
                }
            }
        }
    }

    private void process3() {
        double d = this.minReachSetting.computeDouble();
        double d2 = Math.max(this.maxReachSetting.computeDouble(), d);
        if (this.randomizeSetting.isEnabled()) {
            double d3 = d + (d2 - d) * this.random.nextDouble();
            double d4 = (this.random.nextDouble() - 0.5) * 2.0 * this.randomAmountSetting.computeDouble();
            this.amount = Math.max(3.0, Math.min(6.0, d3 + d4));
        } else {
            this.amount = d2;
        }

        if (this.throughWallsSetting.isEnabled() && this.checkState5()) {
            this.amount = Math.min(this.amount, this.wallsReachSetting.computeDouble());
        }

        if (this.legitCheckSetting.isEnabled()) {
            this.amount = Math.min(this.amount, 3.8);
        }

        if (mc.playerController != null) {
            mc.playerController.setBlockReachDistance((float)this.amount);
        }
    }

    public boolean checkState() {
        return this.checkState44() && this.checkState2();
    }

    private boolean checkState2() {
        if (mc.thePlayer == null) {
            return false;
        }
        if (this.weaponOnlySetting.isEnabled() && !this.checkState4()) {
            return false;
        }
        if (this.sprintOnlySetting.isEnabled() && !mc.thePlayer.isSprinting()) {
            return false;
        }
        if (this.sneakingOnlySetting.isEnabled() && !mc.thePlayer.isSneaking()) {
            return false;
        }
        if (this.jumpingOnlySetting.isEnabled() && mc.thePlayer.onGround) {
            return false;
        }
        if (this.movingOnlySetting.isEnabled() && !GetSpeedHelper.isMoving()) {
            return false;
        }
        if (this.combatOnlySetting.isEnabled()) {
            long l = System.currentTimeMillis() - this.timestamp;
            if (l > 3000L) {
                return false;
            }
        }

        if (this.playersOnlySetting.isEnabled()) {
            Entity entity = this.computeEntity();
            if (!(entity instanceof EntityPlayer)) {
                return false;
            }
        }

        if (this.hitChanceSetting.computeDouble() < 100.0) {
            double d = this.random.nextDouble() * 100.0;
            if (d > this.hitChanceSetting.computeDouble()) {
                return false;
            }
        }

        return true;
    }

    public String getDisplayName() {
        return this.checkState44() && this.checkState2() ? this.getName() + " " + String.format("%.1f", this.getAmount()) : this.getName();
    }

    private Entity computeEntity() {
        return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY ? mc.objectMouseOver.entityHit : null;
    }
}
