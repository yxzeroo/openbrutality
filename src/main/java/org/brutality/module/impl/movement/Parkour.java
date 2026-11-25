package org.brutality.module.impl.movement;

import lombok.Generated;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.GetSpeedHelper;

public class Parkour extends Module {
    private final BooleanSetting autoJumpSetting = new BooleanSetting("Auto Jump", this, true);
    private final BooleanSetting edgeDetectionSetting = new BooleanSetting("Edge Detection", this, true);
    private final NumberSetting edgeDistanceSetting = new NumberSetting("Edge Distance", this, 0.3, 0.1, 1.0, 0.1);
    private final BooleanSetting onlyOnGroundSetting = new BooleanSetting("Only On Ground", this, true);
    private final BooleanSetting preventFallDamageSetting = new BooleanSetting("Prevent Fall Damage", this, false);
    private final NumberSetting jumpDelaySetting = new NumberSetting("Jump Delay", this, 0.0, 0.0, 500.0, 1.0);
    public static Parkour instance;
    private long timestamp = 0L;

    @Generated
    public static Parkour getInstance() {
        return instance;
    }

    private boolean checkState() {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        double motionX = mc.thePlayer.motionX;
        double motionZ = mc.thePlayer.motionZ;
        if (Math.abs(motionX) < 0.01 && Math.abs(motionZ) < 0.01) {
            double rad = Math.toRadians(mc.thePlayer.rotationYaw);
            motionX = -Math.sin(rad) * 0.3;
            motionZ = Math.cos(rad) * 0.3;
        }

        double d = x + (motionX > 0.0 ? this.edgeDistanceSetting.computeDouble() : -this.edgeDistanceSetting.computeDouble());
        double d2 = z + (motionZ > 0.0 ? this.edgeDistanceSetting.computeDouble() : -this.edgeDistanceSetting.computeDouble());
        BlockPos pos = new BlockPos(d, y - 1.0, d2);
        BlockPos pos2 = new BlockPos(x, y - 1.0, z);
        boolean flag = !(mc.theWorld.getBlockState(pos2).getBlock() instanceof BlockAir);
        boolean flag2 = mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir;
        return flag && flag2;
    }

    public Parkour() {
        super("Parkour", 0, Category.MOVEMENT);
        this.handleSettingArray(
            new Setting[]{
                this.autoJumpSetting,
                this.edgeDetectionSetting,
                this.edgeDistanceSetting,
                this.onlyOnGroundSetting,
                this.preventFallDamageSetting,
                this.jumpDelaySetting
            }
        );
        instance = this;
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.timestamp = 0L;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion event2) {
                    if (!this.autoJumpSetting.isEnabled()) {
                        return;
                    }

                    if (this.onlyOnGroundSetting.isEnabled() && !mc.thePlayer.onGround) {
                        return;
                    }

                    if (GetSpeedHelper.isInLiquid()) {
                        return;
                    }

                    if (System.currentTimeMillis() - this.timestamp < this.jumpDelaySetting.computeDouble()) {
                        return;
                    }

                    if (this.edgeDetectionSetting.isEnabled() && this.checkState() && mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        this.timestamp = System.currentTimeMillis();
                    }

                    if (this.preventFallDamageSetting.isEnabled() && mc.thePlayer.fallDistance > 2.0F && mc.thePlayer.onGround) {
                        mc.thePlayer.fallDistance = 0.0F;
                    }
                }
            }
        }
    }
}
