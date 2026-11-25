package org.brutality.module.impl.movement;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class Timer extends Module {
    private final ModeSetting randomizedSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Randomized"));
    private final NumberSetting speedSetting = new NumberSetting("Speed", this, 1.0, 0.0, 5.0, 0.1)
        .computeNumberSetting(() -> this.randomizedSetting.matchesString2("Normal"));
    private final NumberSetting minSpeedSetting = new NumberSetting("Min Speed", this, 1.0, 0.0, 5.0, 0.1)
        .computeNumberSetting(() -> this.randomizedSetting.matchesString2("Randomized"));
    private final NumberSetting maxSpeedSetting = new NumberSetting("Max Speed", this, 1.5, 0.0, 5.0, 0.1)
        .computeNumberSetting(() -> this.randomizedSetting.matchesString2("Randomized"));
    private final NumberSetting randomizeIntervalSetting = new NumberSetting("Randomize Interval", this, 1.0, 1.0, 20.0, 1.0)
        .computeNumberSetting(() -> this.randomizedSetting.matchesString2("Randomized"));
    private final BooleanSetting horizontalOnlySetting = new BooleanSetting("Horizontal Only", this, false);
    private final BooleanSetting verticalOnlySetting = new BooleanSetting("Vertical Only", this, false);
    private final BooleanSetting onGroundOnlySetting = new BooleanSetting("On Ground Only", this, false);
    private final BooleanSetting ignoreInLiquidSetting = new BooleanSetting("Ignore In Liquid", this, true);
    private final BooleanSetting onlyWhileSprintingSetting = new BooleanSetting("Only While Sprinting", this, false);
    private final BooleanSetting pauseOnHurtSetting = new BooleanSetting("Pause On Hurt", this, false);
    private final NumberSetting activationDelaySetting = new NumberSetting("Activation Delay", this, 0.0, 0.0, 100.0, 1.0);
    private final BooleanSetting maximumTicksSetting = new BooleanSetting("Maximum Ticks", this, false);
    private final NumberSetting maxTicksSetting = new NumberSetting("Max Ticks", this, 100.0, 1.0, 1000.0, 1.0)
        .computeNumberSetting(() -> this.maximumTicksSetting.isEnabled());
    private int count;
    private int count2;
    private int count3;
    private float r;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    this.count2++;
                    if (this.count2 <= (int)this.activationDelaySetting.computeDouble()) {
                        Minecraft.getMinecraft().timer.timerSpeed = 1.0F;
                        return;
                    }

                    if (this.checkState()) {
                        Minecraft.getMinecraft().timer.timerSpeed = this.computeFloat();
                        if (this.maximumTicksSetting.isEnabled() && ++this.count >= (int)this.maxTicksSetting.computeDouble()) {
                            this.process28();
                        }
                    } else {
                        Minecraft.getMinecraft().timer.timerSpeed = 1.0F;
                    }
                }
            }
        }
    }

    public Timer() {
        super("Timer", 0, Category.MOVEMENT);
        this.handleSettingArray(
            new Setting[]{
                this.randomizedSetting,
                this.speedSetting,
                this.minSpeedSetting,
                this.maxSpeedSetting,
                this.randomizeIntervalSetting,
                this.horizontalOnlySetting,
                this.verticalOnlySetting,
                this.onGroundOnlySetting,
                this.ignoreInLiquidSetting,
                this.onlyWhileSprintingSetting,
                this.pauseOnHurtSetting,
                this.activationDelaySetting,
                this.maximumTicksSetting,
                this.maxTicksSetting
            }
        );
    }

    private float computeFloat() {
        if (this.randomizedSetting.matchesString2("Randomized")) {
            if (this.count3 <= 0) {
                double d = this.minSpeedSetting.computeDouble();
                double d2 = this.maxSpeedSetting.computeDouble();
                if (d2 < d) {
                    double d3 = d;
                    d = d2;
                    d2 = d3;
                }

                this.r = Math.max(0.0F, (float)(d + Math.random() * (d2 - d)));
                this.count3 = Math.max(1, (int)this.randomizeIntervalSetting.computeDouble());
            }

            this.count3--;
            return this.r;
        }
        return Math.max(0.0F, (float)this.speedSetting.computeDouble());
    }

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().timer.timerSpeed = 1.0F;
        this.count = 0;
        this.count2 = 0;
        this.count3 = 0;
    }

    private boolean checkState() {
        if (mc.thePlayer == null) {
            return false;
        }
        boolean flag = mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F;
        boolean flag2 = !mc.thePlayer.onGround;
        if (!this.horizontalOnlySetting.isEnabled() || mc.thePlayer.onGround && flag) {
            if (this.verticalOnlySetting.isEnabled() && !flag2) {
                return false;
            }
            if (this.onGroundOnlySetting.isEnabled() && !mc.thePlayer.onGround) {
                return false;
            }
            if (!this.ignoreInLiquidSetting.isEnabled() || !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
                return this.onlyWhileSprintingSetting.isEnabled() && !mc.thePlayer.isSprinting()
                    ? false
                    : !this.pauseOnHurtSetting.isEnabled() || mc.thePlayer.hurtTime <= 0;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            this.count = 0;
            this.count2 = 0;
            this.count3 = 0;
            this.r = 0.0F;
            Minecraft.getMinecraft().timer.timerSpeed = this.computeFloat();
        }
    }
}
