package org.brutality.module.impl.movement;

import java.util.Arrays;
import lombok.Generated;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.GetSpeedHelper;
import org.brutality.utils.player.PlayerUtils;

public class Step extends Module {
    private final ModeSetting ncpSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "NCP"));
    private final NumberSetting heightSetting = new NumberSetting("Height", this, 1.0, 0.6, 5.0, 0.1);
    private final NumberSetting timerSetting = new NumberSetting("Timer", this, 0.25, 0.1, 1.0, 0.01);
    private final BooleanSetting reverseSetting = new BooleanSetting("Reverse", this, false);
    public static Step instance;
    private boolean flag = false;

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.stepHeight = 0.6F;
        mc.timer.timerSpeed = 1.0F;
        this.flag = false;
    }

    public Step() {
        super("Step", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.ncpSetting, this.heightSetting, this.timerSetting, this.reverseSetting});
        instance = this;
    }

    private void onEventPreMotion2(EventPreMotion event) {
        mc.thePlayer.stepHeight = 0.5F;
        if (mc.thePlayer.onGround) {
            this.flag = false;
        }

        if (mc.thePlayer.isCollidedHorizontally) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.42F;
            }

            this.flag = true;
        }

        if (mc.thePlayer.offGroundTicks == 4 && this.flag) {
            mc.thePlayer.motionY = -0.09800000190734863;
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.flag = false;
        }
    }

    @Generated
    public static Step getInstance() {
        return instance;
    }

    private void onEventPreMotion(EventPreMotion event) {
        mc.thePlayer.stepHeight = (float)this.heightSetting.computeDouble();
        if (this.reverseSetting.isEnabled()) {
            if (!PlayerUtils.matchesDouble(this.heightSetting.computeDouble() + mc.thePlayer.getEyeHeight()) || GetSpeedHelper.isInLiquid()) {
                return;
            }

            if (mc.thePlayer.posY < mc.thePlayer.lastGroundY && !mc.thePlayer.onGround && mc.thePlayer.offGroundTicks <= 1) {
                mc.thePlayer.motionY = -this.heightSetting.computeDouble();
            }

            if (mc.thePlayer.offGroundTicks == 1 && mc.thePlayer.posY < mc.thePlayer.lastLastGroundY) {
                mc.timer.timerSpeed = (float)this.timerSetting.computeDouble();
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion event2) {
                    if (this.ncpSetting.matchesString2("Normal")) {
                        this.onEventPreMotion(event2);
                    } else if (this.ncpSetting.matchesString2("NCP")) {
                        this.onEventPreMotion2(event2);
                    }
                }
            }
        }
    }
}
